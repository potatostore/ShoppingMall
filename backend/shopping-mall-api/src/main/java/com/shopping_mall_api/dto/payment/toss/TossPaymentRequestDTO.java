package com.shopping_mall_api.dto.payment.toss;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TossPaymentRequestDTO(
    @NotBlank(message = "paymentKey must not be blank")
    String paymentKey,

    @NotNull(message = "orderId must not be null")
    Long orderId,

    @NotNull(message = "amount must not be null")
    @Min(value = 0, message = "amount must be at least 0")
    Long amount
) {}
