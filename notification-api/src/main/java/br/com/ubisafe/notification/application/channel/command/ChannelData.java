package br.com.ubisafe.notification.application.channel.command;

import br.com.ubisafe.notification.domain.channel.model.ChannelConfig;
import br.com.ubisafe.notification.domain.channel.model.ChannelDefinition;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;
import br.com.ubisafe.notification.domain.channel.model.Template;

public record ChannelData(
        String name,
        ChannelType type,
        String template,
        int maxRetries,
        Priority priority,
        boolean active
) {

    public ChannelDefinition toDefinition() {
        return new ChannelDefinition(name, type, new Template(template), new ChannelConfig(maxRetries, priority));
    }
}
