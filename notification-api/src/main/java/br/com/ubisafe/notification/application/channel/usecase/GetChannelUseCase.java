package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;

import java.util.UUID;

public class GetChannelUseCase {

    private final ChannelLookup channelLookup;

    public GetChannelUseCase(ChannelLookup channelLookup) {
        this.channelLookup = channelLookup;
    }

    public ChannelView execute(UUID channelId) {
        return ChannelView.from(channelLookup.require(new ChannelId(channelId)));
    }
}
