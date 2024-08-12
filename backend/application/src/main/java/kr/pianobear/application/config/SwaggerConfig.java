package kr.pianobear.application.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private final String serverUrl;

    public SwaggerConfig(@Value("${application.service-url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        Components components = new Components()
                .addSecuritySchemes(jwt, new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        Server server = new Server();
        server.setUrl(serverUrl);

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components)
                .servers(List.of(server));
    }

    private Info apiInfo() {
        return new Info()
                .title("PianoBear API")
                .description("REST API for PianoBear")
                .version("1.0.0");
    }

    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("Authentication")
                .pathsToMatch("/api/v1/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi musicGroup() {
        return GroupedOpenApi.builder()
                .group("Music API")
                .pathsToMatch("/api/v1/music/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userGroup() {
        return GroupedOpenApi.builder()
                .group("User")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fileGroup() {
        return GroupedOpenApi.builder()
                .group("File")
                .pathsToMatch("/api/v1/files/**")
                .build();
    }

    @Bean
    public GroupedOpenApi dashboardGroup() {
        return GroupedOpenApi.builder()
                .group("Dashboard")
                .pathsToMatch("/api/v1/dashboard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi friendGroup() {
        return GroupedOpenApi.builder()
                .group("Friend")
                .pathsToMatch("/api/v1/friends/**")
                .build();
    }

    @Bean
    public GroupedOpenApi transcriberGroup() {
        return GroupedOpenApi.builder()
                .group("Transcriber")
                .pathsToMatch("/api/v1/transcriber/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationGroup() {
        return GroupedOpenApi.builder()
                .group("Notification")
                .pathsToMatch("/api/v1/notifications/**")
                .build();
    }

    @Bean
    public GroupedOpenApi communityGroup() {
        return GroupedOpenApi.builder()
                .group("Openvidu")
                .pathsToMatch("/api/v1/community/**")
                .build();
    }

    @Bean
    public GroupedOpenApi profileGroup() {
        return GroupedOpenApi.builder()
                .group("Profile")
                .pathsToMatch("/api/v1/profile/**")
                .build();
    }

    @Bean
    public GroupedOpenApi chatGroup() {
        return GroupedOpenApi.builder()
                .group("chat")
                .pathsToMatch("/api/v1/ws/**")
                .build();
    }
}
