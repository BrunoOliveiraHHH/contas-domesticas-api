package br.com.contasdomesticas.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI contasDomesticasOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Contas Domesticas API")
                .description("API de sincronizacao do aplicativo Contas Domesticas")
                .version("v1"));
    }
}
