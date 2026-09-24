package br.com.ubisafe.alertprocessor.presentation.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {

    @Bean
    OpenAPI alertProcessorOpenApi(@Value("${app.version:1.0.0}") String version) {
        return new OpenAPI().info(new Info()
                .title("Alert Processor")
                .version(version)
                .description("Consulta do ciclo de vida das notificações processadas")
                .license(new License().name("Proprietária")));
    }
}
