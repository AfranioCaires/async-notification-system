package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.application.channel.query.ListChannelsQuery;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.shared.pagination.PageQuery;
import br.com.ubisafe.notification.domain.shared.pagination.PageResult;

public class ListChannelsUseCase {

    private final ChannelRepository repository;

    public ListChannelsUseCase(ChannelRepository repository) {
        this.repository = repository;
    }

    public PageResult<ChannelView> execute(ListChannelsQuery query) {
        return repository.findAll(new PageQuery(query.page(), query.size())).map(ChannelView::from);
    }
}
