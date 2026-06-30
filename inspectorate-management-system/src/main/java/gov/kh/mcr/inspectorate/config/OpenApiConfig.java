package gov.kh.mcr.inspectorate.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.public-url:http://localhost:8080}")
    private String publicUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ប្រព័ន្ធគ្រប់គ្រងអគ្គាធិការដ្ឋាន")
                        .description(
                                "REST API Inspectorate Management System\n\n"
                                        + "**Default Credentials:**\n")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("System Administrator")
                                .email("system.supperadmin@inspectorate.gov.kh")))
                .servers(List.of(
                        new Server()
                                .url(publicUrl)
                                .description("API Server")))
                .addSecurityItem(
                        new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description(
                                                "បញ្ចូល JWT Token: Bearer {token}")));
    }
}