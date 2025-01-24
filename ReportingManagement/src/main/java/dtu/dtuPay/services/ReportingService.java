/**
 * @author Raihanullah Mehran s233837
 */
package dtu.dtuPay.services;

import messaging.Event;
import messaging.MessageQueue;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.PaymentEventMessage;
import dtu.dtuPay.models.ReportingEventMessage;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportingService {
    private static final String REPORTING_GET_PAYMENTS_REQUESTED = "ReportingGetPaymentsRequested";
    private static final String REPORTING_GET_PAYMENTS_FETCHED = "ReportingGetPaymentsFetched";
    private static final String REPORTING_GET_CUSTOMER_PAYMENTS_REQUESTED = "ReportingGetCustomerPaymentsRequested";
    private static final String REPORTING_CUSTOMER_PAYMENTS_FETCHED = "ReportingCustomerPaymentsFetched";
    private static final String REPORTING_GET_MERCHANT_PAYMENTS_REQUESTED = "ReportingGetMerchantPaymentsRequested";
    private static final String REPORTING_MERCHANT_PAYMENTS_FETCHED = "ReportingMerchantPaymentsFetched";

    private static final String GET_PAYMENTS_REQUESTED = "GetPaymentsRequested";
    private static final String PAYMENTS_FETCHED = "PaymentsFetched";
    private static final String GET_CUSTOMER_PAYMENTS_REQUESTED = "GetCustomerPaymentsRequested";
    private static final String CUSTOMER_PAYMENTS_FETCHED = "CustomerPaymentsFetched";
    private static final String GET_MERCHANT_PAYMENTS_REQUESTED = "GetMerchantPaymentsRequested";
    private static final String MERCHANT_PAYMENTS_FETCHED = "MerchantPaymentsFetched";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    private final MessageQueue messageQueue;
    public HashMap<CorrelationId, CompletableFuture<PaymentEventMessage>> paymentCorrelations = new HashMap<>();

    public ReportingService(MessageQueue queue) {
        messageQueue = queue;

        messageQueue.addHandler(REPORTING_GET_PAYMENTS_REQUESTED, this::handleReportingGetPaymentsRequested);
        messageQueue.addHandler(REPORTING_GET_CUSTOMER_PAYMENTS_REQUESTED, this::handleReportingGetCustomerPaymentsRequested);
        messageQueue.addHandler(REPORTING_GET_MERCHANT_PAYMENTS_REQUESTED, this::handleReportingGetMerchantPaymentsRequested);

        // Add handlers for various events
        messageQueue.addHandler(PAYMENTS_FETCHED, this::handlePaymentsFetched);
        messageQueue.addHandler(CUSTOMER_PAYMENTS_FETCHED, this::handleCustomerPaymentsFetched);
        messageQueue.addHandler(MERCHANT_PAYMENTS_FETCHED, this::handleMerchantPaymentsFetched);
    }

    private void processResponseEventMessage(CorrelationId correlationId, ReportingEventMessage eventMessage,
                                             PaymentEventMessage paymentEventMessage, String eventName) {
        if (paymentEventMessage.getRequestResponseCode() == OK) {
            eventMessage.setPaymentList(paymentEventMessage.getPaymentList());
            eventMessage.setRequestResponseCode(OK);
        } else {
            eventMessage.setRequestResponseCode(paymentEventMessage.getRequestResponseCode());
            eventMessage.setExceptionMessage(paymentEventMessage.getExceptionMessage());
        }

        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });
        messageQueue.publish(event);
    }

    // Get all payments
    public void handleReportingGetPaymentsRequested(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        ReportingEventMessage eventMessage = e.getArgument(1, ReportingEventMessage.class);

        PaymentEventMessage paymentEventMessage = requestPayments();
        processResponseEventMessage(correlationId, eventMessage, paymentEventMessage, REPORTING_GET_PAYMENTS_FETCHED);
    }

    private void handlePaymentsFetched(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = event.getArgument(1, PaymentEventMessage.class);

        paymentCorrelations.get(correlationId).complete(eventMessage);
    }

    public PaymentEventMessage requestPayments() {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<PaymentEventMessage> futureCompletableObject = new CompletableFuture<>();
        paymentCorrelations.put(correlationId, futureCompletableObject);

        PaymentEventMessage eventMessage = new PaymentEventMessage();

        Event event = new Event(GET_PAYMENTS_REQUESTED, new Object[] { correlationId, eventMessage });
        messageQueue.publish(event);

        return futureCompletableObject.join();
    }

    // Customer Payments
    public void handleReportingGetCustomerPaymentsRequested(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        ReportingEventMessage eventMessage = e.getArgument(1, ReportingEventMessage.class);

        PaymentEventMessage paymentEventMessage = requestCustomerPayments(eventMessage.getCustomerId());
        processResponseEventMessage(correlationId, eventMessage, paymentEventMessage, REPORTING_CUSTOMER_PAYMENTS_FETCHED);
    }

    private void handleCustomerPaymentsFetched(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = event.getArgument(1, PaymentEventMessage.class);

        paymentCorrelations.get(correlationId).complete(eventMessage);
    }

    public PaymentEventMessage requestCustomerPayments(UUID customerId) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<PaymentEventMessage> futureCompletableObject = new CompletableFuture<>();
        paymentCorrelations.put(correlationId, futureCompletableObject);

        PaymentEventMessage eventMessage = new PaymentEventMessage();
        eventMessage.setCustomerId(customerId);

        Event event = new Event(GET_CUSTOMER_PAYMENTS_REQUESTED, new Object[] { correlationId, eventMessage });
        messageQueue.publish(event);

        return futureCompletableObject.join();
    }

    // Get merchant payments
    public void handleReportingGetMerchantPaymentsRequested(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        ReportingEventMessage eventMessage = e.getArgument(1, ReportingEventMessage.class);

        PaymentEventMessage paymentEventMessage = requestMerchantPayments(eventMessage.getMerchantId());
        processResponseEventMessage(correlationId, eventMessage, paymentEventMessage, REPORTING_MERCHANT_PAYMENTS_FETCHED);
    }

    private void handleMerchantPaymentsFetched(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = event.getArgument(1, PaymentEventMessage.class);

        paymentCorrelations.get(correlationId).complete(eventMessage);
    }

    public PaymentEventMessage requestMerchantPayments(UUID merchantId) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<PaymentEventMessage> futureCompletableObject = new CompletableFuture<>();
        paymentCorrelations.put(correlationId, futureCompletableObject);

        PaymentEventMessage eventMessage = new PaymentEventMessage();
        eventMessage.setMerchantId(merchantId);

        Event event = new Event(GET_MERCHANT_PAYMENTS_REQUESTED, new Object[] { correlationId, eventMessage });
        messageQueue.publish(event);

        return futureCompletableObject.join();
    }
}
