# ADR 0009 — API HTTP, Problem Details e OpenAPI

## Contexto

Clientes precisam de erros previsíveis e informativos, e de documentação navegável e sempre atualizada.

## Decisão

- Erros no formato RFC 9457 (Problem Details) com `application/problem+json`, enriquecidos com `code` estável (legível por máquina) e `timestamp`:

| Situação | Status | `code` |
|----------|--------|--------|
| Campos inválidos (Bean Validation) | 400 | `validation-error` (+ `errors[]`) |
| JSON malformado / enum desconhecido | 400 | `malformed-request` |
| Regra de domínio violada | 400 | `validation-error` |
| Canal inexistente | 404 | `channel-not-found` |
| Nome + tipo duplicados | 409 | `channel-already-exists` |
| Canal inativo | 422 | `channel-inactive` |
| Parâmetros do template ausentes | 422 | `missing-template-params` (+ `missingParams[]`) |
| Broker indisponível | 503 | `alert-publication-unavailable` |
| Erro inesperado | 500 | `internal-error`, sem vazar detalhes internos |

- Validação sintática com Bean Validation nos DTOs; regras de negócio no domínio. A presentation não decide regra de negócio.
- `POST /channels` retorna `201` com `Location`; `DELETE` retorna `204`; disparo retorna `202` com `correlationId`.
- Listagem paginada (`page`, `size` ≤ 100) para não devolver coleções ilimitadas.
- **OpenAPI 3 via springdoc**: anotações de documentação ficam em interfaces (`ChannelApi`, `AlertApi`, `AlertStatusApi`) implementadas pelos controllers, que ficam limpos. Swagger UI em `/swagger-ui.html` e contrato em `/v3/api-docs`, desligáveis com `SWAGGER_ENABLED=false`.

## Consequências

- Clientes tratam erros por `code`, não por texto.
- A documentação é gerada do código e não fica desatualizada.
- Autenticação/autorização não foi implementada neste escopo; a recomendação é aplicá-la no API Gateway (OAuth2/JWT) ou adicionar Spring Security como resource server.
