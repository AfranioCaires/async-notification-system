package br.com.ubisafe.notification.presentation.channel.response;

import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.domain.shared.pagination.PageResult;

import java.util.List;

public record ChannelPageResponse(
        List<ChannelResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static ChannelPageResponse from(PageResult<ChannelView> result) {
        return new ChannelPageResponse(
                result.items().stream().map(ChannelResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
