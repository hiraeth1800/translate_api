package be.geo_solutions.translate_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket formApi() {
        // DocumentationType.SPRING_WEB does not seem to work?
        return new Docket(DocumentationType.SPRING_WEB)
                .select()
                .paths(regex("/api.*"))
                .apis(RequestHandlerSelectors.basePackage("be.geo_solutions.translate_api"))
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(metaInfo());
    }

    private ApiInfo metaInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Translate API",
                "A component made to simplify translation in projects.",
                "1.0",
                "",
                new Contact("", "", ""),
                "",
                "",
                Collections.emptyList()
        );
        return apiInfo;
    }
}
