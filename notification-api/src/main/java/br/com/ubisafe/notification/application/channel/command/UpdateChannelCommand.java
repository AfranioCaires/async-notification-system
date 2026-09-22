package br.com.ubisafe.notification.application.channel.command;

import java.util.UUID;

public record UpdateChannelCommand(UUID channelId, ChannelData data) {
}
