package com.shopping_mall_api.global.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class TossPaymentConfig {
    @Value("${toss.payments.secret-key}")
    private String secretKey;

    @Value("${toss.payments.url}")
    private String baseUrl;

    // 타임아웃이 없으면 토스 응답 지연 시 요청 스레드·DB 커넥션·주문 행 잠금이 무기한 묶임
    // 값은 application.yml에 없으면 기본값 사용 (설정 파일 변경 없이 동작)
    @Value("${toss.payments.connect-timeout-ms:5000}")
    private long connectTimeoutMs;

    @Value("${toss.payments.read-timeout-ms:30000}")
    private long readTimeoutMs;

    @Bean
    public RestClient tossRestClient() {
        return tossRestClientBuilder().build();
    }

    // 테스트에서 MockRestServiceServer를 붙일 수 있도록 builder 구성을 분리
    public RestClient.Builder tossRestClientBuilder() {
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}
