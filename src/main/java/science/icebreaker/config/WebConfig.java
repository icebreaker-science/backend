package science.icebreaker.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class WebConfig {

    private final boolean development;


    public WebConfig(
            @Value("${icebreaker.development}") boolean development
    ) {
        this.development = development;
    }


    public Docket restAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/*"))
                .build();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (development) {
                    registry.addMapping("/**").allowedOrigins("*");
                }
            }
        };
    }

    @Bean
    // Prevents lazy loading of entities
    // in controllers which could potentially leak sensitive data
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }
}
