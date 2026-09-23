package br.com.ubisafe.notification.presentation.alert.controller;

import br.com.ubisafe.notification.application.alert.dto.DispatchAlertResult;
import br.com.ubisafe.notification.application.alert.usecase.DispatchAlertUseCase;
import br.com.ubisafe.notification.presentation.alert.request.DispatchAlertRequest;
import br.com.ubisafe.notification.presentation.alert.response.AlertAcceptedResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AlertController implements AlertApi {

    private final DispatchAlertUseCase dispatchAlert;

    public AlertController(DispatchAlertUseCase dispatchAlert) {
        this.dispatchAlert = dispatchAlert;
    }

    @Override
    @PostMapping("/channels/{channelId}/alerts")
    public ResponseEntity<AlertAcceptedResponse> dispatch(@PathVariable UUID channelId,
                                                          @Valid @RequestBody DispatchAlertRequest request) {
        DispatchAlertResult result = dispatchAlert.execute(request.toCommand(channelId));
        return ResponseEntity.accepted().body(new AlertAcceptedResponse(result.correlationId()));
    }
}
