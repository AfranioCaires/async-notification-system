# ADR 0007 — Strategy para entrega por tipo de canal

## Contexto

Cada tipo de alerta (`EMAIL`, `SMS`, `PUSH`) tem regras próprias de entrega, e novos tipos (ex.: `WHATSAPP`) devem entrar sem alterar o fluxo de processamento.

## Decisão

- Porta de saída `AlertDeliveryStrategy` na camada de aplicação: `channelType()` + `deliver(Alert)`.
- Implementações em `infrastructure/delivery` (`EmailDeliveryStrategy`, `SmsDeliveryStrategy`, `PushDeliveryStrategy`), registradas como beans.
- `DeliveryStrategyResolver` recebe todas as estratégias, monta um `EnumMap` e falha no startup se houver duas estratégias para o mesmo tipo.
- As entregas atuais são simuladas: registram em log os detalhes específicos de cada canal (assunto do e-mail, segmentos de SMS, prioridade de push).

Para adicionar um tipo novo:

1. adicionar o valor ao `ChannelType` dos dois serviços;
2. criar uma classe que implementa `AlertDeliveryStrategy` em `infrastructure/delivery`.

`ProcessAlertUseCase` não muda.

## Consequências

- Princípio aberto/fechado: o caso de uso é fechado para modificação e aberto para novos canais.
- Um teste ArchUnit garante que as estratégias vivam em `infrastructure/delivery`.
- Se a mensagem trouxer um tipo sem estratégia, o erro é não retentável e a mensagem vai para a DLQ com `FALHA` registrada.
