package br.com.ubisafe.notification.application.alert.command;

import java.util.Map;
import java.util.UUID;

public record DispatchAlertCommand(UUID channelId, long clientId, Map<String, String> params) {

    public DispatchAlertCommand {
        params = params == null ? Map.of() : Map.copyOf(params);
    }
}
