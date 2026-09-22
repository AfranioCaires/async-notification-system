package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.command.CreateChannelCommand;
import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.domain.channel.exception.ChannelAlreadyExistsException;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.channel.service.ChannelUniquenessPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.NOW;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.data;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateChannelUseCaseTest {

    @Mock
    private ChannelRepository repository;

    private CreateChannelUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateChannelUseCase(repository, new ChannelUniquenessPolicy(repository), fixedClock());
    }

    @Test
    void shouldCreateChannel() {
        when(repository.save(any(Channel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChannelView view = useCase.execute(new CreateChannelCommand(data(true)));

        assertThat(view.id()).isNotNull();
        assertThat(view.name()).isEqualTo("Fatura Disponível");
        assertThat(view.type()).isEqualTo(ChannelType.EMAIL);
        assertThat(view.active()).isTrue();
        assertThat(view.createdAt()).isEqualTo(NOW);
    }

    @Test
    void shouldNotCreateDuplicatedChannel() {
        when(repository.existsByNameAndType("Fatura Disponível", ChannelType.EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new CreateChannelCommand(data(true))))
                .isInstanceOf(ChannelAlreadyExistsException.class);
        verify(repository, never()).save(any());
    }
}
