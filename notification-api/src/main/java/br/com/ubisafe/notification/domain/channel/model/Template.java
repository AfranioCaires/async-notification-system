package br.com.ubisafe.notification.domain.channel.model;

import br.com.ubisafe.notification.domain.channel.exception.MissingTemplateParametersException;
import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Template(String value) {

    public static final int MAX_LENGTH = 2000;
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([A-Za-z0-9_.-]+)\\s*}}");

    public Template {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("O template do canal é obrigatório");
        }
        if (value.length() > MAX_LENGTH) {
            throw new DomainValidationException("O template deve ter no máximo " + MAX_LENGTH + " caracteres");
        }
    }

    public Set<String> placeholders() {
        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = PLACEHOLDER.matcher(value);
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }

    public String render(Map<String, String> params) {
        Set<String> missing = new LinkedHashSet<>();
        for (String name : placeholders()) {
            if (isAbsent(params.get(name))) {
                missing.add(name);
            }
        }
        if (!missing.isEmpty()) {
            throw new MissingTemplateParametersException(missing);
        }
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder rendered = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(params.get(matcher.group(1))));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }

    private static boolean isAbsent(String param) {
        return param == null || param.isBlank();
    }
}
