package models.dtos;

public class PaymentRequestDto {

    private String customerToken;
    private String merchantId;
    private double amount;

    public PaymentRequestDto() {}

    public PaymentRequestDto(String customerToken, String merchantId, double amount) {
        this.customerToken = customerToken;
        this.merchantId = merchantId;
        this.amount = amount;
    }

    public String getCustomerToken() {
        return this.customerToken;
    }

    public void setCustomerToken(String customerToken) {
        this.customerToken = customerToken;
    }

    public String getMerchantId() {
        return this.merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
