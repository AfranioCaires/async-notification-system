package br.com.ubisafe.alertprocessor.presentation.alert.controller;

import br.com.ubisafe.alertprocessor.application.alert.usecase.GetAlertStatusUseCase;
import br.com.ubisafe.alertprocessor.presentation.alert.response.AlertStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AlertStatusController implements AlertStatusApi {

    private final GetAlertStatusUseCase getAlertStatus;

    public AlertStatusController(GetAlertStatusUseCase getAlertStatus) {
        this.getAlertStatus = getAlertStatus;
    }

    @Override
    @GetMapping("/alerts/{correlationId}/status")
    public AlertStatusResponse status(@PathVariable UUID correlationId) {
        return AlertStatusResponse.from(getAlertStatus.execute(correlationId));
    }
}
