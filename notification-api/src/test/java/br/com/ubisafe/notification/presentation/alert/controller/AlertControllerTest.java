package br.com.ubisafe.notification.presentation.alert.controller;

import br.com.ubisafe.notification.application.alert.dto.DispatchAlertResult;
import br.com.ubisafe.notification.application.alert.usecase.DispatchAlertUseCase;
import br.com.ubisafe.notification.application.port.AlertPublicationException;
import br.com.ubisafe.notification.domain.channel.exception.ChannelInactiveException;
import br.com.ubisafe.notification.domain.channel.exception.MissingTemplateParametersException;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

    private static final String BODY = """
            { "clientId": 12345, "params": { "clientName": "João Silva", "billingMonth": "Novembro/2025" } }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DispatchAlertUseCase dispatchAlert;

    @Test
    void shouldAcceptAlertAndReturnCorrelationId() throws Exception {
        UUID correlationId = UUID.randomUUID();
        when(dispatchAlert.execute(any())).thenReturn(new DispatchAlertResult(correlationId));

        mockMvc.perform(post("/channels/{id}/alerts", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.correlationId").value(correlationId.toString()));
    }

    @Test
    void shouldRejectMissingClientId() throws Exception {
        mockMvc.perform(post("/channels/{id}/alerts", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"params\":{}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("clientId"));
    }

    @Test
    void shouldReturnUnprocessableForInactiveChannel() throws Exception {
        UUID channelId = UUID.randomUUID();
        when(dispatchAlert.execute(any())).thenThrow(new ChannelInactiveException(new ChannelId(channelId)));

        mockMvc.perform(post("/channels/{id}/alerts", channelId).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("channel-inactive"));
    }

    @Test
    void shouldListMissingParams() throws Exception {
        when(dispatchAlert.execute(any())).thenThrow(new MissingTemplateParametersException(Set.of("billingMonth")));

        mockMvc.perform(post("/channels/{id}/alerts", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.missingParams[0]").value("billingMonth"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenBrokerIsDown() throws Exception {
        when(dispatchAlert.execute(any())).thenThrow(new AlertPublicationException("indisponível", new RuntimeException()));

        mockMvc.perform(post("/channels/{id}/alerts", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("alert-publication-unavailable"));
    }
}
