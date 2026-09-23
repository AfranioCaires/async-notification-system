package br.com.ubisafe.notification.presentation.channel.request;

import br.com.ubisafe.notification.application.channel.command.ChannelData;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateChannelRequest(
        @Schema(example = "Fatura Disponível")
        @NotBlank @Size(max = 120) String name,
        @Schema(example = "EMAIL")
        @NotNull ChannelType type,
        @Schema(example = "Olá {{clientName}}, sua fatura de {{billingMonth}} está disponível.")
        @NotBlank @Size(max = 2000) String template,
        @NotNull @Valid ChannelConfigPayload config,
        @Schema(example = "false")
        @NotNull Boolean active
) {

    public ChannelData toData() {
        return new ChannelData(name, type, template, config.maxRetries(), config.priority(), active);
    }
}
