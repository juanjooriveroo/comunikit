package apigateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    /**
     * Configuración personalizada de OpenAPI para el API Gateway
     * 
     * @return OpenAPI configuración personalizada de Swagger/OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ComuniKit API Gateway")
                        .version("1.0.0")
                        .description("API Gateway centralizado para todos los microservicios de ComuniKit")
                        .contact(new Contact()
                                .name("ComuniKit Team")
                                .email("jurrilo.25.22.github@gmail.com"))
                        .license(new License()
                                .name("Creative Commons NonCommercial 4.0 International")
                                .url("https://github.com/juanjooriveroo/comunikit/blob/main/LICENSE")))
                .servers(List.of(
                        new Server()
                                .url("https://back.comunikit.duckdns.org")
                                .description("Production")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtenido del endpoint /auth/login")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt"));
    }
}