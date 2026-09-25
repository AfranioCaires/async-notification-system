# ADR 0002 — Clean Architecture + DDD tático em cada serviço

## Contexto

O envio de notificações hoje é acoplado ao monólito e ao framework. Queremos regras de negócio (resolução de template, ciclo de vida da notificação, idempotência) testáveis sem Spring, Kafka ou banco, e trocar detalhes de infraestrutura sem reescrever o núcleo.

## Decisão

Cada serviço é organizado em quatro camadas, com dependências apontando somente para dentro:

```
presentation ─┐
              ├──► application ──► domain
infrastructure┘
```

| Camada | Responsabilidade | Pode depender de |
|--------|------------------|------------------|
| `domain` | Agregados, value objects, eventos de domínio, serviços de domínio e interfaces de repositório. Java puro. | nada |
| `application` | Use cases (um por intenção de negócio), commands/queries, views e portas de saída (`AlertPublisher`, `AlertDeliveryStrategy`). Java puro. | `domain` |
| `infrastructure` | Adaptadores: JPA, Kafka, estratégias de entrega, wiring de beans (`UseCaseConfiguration`). | `application`, `domain` |
| `presentation` | Controllers REST, DTOs HTTP, tratamento de erros, filtros e documentação OpenAPI. | `application`, `domain` |

Dentro de cada camada, o código é organizado por feature e, dentro dela, em subpastas por tipo de classe (`model`, `event`, `exception`, `repository`, `service`, `usecase`, `command`, `query`, `dto`, `controller`, `request`, `response`). Na infraestrutura, `persistence`, `messaging` e `delivery` ficam sem subpastas: seus detalhes internos (entidades JPA, mappers, mensagens Kafka, adaptadores) colaboram entre si e ficam package-private, com o encapsulamento garantido pelo compilador. A árvore completa está em [`docs/architecture.md`](../architecture.md).

Elementos de DDD tático utilizados:

- **Agregado** `Channel` protege invariantes (canal inativo/excluído não aceita disparo; template renderizado exige todos os parâmetros).
- **Value objects** imutáveis como `record`: `ChannelId`, `Template`, `ChannelConfig`, `ChannelDefinition`, `CorrelationId`, `ClientId`.
- **Evento de domínio** `AlertRequested`, produzido pelo agregado em `Channel.requestAlert(...)`.
- **Serviço de domínio** `ChannelUniquenessPolicy` para a regra de unicidade que depende do repositório.
- **Agregado de leitura** `NotificationHistory` no `alert-processor`, que deriva o estado atual a partir dos eventos.

Use cases são classes sem anotações de framework, com um único método `execute`, e recebem `Clock` para tornar o tempo determinístico. O Spring só aparece na borda: os beans são montados em `infrastructure/config/UseCaseConfiguration`.

A regra de dependência é verificada automaticamente por testes ArchUnit (`ArchitectureTest`), que também proíbem `org.springframework`, `jakarta`, `jackson`, `kafka` e `hibernate` dentro de `domain` e `application`.

## Consequências

- Domínio e use cases são testados com JUnit + Mockito puros, em milissegundos.
- Trocar Kafka por outro broker ou MySQL por outro banco afeta apenas `infrastructure`.
- Só é público o que atravessa pastas por ser contrato: DTOs HTTP, commands, dtos de saída dos use cases e portas. Detalhes de infraestrutura nunca ficam visíveis fora do próprio pacote.
- Há mais tipos (DTO HTTP → command → domínio → view → DTO HTTP). É um custo aceito em troca de fronteiras claras; mapeamentos ficam em métodos estáticos pequenos (`from`, `toData`, `toCommand`).
- Use cases são classes concretas, sem interface de "input port". Com um único adaptador de entrada por use case, a interface extra não traria benefício; se surgir um segundo adaptador (ex.: gRPC), ela pode ser extraída sem impacto.
