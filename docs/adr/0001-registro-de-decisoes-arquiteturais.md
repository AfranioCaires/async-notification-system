# ADR 0001 — Registrar decisões arquiteturais em ADRs

## Contexto

A solução é composta por dois serviços que evoluem de forma independente. Decisões como formato de mensagens, estratégia de idempotência e regras de camadas precisam sobreviver à rotatividade do time e ser revisáveis em PR, junto do código.

## Decisão

Registrar toda decisão arquitetural relevante como um arquivo Markdown numerado em `docs/adr/`, versionado junto ao código, no formato Contexto / Decisão / Consequências.

## Consequências

- O histórico do "porquê" fica rastreável via Git.
- Decisões são revisadas em PR como qualquer código.
- Exige disciplina: mudanças estruturais sem ADR devem ser barradas no review.
