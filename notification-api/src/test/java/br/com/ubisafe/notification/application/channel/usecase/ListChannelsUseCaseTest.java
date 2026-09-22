package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.application.channel.query.ListChannelsQuery;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;
import br.com.ubisafe.notification.domain.shared.pagination.PageQuery;
import br.com.ubisafe.notification.domain.shared.pagination.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.activeChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListChannelsUseCaseTest {

    @Mock
    private ChannelRepository repository;

    @InjectMocks
    private ListChannelsUseCase useCase;

    @Test
    void shouldReturnMappedPage() {
        when(repository.findAll(new PageQuery(0, 20)))
                .thenReturn(new PageResult<>(List.of(activeChannel(), activeChannel()), 0, 20, 2));

        PageResult<ChannelView> page = useCase.execute(new ListChannelsQuery(0, 20));

        assertThat(page.items()).hasSize(2);
        assertThat(page.totalPages()).isEqualTo(1);
    }

    @Test
    void shouldRejectInvalidPageSize() {
        assertThatThrownBy(() -> useCase.execute(new ListChannelsQuery(0, 500)))
                .isInstanceOf(DomainValidationException.class);
    }
}
