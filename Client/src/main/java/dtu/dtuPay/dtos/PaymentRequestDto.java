package dtu.dtuPay.dtos;

import java.util.UUID;

public class PaymentRequestDto {

    private UUID customerToken;
    private UUID merchantId;
    private double amount;

    public PaymentRequestDto() {}

    public PaymentRequestDto(UUID customerToken, UUID merchantId, double amount) {
        this.customerToken = customerToken;
        this.merchantId = merchantId;
        this.amount = amount;
    }

    public UUID getCustomerToken() {
        return this.customerToken;
    }

    public void setCustomerToken(UUID customerToken) {
        this.customerToken = customerToken;
    }

    public UUID getMerchantId() {
        return this.merchantId;
    }

    public void setMerchantId(UUID merchantId) {
        this.merchantId = merchantId;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
