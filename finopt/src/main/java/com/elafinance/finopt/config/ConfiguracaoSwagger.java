package com.elafinance.finopt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoSwagger {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ElaFinance API")
                        .description("Sistema de otimização financeira com foco em autonomia financeira feminina")
                        .version("1.0.0"));
    }
}
