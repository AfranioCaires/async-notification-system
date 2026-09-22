package br.com.ubisafe.notification.domain.channel;

import br.com.ubisafe.notification.domain.channel.exception.MissingTemplateParametersException;
import br.com.ubisafe.notification.domain.channel.model.Template;
import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TemplateTest {

    @Test
    void shouldRenderAllPlaceholders() {
        Template template = new Template(TEMPLATE);

        String message = template.render(Map.of("clientName", "João Silva", "billingMonth", "Novembro/2025"));

        assertThat(message).isEqualTo("Olá João Silva, sua fatura de Novembro/2025 está disponível.");
    }

    @Test
    void shouldAcceptPlaceholdersWithInnerSpaces() {
        Template template = new Template("Valor: {{ amount }}");

        assertThat(template.render(Map.of("amount", "R$ 10,00"))).isEqualTo("Valor: R$ 10,00");
    }

    @Test
    void shouldKeepReplacementCharactersLiteral() {
        Template template = new Template("Total {{amount}}");

        assertThat(template.render(Map.of("amount", "$1\\2"))).isEqualTo("Total $1\\2");
    }

    @Test
    void shouldListDistinctPlaceholdersInOrder() {
        Template template = new Template("{{a}} {{b}} {{a}}");

        assertThat(template.placeholders()).containsExactly("a", "b");
    }

    @Test
    void shouldReportEveryMissingOrBlankParameter() {
        Template template = new Template(TEMPLATE);

        assertThatThrownBy(() -> template.render(Map.of("clientName", " ")))
                .isInstanceOfSatisfying(MissingTemplateParametersException.class, exception ->
                        assertThat(exception.missingParameters()).containsExactlyInAnyOrder("clientName", "billingMonth"));
    }

    @Test
    void shouldRejectBlankTemplate() {
        assertThatThrownBy(() -> new Template(" ")).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldRejectTemplateAboveMaxLength() {
        String tooLong = "x".repeat(Template.MAX_LENGTH + 1);

        assertThatThrownBy(() -> new Template(tooLong)).isInstanceOf(DomainValidationException.class);
    }
}
