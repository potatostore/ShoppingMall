package com.shopping_mall_api.dto.payment.toss;

public record TossPaymentResponseDTO (
    Payment payment,
    TossError tossError
){}
