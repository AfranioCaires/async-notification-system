package br.com.ubisafe.notification.domain.channel.model;

import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;

public record ChannelDefinition(String name, ChannelType type, Template template, ChannelConfig config) {

    public static final int NAME_MAX_LENGTH = 120;

    public ChannelDefinition {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("O nome do canal é obrigatório");
        }
        name = name.strip();
        if (name.length() > NAME_MAX_LENGTH) {
            throw new DomainValidationException("O nome do canal deve ter no máximo " + NAME_MAX_LENGTH + " caracteres");
        }
        if (type == null) {
            throw new DomainValidationException("O tipo do canal é obrigatório");
        }
        if (template == null) {
            throw new DomainValidationException("O template do canal é obrigatório");
        }
        if (config == null) {
            throw new DomainValidationException("A configuração do canal é obrigatória");
        }
    }
}
