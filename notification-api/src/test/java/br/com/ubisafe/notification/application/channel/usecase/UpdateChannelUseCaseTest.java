package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.command.ChannelData;
import br.com.ubisafe.notification.application.channel.command.UpdateChannelCommand;
import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.application.channel.exception.ChannelNotFoundException;
import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.domain.channel.exception.ChannelAlreadyExistsException;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.channel.service.ChannelUniquenessPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.activeChannel;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.data;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateChannelUseCaseTest {

    @Mock
    private ChannelRepository repository;

    private UpdateChannelUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateChannelUseCase(repository, new ChannelLookup(repository),
                new ChannelUniquenessPolicy(repository), fixedClock());
    }

    @Test
    void shouldUpdateExistingChannel() {
        Channel channel = activeChannel();
        when(repository.findById(channel.id())).thenReturn(Optional.of(channel));
        when(repository.save(channel)).thenReturn(channel);
        ChannelData newData = new ChannelData("Cobrança", ChannelType.SMS, "Pague {{valor}}", 1, Priority.LOW, false);

        ChannelView view = useCase.execute(new UpdateChannelCommand(channel.id().value(), newData));

        assertThat(view.name()).isEqualTo("Cobrança");
        assertThat(view.type()).isEqualTo(ChannelType.SMS);
        assertThat(view.active()).isFalse();
    }

    @Test
    void shouldFailWhenChannelDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new UpdateChannelCommand(id, data(true))))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    void shouldRejectConflictWithAnotherChannel() {
        Channel channel = activeChannel();
        when(repository.findById(channel.id())).thenReturn(Optional.of(channel));
        when(repository.existsByNameAndTypeExcluding("Fatura Disponível", ChannelType.EMAIL, channel.id()))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new UpdateChannelCommand(channel.id().value(), data(true))))
                .isInstanceOf(ChannelAlreadyExistsException.class);
        verify(repository, never()).save(any());
    }
}
