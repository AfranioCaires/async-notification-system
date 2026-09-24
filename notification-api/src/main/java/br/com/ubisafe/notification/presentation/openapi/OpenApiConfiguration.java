package br.com.ubisafe.notification.presentation.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {

    @Bean
    OpenAPI notificationApiOpenApi(@Value("${app.version:1.0.0}") String version) {
        return new OpenAPI().info(new Info()
                .title("Notification API")
                .version(version)
                .description("Gerenciamento de canais de alerta e disparo assíncrono de notificações")
                .license(new License().name("Proprietária")));
    }
}
