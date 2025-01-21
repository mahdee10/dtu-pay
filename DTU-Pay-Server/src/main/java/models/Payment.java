package models;

import java.util.UUID;

public class Payment {

    private UUID id;
    private UUID customerToken;
    private UUID merchantId;
    private double amount;

    public Payment() {}

    public UUID getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getCustomerToken() {
        return customerToken;
    }

    public UUID getMerchantId() {
        return merchantId;
    }

}
