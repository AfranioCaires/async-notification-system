package br.com.ubisafe.notification.presentation.channel.controller;

import br.com.ubisafe.notification.presentation.channel.request.CreateChannelRequest;
import br.com.ubisafe.notification.presentation.channel.request.UpdateChannelRequest;
import br.com.ubisafe.notification.presentation.channel.response.ChannelPageResponse;
import br.com.ubisafe.notification.presentation.channel.response.ChannelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Canais", description = "CRUD de canais de alerta")
public interface ChannelApi {

    @Operation(summary = "Cria um canal de alerta")
    @ApiResponse(responseCode = "201", description = "Canal criado")
    @ApiResponse(responseCode = "400", description = "Payload inválido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Já existe canal com o mesmo nome e tipo",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    ResponseEntity<ChannelResponse> create(CreateChannelRequest request);

    @Operation(summary = "Lista canais ativos e inativos não excluídos, paginados")
    @ApiResponse(responseCode = "200", description = "Página de canais")
    @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    ChannelPageResponse list(@Parameter(description = "Página, iniciando em 0") int page,
                             @Parameter(description = "Tamanho da página, entre 1 e 100") int size);

    @Operation(summary = "Busca um canal pelo identificador")
    @ApiResponse(responseCode = "200", description = "Canal encontrado")
    @ApiResponse(responseCode = "404", description = "Canal não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    ChannelResponse get(UUID id);

    @Operation(summary = "Atualiza integralmente um canal")
    @ApiResponse(responseCode = "200", description = "Canal atualizado")
    @ApiResponse(responseCode = "400", description = "Payload inválido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Canal não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Já existe canal com o mesmo nome e tipo",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    ChannelResponse update(UUID id, UpdateChannelRequest request);

    @Operation(summary = "Exclui logicamente um canal")
    @ApiResponse(responseCode = "204", description = "Canal excluído")
    @ApiResponse(responseCode = "404", description = "Canal não encontrado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    ResponseEntity<Void> delete(UUID id);
}
