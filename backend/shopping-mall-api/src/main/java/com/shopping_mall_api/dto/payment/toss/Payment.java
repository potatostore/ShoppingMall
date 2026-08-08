package com.shopping_mall_api.dto.payment.toss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping_mall_api.global.config.CheckConfig;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
enum Status {
    READY("READY"),
    IN_PROGRESS("IN_PROGRESS"),
    WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT"),
    DONE("DONE"),
    CANCELED("CANCELED"),
    PARTIAL_CANCELED("PARTIAL_CANCELED"),
    ABORTED("ABORTED"),
    EXPIRED("EXPIRED");

    private final String message;

    Status(String message) {
        CheckConfig.npeAndBlankCheck(message, "message");
        this.message = message;
    }
}

@Getter
enum AcquireStatus {
    READY("READY"),
    REQUESTED("REQUESTED"),
    COMPLETED("COMPLETED"),
    CANCEL_REQUESTED("CANCEL_REQUESTED"),
    CANCELED("CANCELED");

    private final String message;

    AcquireStatus(String message) {
        CheckConfig.npeAndBlankCheck(message, "message");
        this.message = message;
    }
}

@Getter
enum RefundStatus {
    NONE("NONE"),
    PENDING("PENDING"),
    FAILED("FAILED"),
    PARTIAL_FAILED("PARTIAL_FAILED"),
    COMPLETED("COMPLETED");

    private final String message;

    RefundStatus(String message) {
        CheckConfig.npeAndBlankCheck(message, "message");
        this.message = message;
    }
}

@Getter
enum SettlementStatus {
    INCOMPLETED("INCOMPLETED"),
    COMPLETED("COMPLETED");

    private final String message;

    SettlementStatus(String message) {
        CheckConfig.npeAndBlankCheck(message, "message");
        this.message = message;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
public record Payment(
        String version,
        String paymentKey,
        String type,
        String orderId,
        String orderName,
        String mId,
        String currency,
        String method,
        Long totalAmount,
        Long balanceAmount,
        String status,
        String requestedAt,
        String approvedAt,
        boolean useEscrow,
        String lastTransactionKey,
        Long suppliedAmount,
        Long vat,
        boolean cultureExpense,
        Long taxFreeAmount,
        Long taxExemptionAmount,
        List<Cancel> cancels,
        boolean isPartialCancelable,
        Card card,
        VirtualAccount virtualAccount,
        String secret,
        MobilePhone mobilePhone,
        GiftCertificate giftCertificate,
        Transfer transfer,
        Map<String, String> metadata,
        Receipt receipt,
        CheckOut checkOut,
        EasyPay easyPay,
        String country,
        Failure failure,
        CashReceipt cashReceipt,
        List<CashReceipt> cashReceipts,
        Discount discount
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cancel(
            Long cancelAmount,
            String cancelReason,
            Long taxFreeAmount,
            Long taxExemptionAmount,
            Long refundableAmount,
            Long cartDiscountAmount,
            Long transferDiscountAmount,
            Long easyPayDiscountAmount,
            String canceledAt,
            String transactionKey,
            String receiptKey,
            String cancelStatus,
            String cancelRequestId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Card(
            Long amount,
            String issuerCode,
            String acquirerCode,
            String number,
            Long installmentPlanMonths,
            String approveNo,
            boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus,
            boolean isInterestFree,
            String interestPayer
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RefundReceiveAccount(
            String bankCode,
            String accountNumber,
            String holderName
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VirtualAccount(
            String accountType,
            String accountNumber,
            String bankCode,
            String customerName,
            String depositorName,
            String dueDate,
            String refundStatus,
            boolean expired,
            String settlementStatus,
            RefundReceiveAccount refundReceiveAccount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MobilePhone(
            String customerMobilePhone,
            String settlementStatus,
            String receiptUrl
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GiftCertificate(
            String approveNo,
            String settlementStatus,
            String receiptUrl
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Transfer(
            String bankCode,
            String settlementStatus
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Receipt(
            String url
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CheckOut(
            String url
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EasyPay(
            String provider,
            Long amount,
            Long discountAmount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Failure(
            String code,
            String message
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CashReceipt(
            String type,
            String receiptKey,
            String issueNumber,
            String receiptUrl,
            Long number,
            Long taxFreeAmount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Discount(
            Long amount
    ) {}
}