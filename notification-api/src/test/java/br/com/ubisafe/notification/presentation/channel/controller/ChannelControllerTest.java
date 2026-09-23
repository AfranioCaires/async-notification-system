package br.com.ubisafe.notification.presentation.channel.controller;

import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.application.channel.exception.ChannelNotFoundException;
import br.com.ubisafe.notification.application.channel.usecase.CreateChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.DeleteChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.GetChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.ListChannelsUseCase;
import br.com.ubisafe.notification.application.channel.usecase.UpdateChannelUseCase;
import br.com.ubisafe.notification.domain.channel.exception.ChannelAlreadyExistsException;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.activeChannel;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    private static final String VALID_BODY = """
            {
              "name": "Fatura Disponível",
              "type": "EMAIL",
              "template": "Olá {{clientName}}",
              "config": { "maxRetries": 3, "priority": "HIGH" },
              "active": true
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateChannelUseCase createChannel;

    @MockitoBean
    private ListChannelsUseCase listChannels;

    @MockitoBean
    private GetChannelUseCase getChannel;

    @MockitoBean
    private UpdateChannelUseCase updateChannel;

    @MockitoBean
    private DeleteChannelUseCase deleteChannel;

    @Test
    void shouldCreateChannelAndReturnLocation() throws Exception {
        ChannelView view = ChannelView.from(activeChannel());
        when(createChannel.execute(any())).thenReturn(view);

        mockMvc.perform(post("/channels").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/channels/" + view.id()))
                .andExpect(jsonPath("$.id").value(view.id().toString()))
                .andExpect(jsonPath("$.config.maxRetries").value(3))
                .andExpect(jsonPath("$.config.priority").value("HIGH"));
    }

    @Test
    void shouldReturnFieldErrorsForInvalidPayload() throws Exception {
        mockMvc.perform(post("/channels").contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"EMAIL\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation-error"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void shouldRejectUnknownChannelType() throws Exception {
        mockMvc.perform(post("/channels").contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY.replace("EMAIL", "FAX")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("malformed-request"));
    }

    @Test
    void shouldReturnConflictForDuplicatedChannel() throws Exception {
        when(createChannel.execute(any())).thenThrow(new ChannelAlreadyExistsException("Fatura Disponível", ChannelType.EMAIL));

        mockMvc.perform(post("/channels").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("channel-already-exists"));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getChannel.execute(id)).thenThrow(new ChannelNotFoundException(new ChannelId(id)));

        mockMvc.perform(get("/channels/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("channel-not-found"))
                .andExpect(jsonPath("$.instance").value("/channels/" + id));
    }

    @Test
    void shouldRejectMalformedId() throws Exception {
        mockMvc.perform(get("/channels/{id}", "not-a-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteChannel() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/channels/{id}", id)).andExpect(status().isNoContent());

        verify(deleteChannel).execute(id);
    }

    @Test
    void shouldPropagateNotFoundOnDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ChannelNotFoundException(new ChannelId(id))).when(deleteChannel).execute(id);

        mockMvc.perform(delete("/channels/{id}", id)).andExpect(status().isNotFound());
    }
}
