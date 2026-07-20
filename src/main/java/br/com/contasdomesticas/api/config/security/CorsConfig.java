package br.com.contasdomesticas.api.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.dev-patterns-enabled:false}")
    private boolean devPatternsEnabled;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens configuráveis via propriedade
        List<String> origins = new ArrayList<>(List.of(allowedOrigins.split(",")));

        // Padrões para desenvolvimento (apenas quando habilitado)
        if (devPatternsEnabled) {
            origins.add("http://localhost:*");
            origins.add("http://127.0.0.1:*");
            origins.add("http://192.168.*.*");
            origins.add("https://192.168.*.*");
        }

        config.setAllowedOriginPatterns(origins);

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Headers permitidos
        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-CSRF-TOKEN",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "Cache-Control",
            "Pragma"
        ));

        // Headers expostos
        config.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-CSRF-TOKEN",
            "Content-Disposition",
            "X-Total-Count"
        ));

        // Credenciais (importante para JWT/cookies)
        config.setAllowCredentials(true);

        // Cache do preflight (1 hora)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
