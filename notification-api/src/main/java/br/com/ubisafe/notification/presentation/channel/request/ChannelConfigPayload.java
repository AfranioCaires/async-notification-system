package br.com.ubisafe.notification.presentation.channel.request;

import br.com.ubisafe.notification.domain.channel.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChannelConfigPayload(
        @Schema(example = "3", minimum = "0", maximum = "10")
        @NotNull @Min(0) @Max(10) Integer maxRetries,
        @Schema(example = "HIGH")
        @NotNull Priority priority
) {
}
