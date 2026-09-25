# Notificações Assíncronas

Dois microsserviços Java 21 / Spring Boot que recebem pedidos de disparo em alto volume, processam de forma desacoplada
via Kafka e mantêm rastreabilidade ponta a ponta em um event store append-only.

| Serviço            | Porta | Responsabilidade                                                           |
|--------------------|-------|----------------------------------------------------------------------------|
| `notification-api` | 8080  | CRUD de canais de alerta e disparo (`202 Accepted` + `correlationId`)      |
| `alert-processor`  | 8081  | Consumo, estratégia por canal, retry/DLQ, event store e consulta de status |

Stack: Java 21 · Spring Boot 3.5 · Spring Kafka · MySQL 8.4 · Flyway · Micrometer Tracing · springdoc-openapi · JUnit
5 + Mockito + ArchUnit · Docker Compose · GitHub Actions.

Documentação complementar:

- [Arquitetura e diagramas](docs/architecture.md)
- [ADRs: decisões e trade-offs](docs/adr/README.md)

---

## Como executar

Pré-requisito: Docker com Compose v2.

```bash
cp .env.example .env
docker compose up -d --build
```

O Compose sobe MySQL, Kafka (KRaft), cria os tópicos (`kafka-init`) e inicia os dois serviços após os healthchecks. Na
primeira execução o build das imagens leva alguns minutos.

```bash
docker compose ps
```

