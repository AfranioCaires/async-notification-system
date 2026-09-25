# ADR 0006 — Retry, DLQ e consumo idempotente

## Contexto

Provedores de e-mail, SMS e push falham de forma transitória. O Kafka entrega mensagens *at-least-once*: o mesmo alerta pode ser consumido mais de uma vez (rebalance, reprocessamento, reenvio manual).

## Decisão

**Retry**

- `DefaultErrorHandler` do Spring Kafka com retry bloqueante e `FixedBackOff`.
- O número de tentativas vem do `maxRetries` do canal, que viaja no payload. `RetryBackOffPolicy` o lê de cada mensagem via `setBackOffFunction`; se a mensagem não pôde ser lida, usa `app.kafka.default-max-retries`. O intervalo fica em `app.kafka.retry-interval`.
- Exceções não retentáveis vão direto para a DLQ:
  - `DeserializationException`: payload ilegível, capturado pelo `ErrorHandlingDeserializer` e já tratado como não retentável pelo Spring Kafka;
  - `DomainValidationException`: mensagem que viola invariantes do domínio;
  - `UnsupportedChannelTypeException`: tipo sem estratégia registrada.

**DLQ**

- Esgotadas as tentativas, `AlertFailureRecoverer`:
  1. registra o evento `FALHA` com o motivo, via `RecordAlertFailureUseCase`, quando a mensagem é legível;
  2. publica a mensagem em `notifications.alerts.requested.dlt` via `DeadLetterPublishingRecoverer`, que preserva os headers originais e adiciona os de diagnóstico (`kafka_dlt-exception-*`). Mensagens ilegíveis são publicadas com os bytes originais, por um template com `ByteArraySerializer`.
- Se o registro de `FALHA` falhar (ex.: banco fora do ar), a recuperação falha e o Spring Kafka reentrega a mensagem, sem perda.

**Idempotência**

- `ProcessAlertUseCase` carrega o histórico antes de agir:
  - histórico concluído (`PROCESSADO` ou `FALHA`) → retorna `ALREADY_CONCLUDED` sem efeitos;
  - só adiciona `RECEBIDO`/`PROCESSANDO` se ainda não existirem, então um retry retoma de onde parou.
- A `UNIQUE (correlation_id, status)` é a barreira final contra corridas: `append` trata a violação de unicidade como "já registrado" e retorna `false`.
- Offsets são confirmados por registro (`ack-mode: record`) e só após o processamento.

## Consequências

- Reprocessar a mesma mensagem não gera duplicidade no banco (verificado reenviando uma mensagem já processada ao tópico).
- A garantia de entrega no provedor externo é *at-least-once*: se o processo cair entre a entrega e o `append(PROCESSADO)`, o provedor pode receber a notificação duas vezes. Para eliminar isso seria preciso chave de idempotência no provedor (o `correlationId` já serve para isso).
- O retry bloqueante segura a partição durante o backoff. Com poucos retries curtos isso é aceitável; para backoffs longos, migrar para *non-blocking retries* (`@RetryableTopic`).
- Para demonstrar a DLQ sem provedores reais, `DELIVERY_FAILING_CLIENT_IDS` define clientes cuja entrega simulada sempre falha.
