package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.exception.ChannelNotFoundException;
import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.NOW;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.activeChannel;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteChannelUseCaseTest {

    @Mock
    private ChannelRepository repository;

    private DeleteChannelUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteChannelUseCase(repository, new ChannelLookup(repository), fixedClock());
    }

    @Test
    void shouldDeleteLogically() {
        Channel channel = activeChannel();
        when(repository.findById(channel.id())).thenReturn(Optional.of(channel));

        useCase.execute(channel.id().value());

        verify(repository).save(channel);
        assertThat(channel.deletedAt()).contains(NOW);
        assertThat(channel.isActive()).isFalse();
    }

    @Test
    void shouldFailWhenChannelDoesNotExist() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID())).isInstanceOf(ChannelNotFoundException.class);
    }
}
