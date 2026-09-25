# ADR 0004 — Contrato de mensageria no Kafka

## Contexto

Os serviços precisam trocar o pedido de alerta de forma durável, ordenada quando fizer sentido e evolutiva, sem acoplar classes Java entre eles.

## Decisão

- Tópico `notifications.alerts.requested` (6 partições) e DLQ `notifications.alerts.requested.dlt`, criados pelo serviço `kafka-init` no Compose (`auto.create.topics.enable=false`). Tópicos são infraestrutura: as aplicações não os criam.
- **Payload JSON** com `JsonSerializer`/`JsonDeserializer` do Spring Kafka, com `spring.json.add.type.headers=false` no produtor e `spring.json.value.default.type` no consumidor. Assim, nomes de classes Java não vazam para o contrato e cada serviço tem seu próprio record da mensagem.
- **Chave da mensagem = `clientId`**: preserva a ordem dos alertas de um mesmo cliente e distribui carga entre partições.
- **Headers**: `correlationId`, `eventType=AlertRequested.v1` (versionamento do contrato) e `traceparent`, adicionado automaticamente pelo Micrometer Tracing (ver ADR 0010).
- O produtor usa os padrões do Kafka 3.x (`acks=all` e idempotência habilitada). A API aguarda a confirmação do broker, com timeout de 5s, antes de responder `202`. Se o broker não confirmar, a API responde `503` e o cliente pode repetir com segurança.
- O `JsonDeserializer` ignora campos desconhecidos, o que permite evoluir o contrato de forma aditiva.

Payload:

```json
{
  "correlationId": "uuid",
  "channelId": "uuid",
  "channelType": "EMAIL",
  "clientId": 12345,
  "message": "Olá João Silva, sua fatura de Novembro/2025 está disponível.",
  "priority": "HIGH",
  "maxRetries": 3,
  "requestedAt": "2025-11-10T14:30:00Z"
}
```

## Consequências

- O `202` só é devolvido quando a mensagem está durável no broker.
- A mensagem já sai com o texto renderizado: o processador não precisa conhecer templates nem consultar o `notification-api`.
- Mudanças incompatíveis exigem novo `eventType` (`AlertRequested.v2`) e período de convivência.
- Não há Outbox: como a API não grava estado ao disparar, não existe dual write a proteger. Se no futuro o disparo passar a persistir algo, o padrão Transactional Outbox deve ser adotado.
