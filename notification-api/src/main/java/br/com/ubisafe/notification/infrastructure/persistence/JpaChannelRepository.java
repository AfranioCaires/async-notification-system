package br.com.ubisafe.notification.infrastructure.persistence;

import br.com.ubisafe.notification.domain.channel.exception.ChannelAlreadyExistsException;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.shared.pagination.PageQuery;
import br.com.ubisafe.notification.domain.shared.pagination.PageResult;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaChannelRepository implements ChannelRepository {

    private final SpringDataChannelRepository springData;

    JpaChannelRepository(SpringDataChannelRepository springData) {
        this.springData = springData;
    }

    @Override
    public Channel save(Channel channel) {
        boolean newEntity = !springData.existsById(channel.id().toString());
        try {
            springData.saveAndFlush(ChannelEntityMapper.toEntity(channel, newEntity));
        } catch (DataIntegrityViolationException exception) {
            throw new ChannelAlreadyExistsException(channel.name(), channel.type());
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(ChannelId id) {
        return springData.findByIdAndDeletedAtIsNull(id.toString()).map(ChannelEntityMapper::toDomain);
    }

    @Override
    public PageResult<Channel> findAll(PageQuery pageQuery) {
        PageRequest pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChannelJpaEntity> page = springData.findAllByDeletedAtIsNull(pageable);
        return new PageResult<>(
                page.getContent().stream().map(ChannelEntityMapper::toDomain).toList(),
                pageQuery.page(),
                pageQuery.size(),
                page.getTotalElements()
        );
    }

    @Override
    public boolean existsByNameAndType(String name, ChannelType type) {
        return springData.existsByNameAndTypeAndDeletedAtIsNull(name, type);
    }

    @Override
    public boolean existsByNameAndTypeExcluding(String name, ChannelType type, ChannelId excludedId) {
        return springData.existsByNameAndTypeAndDeletedAtIsNullAndIdNot(name, type, excludedId.toString());
    }
}
