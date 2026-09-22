package br.com.ubisafe.notification.infrastructure.persistence;

import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringDataChannelRepository extends JpaRepository<ChannelJpaEntity, String> {

    Optional<ChannelJpaEntity> findByIdAndDeletedAtIsNull(String id);

    Page<ChannelJpaEntity> findAllByDeletedAtIsNull(Pageable pageable);

    boolean existsByNameAndTypeAndDeletedAtIsNull(String name, ChannelType type);

    boolean existsByNameAndTypeAndDeletedAtIsNullAndIdNot(String name, ChannelType type, String id);
}
