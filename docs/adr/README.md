# Architecture Decision Records

Registro das decisões arquiteturais da solução de notificações assíncronas. Cada ADR segue o formato de Michael Nygard: Contexto, Decisão, Consequências.

| # | Decisão |
|---|---------|
| [0001](0001-registro-de-decisoes-arquiteturais.md) | Registrar decisões arquiteturais em ADRs |
| [0002](0002-clean-architecture-e-ddd.md) | Clean Architecture + DDD tático em cada serviço |
| [0003](0003-microsservicos-e-banco-por-servico.md) | Dois microsserviços com banco por serviço |
| [0004](0004-contrato-de-mensageria-kafka.md) | Contrato de mensageria no Kafka |
| [0005](0005-event-store-append-only.md) | Event store append-only para o ciclo de vida |
| [0006](0006-resiliencia-retry-dlq-idempotencia.md) | Retry, DLQ e consumo idempotente |
| [0007](0007-strategy-para-canais-de-entrega.md) | Strategy para entrega por tipo de canal |
| [0008](0008-exclusao-logica-e-unicidade-de-canais.md) | Exclusão lógica e unicidade de canais |
| [0009](0009-api-http-erros-e-openapi.md) | API HTTP, Problem Details e OpenAPI |
| [0010](0010-observabilidade-e-configuracao.md) | Logs estruturados e configuração externalizada |
| [0011](0011-ci-e-empacotamento.md) | CI no GitHub Actions e imagens Docker em camadas |

Para propor uma nova decisão, copie o ADR mais recente, incremente o número e abra um PR. ADRs existentes não são editados: uma mudança de rumo gera um novo ADR que substitui o anterior.
