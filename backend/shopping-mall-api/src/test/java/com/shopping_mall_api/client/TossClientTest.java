package com.shopping_mall_api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.payment.toss.TossPaymentRequestDTO;
import com.shopping_mall_api.dto.payment.toss.response.Payment;
import com.shopping_mall_api.dto.payment.toss.response.TossError;
import com.shopping_mall_api.dto.payment.toss.response.TossResponse;
import com.shopping_mall_api.global.client.toss.TossClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TossClientTest {
    private static final String BASE = "https://api.tosspayments.com/v1/payments";

    private MockRestServiceServer server;
    private TossClient tossClient;

    @BeforeEach
    void setUp(){
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        server = MockRestServiceServer.bindTo(builder).build();
        tossClient = new TossClient(builder.build(), new ObjectMapper());
    }

    @Test
    void confirm_success_returnsPayment(){
        server.expect(requestTo(BASE + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.paymentKey").value("pk"))
                .andRespond(withSuccess("""
                        {"paymentKey":"pk","orderId":"123456","status":"DONE","totalAmount":1000}
                        """, MediaType.APPLICATION_JSON));

        TossResponse response = tossClient.confirm(new TossPaymentRequestDTO("pk", 123456L, 1000L));

        assertThat(response).isInstanceOf(Payment.class);
        assertThat(((Payment) response).status()).isEqualTo("DONE");
        server.verify();
    }

    @Test
    void confirm_error_returnsTossError(){
        server.expect(requestTo(BASE + "/confirm"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"code":"ALREADY_PROCESSED_PAYMENT","message":"이미 처리된 결제 입니다."}
                                """));

        TossResponse response = tossClient.confirm(new TossPaymentRequestDTO("pk", 123456L, 1000L));

        assertThat(response).isEqualTo(new TossError("ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제 입니다."));
    }

    @Test
    void getPaymentByOrderId_callsOrderLookupApi(){
        server.expect(requestTo(BASE + "/orders/123456"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"paymentKey":"pk","orderId":"123456","status":"DONE","totalAmount":1000}
                        """, MediaType.APPLICATION_JSON));

        TossResponse response = tossClient.getPaymentByOrderId(123456L);

        assertThat(((Payment) response).paymentKey()).isEqualTo("pk");
        server.verify();
    }

    @Test
    void cancel_sendsReasonAndIdempotencyKey(){
        server.expect(requestTo(BASE + "/pk/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Idempotency-Key", "cancel-123456"))
                .andExpect(jsonPath("$.cancelReason").value("단순 변심"))
                .andRespond(withSuccess("""
                        {"paymentKey":"pk","orderId":"123456","status":"CANCELED","totalAmount":1000}
                        """, MediaType.APPLICATION_JSON));

        TossResponse response = tossClient.cancel("pk", "단순 변심", "cancel-123456");

        assertThat(((Payment) response).status()).isEqualTo("CANCELED");
        server.verify();
    }
}
