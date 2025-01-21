package org.dtu.reporting.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Data
public class PaymentEventMessage {

    private Boolean isValid;
    private int requestResponseCode;
    private String exceptionMessage;
    private String merchantBankAccount;
    private String customerBankAccount;
    private UUID customerToken;
    private UUID merchantId;
    private UUID customerId;
    private double amount;
    private UUID paymentId;
    private List<Payment> paymentList;

    public PaymentEventMessage() {}
}
