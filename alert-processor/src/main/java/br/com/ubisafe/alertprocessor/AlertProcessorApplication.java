package br.com.ubisafe.alertprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AlertProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertProcessorApplication.class, args);
    }
}
