package br.com.ubisafe.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NotificationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApiApplication.class, args);
    }
}
