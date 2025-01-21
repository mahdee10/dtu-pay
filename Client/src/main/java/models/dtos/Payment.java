package models.dtos;

import java.util.UUID;

public class Payment {
    private UUID id;
    private UUID customerToken;
    private UUID merchantId;
    private double amount;

    // Getters and Setters
    public UUID getCustomerToken() {
        return customerToken;
    }

    public void setCustomerToken(UUID customerToken) {
        this.customerToken = customerToken;
    }

    public UUID getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(UUID merchantId) {
        this.merchantId = merchantId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
