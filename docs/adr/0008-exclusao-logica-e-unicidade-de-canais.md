# ADR 0008 — Exclusão lógica e unicidade de canais

## Contexto

Canais precisam de exclusão lógica, e não pode haver dois canais com o mesmo `name` e `type`. Um canal excluído não deve impedir que um novo canal com o mesmo nome seja criado.

## Decisão

- Exclusão lógica via `deleted_at`. `Channel.delete(now)` também desativa o canal. Canais excluídos não aparecem em nenhuma consulta e respondem `404`.
- Unicidade verificada em dois níveis:
  1. **Domínio:** `ChannelUniquenessPolicy` consulta o repositório antes de criar ou atualizar e lança `ChannelAlreadyExistsException` (`409`).
  2. **Banco:** coluna gerada `live_marker = IF(deleted_at IS NULL, 1, NULL)` e `UNIQUE (name, type, live_marker)`. Como o MySQL permite múltiplos `NULL` em índices únicos, só canais não excluídos competem pela unicidade (equivalente a um índice parcial). Uma violação por corrida também é traduzida para `409`.
- Os bancos são criados com a collation `utf8mb4_0900_as_ci`, que ignora maiúsculas e diferencia acentos: "Fatura Disponível" e "fatura disponível" são o mesmo nome, mas "Fatura Disponivel" é outro.

## Consequências

- Não há janela de corrida que permita duplicatas.
- Registros excluídos permanecem para auditoria.
- Exclusão é idempotente no domínio; na API, excluir de novo retorna `404`, pois o recurso deixou de existir para o cliente.
