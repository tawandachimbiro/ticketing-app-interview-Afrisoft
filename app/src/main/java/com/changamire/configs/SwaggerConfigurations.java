package com.changamire.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 * <p>
 * This configuration sets up Swagger/OpenAPI documentation for the ticketing system API.
 * Provides API metadata including title, version, contact information, and license.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Configuration
public class SwaggerConfigurations {

    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();
        contact.setEmail("developers@chimbiro.co.zw");
        contact.setName("Chimbiro Engineering Team");
        contact.setUrl("https://www.chimbiro.co.zw/#/");

        License mitLicense = new License().name("License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Afrisoft (interview) Ticketing system")
                .version("1.0")
                .contact(contact)
                .description("Afrisoft (interview) Ticketing system").termsOfService("https://www.chimbiro.co.zw/#/")
                .license(mitLicense);

        return new OpenAPI().info(info);

    }
}
