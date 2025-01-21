package org.dtu.reporting.services;

import com.google.gson.Gson;
import messaging.Event;
import messaging.MessageQueue;
import org.dtu.reporting.models.CorrelationId;
import org.dtu.reporting.models.Payment;
import org.dtu.reporting.repositories.ReportingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportingService {
    private final MessageQueue messageQueue;
    private final ReportingRepository reportingRepository = new ReportingRepository();
    private final Gson gson = new Gson();
    private HashMap<CorrelationId, CompletableFuture<String>> correlations = new HashMap<>();

    public ReportingService(MessageQueue queue) {
        reportingRepository.clearPayments();
        messageQueue = queue;

        // Add handlers for various events
        messageQueue.addHandler("PaymentsFetched", this::handlePaymentsFetched);
        messageQueue.addHandler("CustomerPaymentsFetched", this::handleCustomerPaymentsFetched);
        messageQueue.addHandler("MerchantPaymentsFetched", this::handleMerchantPaymentsFetched);
    }

    public void requestPayments() {
        var correlationId = CorrelationId.randomId();
        var futureCompletableObject = new CompletableFuture<String>();
        correlations.put(correlationId, futureCompletableObject);

        Event event = new Event("GetPaymentsRequested", new Object[]{correlationId});
        messageQueue.publish(event);
    }

    public void requestCustomerPayments(UUID customerId) {
        var correlationId = CorrelationId.randomId();
        var futureCompletableObject = new CompletableFuture<String>();
        correlations.put(correlationId, futureCompletableObject);

        Event event = new Event("GetCustomerPaymentsRequested", new Object[]{correlationId, customerId});
        messageQueue.publish(event);
    }

    public void requestMerchantPayments(UUID merchantId) {
        var correlationId = CorrelationId.randomId();
        var futureCompletableObject = new CompletableFuture<String>();
        correlations.put(correlationId, futureCompletableObject);

        Event event = new Event("GetMerchantPaymentsRequested", new Object[]{correlationId, merchantId});
        messageQueue.publish(event);
    }

    private void handlePaymentsFetched(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);

        var jsonPaymentList = event.getArgument(1, String.class);

        iteratePayments(jsonPaymentList);

        correlations.get(correlationId).complete(jsonPaymentList);
    }

    private void handleCustomerPaymentsFetched(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);

        var jsonPaymentList = event.getArgument(1, String.class);

        iteratePayments(jsonPaymentList);

        correlations.get(correlationId).complete(jsonPaymentList);
    }

    private void handleMerchantPaymentsFetched(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);

        var jsonPaymentList = event.getArgument(1, String.class);

        iteratePayments(jsonPaymentList);

        correlations.get(correlationId).complete(jsonPaymentList);
    }

    private void iteratePayments(String jsonPaymentList) {
        Payment[] payments = gson.fromJson(jsonPaymentList, Payment[].class);
        for (Payment payment : payments) {
            if (!reportingRepository.paymentExists(payment.getId())) {
                reportingRepository.addPayment(payment);
            } else {
                System.out.println("Duplicate payment detected, skipping: " + payment.getId());
            }
        }
    }

    public List<Payment> getAllPayments() {
        return reportingRepository.getAllPayments();
    }

    public List<Payment> getCustomerPayments(UUID customerId) {
        return reportingRepository.getCustomerPayments(customerId);
    }

    public List<Payment> getMerchantPayments(UUID merchantId) {
        return reportingRepository.getMerchantPayments(merchantId);
    }

}
