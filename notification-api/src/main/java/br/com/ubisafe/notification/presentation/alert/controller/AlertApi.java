package br.com.ubisafe.notification.presentation.alert.controller;

import br.com.ubisafe.notification.presentation.alert.request.DispatchAlertRequest;
import br.com.ubisafe.notification.presentation.alert.response.AlertAcceptedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Alertas", description = "Disparo assíncrono de alertas")
public interface AlertApi {

    @Operation(summary = "Dispara um alerta pelo canal informado",
            description = "Resolve o template do canal com os parâmetros e publica a mensagem no Kafka")
    @ApiResponse(responseCode = "202", description = "Alerta aceito para processamento")
    @ApiResponse(responseCode = "400", description = "Payload inválido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Canal não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Canal inativo ou parâmetros do template ausentes",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "503", description = "Mensageria indisponível",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    ResponseEntity<AlertAcceptedResponse> dispatch(UUID channelId, DispatchAlertRequest request);
}
