package com.shopping_mall_api.dto.payment.toss.response;

public record TossError (
    String code,
    String message
) implements TossResponse{}
