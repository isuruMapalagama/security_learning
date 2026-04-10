package learning.security_learning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${profile.service.url}")
    private String profileServiceUrl;

    @Bean
    public WebClient profileServiceClient(){
        return WebClient.builder()
                .baseUrl(profileServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
