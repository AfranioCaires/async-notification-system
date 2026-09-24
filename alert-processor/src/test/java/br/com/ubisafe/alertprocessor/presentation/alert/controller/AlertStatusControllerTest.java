package br.com.ubisafe.alertprocessor.presentation.alert.controller;

import br.com.ubisafe.alertprocessor.application.alert.dto.AlertStatusView;
import br.com.ubisafe.alertprocessor.application.alert.exception.AlertNotFoundException;
import br.com.ubisafe.alertprocessor.application.alert.usecase.GetAlertStatusUseCase;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.CORRELATION_ID;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.NOW;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertStatusController.class)
class AlertStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAlertStatusUseCase getAlertStatus;

    @Test
    void shouldReturnStatusWithEvents() throws Exception {
        when(getAlertStatus.execute(CORRELATION_ID)).thenReturn(new AlertStatusView(CORRELATION_ID, ChannelType.EMAIL,
                12345L, NotificationStatus.PROCESSADO, List.of(
                new AlertStatusView.EventView(NotificationStatus.RECEBIDO, "Mensagem consumida do tópico", NOW),
                new AlertStatusView.EventView(NotificationStatus.PROCESSADO, "Processamento concluído com sucesso", NOW))));

        mockMvc.perform(get("/alerts/{id}/status", CORRELATION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correlationId").value(CORRELATION_ID.toString()))
                .andExpect(jsonPath("$.currentStatus").value("PROCESSADO"))
                .andExpect(jsonPath("$.events[0].status").value("RECEBIDO"))
                .andExpect(jsonPath("$.events.length()").value(2));
    }

    @Test
    void shouldReturnNotFoundForUnknownAlert() throws Exception {
        when(getAlertStatus.execute(CORRELATION_ID)).thenThrow(new AlertNotFoundException(CORRELATION_ID));

        mockMvc.perform(get("/alerts/{id}/status", CORRELATION_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("alert-not-found"));
    }

    @Test
    void shouldRejectMalformedCorrelationId() throws Exception {
        mockMvc.perform(get("/alerts/{id}/status", "123")).andExpect(status().isBadRequest());
    }
}
