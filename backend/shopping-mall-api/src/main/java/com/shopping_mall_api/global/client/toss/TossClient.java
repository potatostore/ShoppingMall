package com.shopping_mall_api.global.client.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.payment.toss.TossPaymentRequestDTO;
import com.shopping_mall_api.dto.payment.toss.response.Payment;
import com.shopping_mall_api.dto.payment.toss.response.TossError;
import com.shopping_mall_api.dto.payment.toss.response.TossResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

// 네트워크 오류·타임아웃은 ResourceAccessException으로 그대로 던짐 → 호출하는 서비스가 결제 조회로 실제 상태를 확인
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
                .exchange((req, res) -> toTossResponse(res));
    }

    // 주문번호(orderUid)로 결제 조회: GET /v1/payments/orders/{orderId}
    // 승인 응답이 유실됐을 때 토스 쪽 실제 결제 상태를 확인하는 데 사용 (없으면 TossError NOT_FOUND_PAYMENT)
    public TossResponse getPaymentByOrderId(Long orderUid){
        return tossRestClient.get()
                .uri("/orders/{orderId}", orderUid)
                .exchange((req, res) -> toTossResponse(res));
    }

    private TossResponse toTossResponse(ClientHttpResponse res) throws IOException {
        if (res.getStatusCode().is2xxSuccessful()) {
            return objectMapper.readValue(res.getBody(), Payment.class);
        }
        return objectMapper.readValue(res.getBody(), TossError.class);
    }
}
