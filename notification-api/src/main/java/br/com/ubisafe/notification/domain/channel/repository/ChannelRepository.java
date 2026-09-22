package br.com.ubisafe.notification.domain.channel.repository;

import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.shared.pagination.PageQuery;
import br.com.ubisafe.notification.domain.shared.pagination.PageResult;

import java.util.Optional;

public interface ChannelRepository {

    Channel save(Channel channel);

    Optional<Channel> findById(ChannelId id);

    PageResult<Channel> findAll(PageQuery pageQuery);

    boolean existsByNameAndType(String name, ChannelType type);

    boolean existsByNameAndTypeExcluding(String name, ChannelType type, ChannelId excludedId);
}
