# ADR 0003 — Dois microsserviços com banco por serviço

## Contexto

O envio síncrono causa timeouts em pico. A recepção de pedidos de disparo e o processamento por canal têm perfis de carga e ciclos de evolução diferentes.

## Decisão

- **`notification-api`**: dono do cadastro de canais e ponto de entrada de disparos. Valida, renderiza o template, publica no Kafka e responde `202 Accepted`.
- **`alert-processor`**: consome as mensagens, executa a estratégia de entrega, mantém o event store e expõe a consulta de status.
- Cada serviço tem seu próprio schema (`notification_db` e `alert_processor_db`) com usuário próprio e privilégios mínimos. Nenhum serviço lê o banco do outro; a integração acontece apenas via Kafka.
- Tipos compartilhados (ex.: `ChannelType`) são duplicados em cada serviço em vez de publicados como biblioteca comum. O contrato é o JSON da mensagem.
- Um POM agregador na raiz permite build único local e no CI, mas cada serviço tem POM, Dockerfile e ciclo de release independentes.

## Consequências

- A API escala horizontalmente para absorver picos; o processador escala por partições do tópico.
- Uma indisponibilidade do processador não impede a aceitação de novos disparos (as mensagens ficam retidas no Kafka).
- A duplicação de enums exige coordenação ao adicionar um novo tipo de canal (ver ADR 0007). Em troca, evitamos acoplamento binário entre serviços.
- Em produção, os dois schemas podem viver em instâncias distintas sem mudança de código.
