package com.shopping_mall_api.global.client.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.payment.toss.TossPaymentRequestDTO;
import com.shopping_mall_api.dto.payment.toss.response.Payment;
import com.shopping_mall_api.dto.payment.toss.response.TossError;
import com.shopping_mall_api.dto.payment.toss.response.TossResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Getter
@RequiredArgsConstructor
@Component
public class TossClient {
    private final RestClient tossRestClient;
    private final ObjectMapper objectMapper;

    public TossResponse confirm(TossPaymentRequestDTO tossPaymentRequestDTO){
        return tossRestClient.post()
                .uri("/confirm")
                .body(tossPaymentRequestDTO)
                .exchange((req, res) -> {
                    if (res.getStatusCode().is2xxSuccessful()) {
                        return objectMapper.readValue(res.getBody(), Payment.class);
                    } else {
                        return objectMapper.readValue(res.getBody(), TossError.class);
                    }
                });
    }
}
