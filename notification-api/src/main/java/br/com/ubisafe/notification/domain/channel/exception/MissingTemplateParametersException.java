package br.com.ubisafe.notification.domain.channel.exception;

import br.com.ubisafe.notification.domain.shared.exception.DomainException;

import java.util.LinkedHashSet;
import java.util.Set;

public class MissingTemplateParametersException extends DomainException {

    private final Set<String> missingParameters;

    public MissingTemplateParametersException(Set<String> missingParameters) {
        super("Parâmetros obrigatórios do template ausentes: " + String.join(", ", missingParameters));
        this.missingParameters = new LinkedHashSet<>(missingParameters);
    }

    public Set<String> missingParameters() {
        return Set.copyOf(missingParameters);
    }
}
