# ADR 0011 — CI no GitHub Actions e imagens Docker em camadas

## Contexto

Cada mudança precisa ser validada automaticamente (testes, regras de arquitetura, build de imagem), e as imagens precisam ser pequenas, seguras e reprodutíveis.

## Decisão

**CI (`.github/workflows/ci.yml`)**

- Job `test` em matriz por serviço: JDK 21 Temurin com cache Maven, `./mvnw -pl <serviço> verify` (unitários, slices web e ArchUnit). Relatórios do Surefire são publicados como artefato em caso de falha.
- Job `image` (depende de `test`): build das imagens com Buildx e cache do GitHub Actions, sem push.
- Job `compose`: `docker compose config --quiet` valida o arquivo de infraestrutura.
- `concurrency` cancela execuções obsoletas do mesmo ref; `permissions: contents: read` segue o princípio do menor privilégio.

**Imagens**

- Multi-stage: build com Maven e runtime com `eclipse-temurin:21-jre-alpine`.
- Layered jar (`-Djarmode=tools extract --layers`): dependências mudam pouco e ficam em camadas cacheáveis.
- Execução como usuário não-root; `MaxRAMPercentage=75` respeita o limite de memória do container; `ExitOnOutOfMemoryError` deixa o orquestrador reiniciar o processo.
- Healthchecks do Compose usam `/actuator/health/readiness`.

## Consequências

- Nenhuma mudança chega à `main` sem passar pelas regras de arquitetura e pelos testes.
- Os testes não rodam dentro do `docker build` (o CI já os executa), o que deixa o build de imagem mais rápido.
- Publicação em registry e deploy ficam como próximo passo (ex.: job `release` em tags, com push para o GHCR).
