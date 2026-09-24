package br.com.ubisafe.alertprocessor.presentation.alert.controller;

import br.com.ubisafe.alertprocessor.presentation.alert.response.AlertStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;

import java.util.UUID;

@Tag(name = "Alertas", description = "Rastreabilidade do processamento de alertas")
public interface AlertStatusApi {

    @Operation(summary = "Consulta o status e o histórico de eventos de um alerta",
            description = "O status atual é derivado do último evento registrado no event store")
    @ApiResponse(responseCode = "200", description = "Histórico encontrado")
    @ApiResponse(responseCode = "400", description = "correlationId inválido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Nenhum evento para o correlationId",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    AlertStatusResponse status(@Parameter(description = "correlationId retornado no disparo") UUID correlationId);
}
