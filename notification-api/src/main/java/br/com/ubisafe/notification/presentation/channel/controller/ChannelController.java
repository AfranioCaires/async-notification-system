package br.com.ubisafe.notification.presentation.channel.controller;

import br.com.ubisafe.notification.application.channel.command.CreateChannelCommand;
import br.com.ubisafe.notification.application.channel.command.UpdateChannelCommand;
import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.application.channel.query.ListChannelsQuery;
import br.com.ubisafe.notification.application.channel.usecase.CreateChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.DeleteChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.GetChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.ListChannelsUseCase;
import br.com.ubisafe.notification.application.channel.usecase.UpdateChannelUseCase;
import br.com.ubisafe.notification.presentation.channel.request.CreateChannelRequest;
import br.com.ubisafe.notification.presentation.channel.request.UpdateChannelRequest;
import br.com.ubisafe.notification.presentation.channel.response.ChannelPageResponse;
import br.com.ubisafe.notification.presentation.channel.response.ChannelResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class ChannelController implements ChannelApi {

    private final CreateChannelUseCase createChannel;
    private final ListChannelsUseCase listChannels;
    private final GetChannelUseCase getChannel;
    private final UpdateChannelUseCase updateChannel;
    private final DeleteChannelUseCase deleteChannel;

    public ChannelController(CreateChannelUseCase createChannel, ListChannelsUseCase listChannels,
                             GetChannelUseCase getChannel, UpdateChannelUseCase updateChannel,
                             DeleteChannelUseCase deleteChannel) {
        this.createChannel = createChannel;
        this.listChannels = listChannels;
        this.getChannel = getChannel;
        this.updateChannel = updateChannel;
        this.deleteChannel = deleteChannel;
    }

    @Override
    @PostMapping
    public ResponseEntity<ChannelResponse> create(@Valid @RequestBody CreateChannelRequest request) {
        ChannelView created = createChannel.execute(new CreateChannelCommand(request.toData()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(ChannelResponse.from(created));
    }

    @Override
    @GetMapping
    public ChannelPageResponse list(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return ChannelPageResponse.from(listChannels.execute(new ListChannelsQuery(page, size)));
    }

    @Override
    @GetMapping("/{id}")
    public ChannelResponse get(@PathVariable UUID id) {
        return ChannelResponse.from(getChannel.execute(id));
    }

    @Override
    @PutMapping("/{id}")
    public ChannelResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateChannelRequest request) {
        return ChannelResponse.from(updateChannel.execute(new UpdateChannelCommand(id, request.toData())));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteChannel.execute(id);
        return ResponseEntity.noContent().build();
    }
}
