# ADR 0005 — Event store append-only para o ciclo de vida

## Contexto

É preciso rastrear de ponta a ponta cada notificação (`RECEBIDO → PROCESSANDO → PROCESSADO | FALHA`) sem nunca alterar registros já gravados.

## Decisão

- Tabela `notification_events` no `alert-processor`, onde cada mudança de status é um novo registro imutável.
- A imutabilidade é garantida em três níveis:
  1. **Domínio:** `NotificationEvent` é um `record` imutável e o repositório só expõe `append` e `historyOf`.
  2. **ORM:** a entidade JPA é `@Immutable`, com colunas `updatable = false`, e implementa `Persistable.isNew() = true` para sempre gerar `INSERT`.
  3. **Banco:** triggers `BEFORE UPDATE` e `BEFORE DELETE` abortam qualquer alteração com `SQLSTATE 45000`.
- **O estado atual é derivado** por `NotificationHistory`: os eventos são ordenados por `occurredAt` e, em caso de empate, pela ordem do ciclo de vida; o status atual é o do último evento.
- Restrição `UNIQUE (correlation_id, status)`: cada estado ocorre no máximo uma vez por notificação, o que também sustenta a idempotência (ver ADR 0006).
- O schema é versionado com Flyway; o Hibernate não gera DDL.

## Consequências

- Auditoria completa e reconstrução do estado em qualquer momento.
- Tentativas intermediárias de retry não geram eventos próprios (ficam nos logs); só os marcos do ciclo de vida são persistidos. É uma escolha deliberada para manter o histórico legível e o consumo idempotente.
- Criar triggers exige `log_bin_trust_function_creators=1` (ou privilégio equivalente) quando o binlog está ativo. No Compose isso é configurado no MySQL; em produção deve ser tratado pelo time de banco ou a migração executada por um usuário de DDL separado.
- Correções de dados nunca são feitas por `UPDATE`: registra-se um novo evento compensatório.
