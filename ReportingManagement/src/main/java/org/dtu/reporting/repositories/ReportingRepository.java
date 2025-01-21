package org.dtu.reporting.repositories;

import org.dtu.reporting.models.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReportingRepository {
    private final HashMap<UUID, Payment> payments = new HashMap<>();

    public void addPayment(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public List<Payment> getCustomerPayments(UUID customerId) {
        List<Payment> customerPayments = new ArrayList<>();
        for (Payment payment : payments.values()) {
            if (payment.getCustomerToken().equals(customerId)) {
                customerPayments.add(payment);
            }
        }
        return customerPayments;
    }

    public List<Payment> getMerchantPayments(UUID merchantId) {
        List<Payment> merchantPayments = new ArrayList<>();
        for (Payment payment : payments.values()) {
            if (payment.getMerchantId().equals(merchantId)) {
                merchantPayments.add(payment);
            }
        }
        return merchantPayments;
    }

    public boolean paymentExists(UUID paymentId){
        return payments.containsKey(paymentId);
    }

    public void clearPayments() {
        payments.clear();
    }
}
