package br.com.ubisafe.notification.presentation.alert.request;

import br.com.ubisafe.notification.application.alert.command.DispatchAlertCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record DispatchAlertRequest(
        @Schema(example = "12345")
        @NotNull @Positive Long clientId,
        @Schema(example = "{\"clientName\": \"João Silva\", \"billingMonth\": \"Novembro/2025\"}")
        @Size(max = 50) Map<String, String> params
) {

    public DispatchAlertCommand toCommand(UUID channelId) {
        return new DispatchAlertCommand(channelId, clientId, params);
    }
}
