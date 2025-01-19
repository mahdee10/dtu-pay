package dtu.dtuPay.repositeries;

import dtu.dtuPay.models.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PaymentRepository {
    private static PaymentRepository instance;
    private HashMap<UUID, Payment> payments;
    // <UUID paymentId, UUID customerId>
    private HashMap<UUID, List<UUID>> customerPayments = new HashMap<>();;
    // <UUID paymentId, UUID merchantId>
    private HashMap<UUID, List<UUID>> merchantPayments = new HashMap<>();;

    private PaymentRepository() {
        this.payments = new HashMap<>();
    }

    public static PaymentRepository getInstance() {
        if (instance == null) {
            synchronized (PaymentRepository.class) {
                if (instance == null) {
                    instance = new PaymentRepository();
                }
            }
        }
        return instance;
    }

    public void addPayment(Payment payment) {
        this.payments.put(payment.getId(), payment);
    }

    public List<Payment> getPayments() {
        List<Payment> paymentList = new ArrayList<>(this.payments.values());
        return paymentList;
    }

    public List<Payment> getCustomerPayments(UUID customerId) {
        List<UUID> customerPaymnetsIdList = customerPayments.get(customerId);
        List<Payment> paymentList = getPayments().stream().filter(
                payment -> customerPaymnetsIdList.contains(payment.getId())
        ).toList();

        return new ArrayList<>(paymentList);
    }

    public List<Payment> getMerchantPayments(UUID merchantId) {
        List<UUID> merchantPaymnetsIdList = merchantPayments.get(merchantId);
        List<Payment> paymentList = getPayments().stream().filter(
                payment -> merchantPaymnetsIdList.contains(payment.getId())
        ).toList();

        return new ArrayList<>(paymentList);
    }

    public void addCustomerPayment(UUID customerId, UUID paymentId) {
        List<UUID> customerPaymnetsIdList = customerPayments.get(customerId);

        if (customerPaymnetsIdList == null) {
            customerPaymnetsIdList = new ArrayList<>();
            customerPayments.put(customerId, customerPaymnetsIdList);
        }

        customerPaymnetsIdList.add(paymentId);
    }

    public void addMerchantPayment(UUID merchantId, UUID paymentId) {
        List<UUID> merchantPaymentsIdList = merchantPayments.get(merchantId);

        if (merchantPaymentsIdList == null) {
            merchantPaymentsIdList = new ArrayList<>();
            merchantPayments.put(merchantId, merchantPaymentsIdList);
        }

        merchantPaymentsIdList.add(paymentId);
    }

    public void dropData() {
        this.payments.clear();
        this.customerPayments.clear();
        this.merchantPayments.clear();
    }
}
