package br.com.ubisafe.notification.domain.channel;

import br.com.ubisafe.notification.domain.channel.exception.ChannelAlreadyExistsException;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.channel.service.ChannelUniquenessPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.definition;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelUniquenessPolicyTest {

    @Mock
    private ChannelRepository repository;

    @InjectMocks
    private ChannelUniquenessPolicy policy;

    @Test
    void shouldRejectDuplicatedNameAndType() {
        when(repository.existsByNameAndType("Fatura Disponível", ChannelType.EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> policy.ensureAvailable(definition()))
                .isInstanceOf(ChannelAlreadyExistsException.class);
    }

    @Test
    void shouldAcceptSameDefinitionForTheSameChannel() {
        ChannelId channelId = ChannelId.generate();
        when(repository.existsByNameAndTypeExcluding("Fatura Disponível", ChannelType.EMAIL, channelId)).thenReturn(false);

        assertThatCode(() -> policy.ensureAvailableFor(channelId, definition())).doesNotThrowAnyException();
    }
}