Para incluir o Kafka UI (http://localhost:8090):

```bash
docker compose --profile tools up -d
```

Para derrubar tudo, incluindo os volumes:

```bash
docker compose down -v
```

### Swagger

- notification-api: http://localhost:8080/swagger-ui.html
- alert-processor: http://localhost:8081/swagger-ui.html

### Rodando os testes

```bash
./mvnw verify
./mvnw -pl notification-api verify
```

### Rodando um serviço pela IDE

Suba só a infraestrutura com `docker compose up -d mysql kafka kafka-init` e rode a aplicação com o profile `local`
(`SPRING_PROFILES_ACTIVE=local`). O profile aponta para `localhost:3306` e `localhost:9094`.

---

## Roteiro de demonstração

Com o ambiente no ar, a demonstração é feita pelo Swagger UI dos dois serviços.

1. No Swagger do `notification-api` (http://localhost:8080/swagger-ui.html), crie um canal em `POST /channels`:

   ```json
   {
     "name": "Fatura Disponível",
     "type": "EMAIL",
     "template": "Olá {{clientName}}, sua fatura de {{billingMonth}} está disponível.",
     "config": { "maxRetries": 3, "priority": "HIGH" }
   }
   ```

2. Com o `id` retornado, dispare um alerta em `POST /channels/{channelId}/alerts`. A resposta `202` traz o `correlationId`:

   ```json
   { "clientId": 12345, "params": { "clientName": "João Silva", "billingMonth": "Novembro/2025" } }
   ```

3. No Swagger do `alert-processor` (http://localhost:8081/swagger-ui.html), consulte `GET /alerts/{correlationId}/status`. O status atual será `PROCESSADO`, com os eventos `RECEBIDO`, `PROCESSANDO` e `PROCESSADO`.

Falha, retry e DLQ: repita o passo 2 com `"clientId": 99999` (configurável em `DELIVERY_FAILING_CLIENT_IDS`), que simula recusa do provedor. O alerta é retentado `maxRetries` vezes e, após alguns segundos, a consulta mostra `FALHA` com o motivo no `detail`. A mensagem vai para a DLQ:

```bash
docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 --topic notifications.alerts.requested.dlt --from-beginning --timeout-ms 5000
```

**Logs com `correlationId`:**

```bash
docker compose logs notification-api alert-processor | grep <correlationId>
```

**Event store imutável:**

```bash
docker compose exec mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" alert_processor_db \
  -e "UPDATE notification_events SET detail = \"x\" LIMIT 1"'
```

O banco recusa com `notification_events is append-only`.

---

## Endpoints

### notification-api (`:8080`)

| Método   | URL                            | Descrição                                           | Sucesso            |
|----------|--------------------------------|-----------------------------------------------------|--------------------|
| `POST`   | `/channels`                    | Cria canal                                          | `201` + `Location` |
| `GET`    | `/channels?page=0&size=20`     | Lista canais não excluídos (paginado, `size` ≤ 100) | `200`              |
| `GET`    | `/channels/{id}`               | Busca canal                                         | `200`              |
| `PUT`    | `/channels/{id}`               | Atualiza canal (substituição completa)              | `200`              |
| `DELETE` | `/channels/{id}`               | Exclusão lógica                                     | `204`              |
| `POST`   | `/channels/{channelId}/alerts` | Dispara alerta                                      | `202`              |

Criar/atualizar canal (`active` é opcional no `POST`, com default `true`, e obrigatório no `PUT`):

```json
{
    "name": "Fatura Disponível",
    "type": "EMAIL",
    "template": "Olá {{clientName}}, sua fatura de {{billingMonth}} está disponível.",
    "config": {
        "maxRetries": 3,
        "priority": "HIGH"
    },
    "active": true
}
```

- `type`: `EMAIL` | `SMS` | `PUSH`
- `config.maxRetries`: 0 a 10, usado pelo processador como limite de retentativas
- `config.priority`: `LOW` | `MEDIUM` | `HIGH`

Resposta do canal:

```json
{
    "id": "33abb9e6-a3cb-44b6-b07d-805d0de59bd2",
    "name": "Fatura Disponível",
    "type": "EMAIL",
    "template": "Olá {{clientName}}, sua fatura de {{billingMonth}} está disponível.",
    "config": {
        "maxRetries": 3,
        "priority": "HIGH"
    },
    "active": true,
    "createdAt": "2025-11-10T14:30:00Z",
    "updatedAt": "2025-11-10T14:30:00Z"
}
```

Disparo:

```json
{
    "clientId": 12345,
    "params": {
        "clientName": "João Silva",
        "billingMonth": "Novembro/2025"
    }
}
```

```json
{
    "correlationId": "9fa01fc6-3f73-4da7-8d22-24f387a6da03"
}
```

### alert-processor (`:8081`)

| Método | URL                              | Descrição                                            |
|--------|----------------------------------|------------------------------------------------------|
| `GET`  | `/alerts/{correlationId}/status` | Status atual (derivado do último evento) e histórico |

```json
{
    "correlationId": "9fa01fc6-3f73-4da7-8d22-24f387a6da03",
    "channelType": "EMAIL",
    "clientId": 12345,
    "currentStatus": "PROCESSADO",
    "events": [
        {
            "status": "RECEBIDO",
            "detail": "Mensagem consumida do tópico",
            "occurredAt": "2025-11-10T14:30:01Z"
        },
        {
            "status": "PROCESSANDO",
            "detail": "Iniciando processamento via estratégia EMAIL",
            "occurredAt": "2025-11-10T14:30:02Z"
        },
        {
            "status": "PROCESSADO",
            "detail": "Processamento concluído com sucesso",
            "occurredAt": "2025-11-10T14:30:03Z"
        }
    ]
}
```

### Erros

Todos os erros seguem o RFC 9457 (Problem Details), com um `code` estável:

```json
{
    "type": "urn:ubisafe:problem:missing-template-params",
    "title": "Parâmetros do template ausentes",
    "status": 422,
    "detail": "Parâmetros obrigatórios do template ausentes: billingMonth",
    "instance": "/channels/33abb9e6-.../alerts",
    "code": "missing-template-params",
    "missingParams": [
        "billingMonth"
    ],
    "timestamp": "2026-09-25T05:02:26.715Z"
}
```

| Status | `code`                                                   |
|--------|----------------------------------------------------------|
| 400    | `validation-error` (com `errors[]`), `malformed-request` |
| 404    | `channel-not-found`, `alert-not-found`                   |
| 409    | `channel-already-exists`                                 |
| 422    | `channel-inactive`, `missing-template-params`            |
| 503    | `alert-publication-unavailable`                          |
| 500    | `internal-error`                                         |

Operação: `GET /actuator/health/liveness`, `/actuator/health/readiness`, `/actuator/metrics`.

---

## Estrutura

```
.
├── notification-api/          domain · application · infrastructure · presentation
├── alert-processor/           domain · application · infrastructure · presentation
├── infra/mysql/init/          criação dos schemas e usuários por serviço
├── docs/                      arquitetura, ADRs e boas práticas
├── .github/workflows/ci.yml   testes, build de imagens e validação do Compose
├── docker-compose.yml
└── pom.xml                    agregador (build único)
```

---

## Decisões de design e trade-offs

Resumo; o detalhamento está nos [ADRs](docs/adr/README.md).

| Decisão                                                     | Motivo                                                     | Trade-off                                                                        |
|-------------------------------------------------------------|------------------------------------------------------------|----------------------------------------------------------------------------------|
| Clean Architecture + DDD, verificada por ArchUnit           | Núcleo testável sem framework; infraestrutura substituível | Mais tipos e mapeamentos entre camadas                                           |
| Banco por serviço, integração só via Kafka                  | Autonomia de deploy e de escala                            | Enum `ChannelType` duplicado nos dois serviços                                   |
| Template renderizado na API                                 | O processador não depende do cadastro de canais            | Mudanças no template não afetam mensagens já publicadas (comportamento desejado) |
| `202` só após o ack do broker (`acks=all`)                  | Não perder alertas aceitos                                 | Latência do disparo inclui a escrita no Kafka                                    |
| Chave = `clientId`                                          | Ordem garantida por cliente                                | Clientes muito ativos podem concentrar carga em uma partição                     |
| Event store append-only (domínio + `@Immutable` + triggers) | Auditoria e rastreabilidade                                | Correções exigem eventos compensatórios                                          |
| `UNIQUE (correlation_id, status)` + checagem do histórico   | Consumo idempotente                                        | Tentativas intermediárias ficam só nos logs                                      |
| `maxRetries` do canal via header                            | Política de retry por canal, sem consultar a API           | Retry bloqueante segura a partição durante o backoff                             |
| Strategy + resolver com `EnumMap`                           | Novo canal sem alterar o use case                          | Tipo novo exige deploy coordenado dos dois serviços                              |
| Exclusão lógica + unique com coluna gerada                  | Nome reutilizável após exclusão, sem corrida               | Específico de MySQL                                                              |
| Entrega simulada com clientes configurados para falhar      | Demonstra retry e DLQ sem provedores reais                 | Não exercita integrações reais                                                   |

Fora do escopo, com a evolução recomendada:

- **Autenticação/autorização:** OAuth2/JWT no API Gateway ou Spring Security como resource server.
- **Visualização de traces:** exporter Zipkin/OTLP (o `traceId` já é gerado e propagado).
- **Testes de integração com Testcontainers** para MySQL e Kafka no CI.
- **Retry não bloqueante** (`@RetryableTopic`) caso os backoffs precisem ser longos.
- **Kafka com replicação ≥ 3 e TLS/SASL** em produção.
