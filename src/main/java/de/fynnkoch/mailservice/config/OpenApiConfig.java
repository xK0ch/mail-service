package de.fynnkoch.mailservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mail Service API")
                        .description("Sends contact-form messages to a fixed recipient (public) and "
                                + "transactional emails to arbitrary recipients (internal, API-key protected).")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact().name("Fynn Koch").email("mail@fynn-koch.de")));
    }
}
