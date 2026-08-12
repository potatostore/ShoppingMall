package com.shopping_mall_api.global.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class TossPaymentConfig {
    @Value("${toss.payments.secret-key}")
    private String secretKey;

    @Value("${toss.payments.url}")
    private String baseUrl;

    @Bean
    public RestClient tossRestClient() {
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        // RestClient 인터페이스의 정적 메서드 .builder()를 직접 호출
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Basic " + encodedKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
