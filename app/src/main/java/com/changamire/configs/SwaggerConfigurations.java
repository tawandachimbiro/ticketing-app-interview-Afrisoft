package com.changamire.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfigurations {

    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();
        contact.setEmail("developers@chimbiro.co.zw");
        contact.setName("Chimbiro Engineering Team");
        contact.setUrl("https://www.afrosoft.co.zw/#/");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("CBZ Ticketing system")
                .version("1.0")
                .contact(contact)
                .description("CBZ Ticketing system").termsOfService("https://www.chimbiro.co.zw/#/")
                .license(mitLicense);

        return new OpenAPI().info(info);

    }
}
