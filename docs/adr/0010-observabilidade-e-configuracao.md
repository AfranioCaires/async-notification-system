# ADR 0010 — Logs estruturados e configuração externalizada

## Contexto

É preciso seguir um alerta pelos dois serviços em uma ferramenta de logs, e o mesmo artefato deve rodar em qualquer ambiente sem recompilar.

## Decisão

**Logs**

- Logging estruturado nativo do Spring Boot (`logging.structured.format.console: ecs`).
- Micrometer Tracing com bridge Brave: `traceId` e `spanId` entram no MDC automaticamente, e a observação do `KafkaTemplate` e do listener propaga o contexto pelo header `traceparent` (W3C). A requisição HTTP e o processamento no consumidor compartilham o mesmo `traceId`.
- `correlationId`, o identificador de negócio do alerta, é colocado no MDC pelo publisher e pelo listener/recoverer, a partir do payload.
- Actuator expõe `health` (com probes `liveness`/`readiness`), `info` e `metrics`; detalhes de health não são expostos publicamente.

**Configuração**

- Nenhum segredo no código ou no `application.yml`: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `KAFKA_BOOTSTRAP_SERVERS` são obrigatórios via ambiente.
- Nomes de tópicos e clientes com falha simulada vêm de variáveis de ambiente. Qualquer outra propriedade pode ser sobrescrita pelo relaxed binding do Spring (ex.: `SERVER_PORT`, `LOGGING_LEVEL_ROOT`, `APP_KAFKA_RETRY_INTERVAL`), sem placeholders criados à mão.
- Profile `local` com defaults para rodar a aplicação na IDE contra a infraestrutura do Compose.
- `.env.example` documenta as variáveis; `.env` fica fora do Git.

## Consequências

- Busca por `correlationId` ou `traceId` retorna a jornada completa: publicação na API, consumo, estratégia, retries e DLQ.
- Para visualizar os traces basta adicionar um exporter (Zipkin ou OTLP); a instrumentação já existe.
- Os defaults de senha do `docker-compose.yml` servem apenas para ambiente local; em produção os segredos vêm de um secret manager.
