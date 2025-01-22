package services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import models.CorrelationId;
import models.ReportingEventMessage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ReportingService {
    private static final String REPORTING_GET_PAYMENTS_REQUESTED = "ReportingGetPaymentsRequested";
    private static final String REPORTING_GET_PAYMENTS_FETCHED = "ReportingGetPaymentsFetched";
    private static final String REPORTING_GET_CUSTOMER_PAYMENTS_REQUESTED = "ReportingGetCustomerPaymentsRequested";
    private static final String REPORTING_CUSTOMER_PAYMENTS_FETCHED = "ReportingCustomerPaymentsFetched";
    private static final String REPORTING_GET_MERCHANT_PAYMENTS_REQUESTED = "ReportingGetMerchantPaymentsRequested";
    private static final String REPORTING_MERCHANT_PAYMENTS_FETCHED = "ReportingMerchantPaymentsFetched";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    static ReportingService service = null;

    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<ReportingEventMessage>> correlations = new ConcurrentHashMap<>();

    public static synchronized ReportingService getInstance() {
        if (service != null) {
            return service;
        }

        String environment = System.getenv("Environment");
        String hostname = !environment.isEmpty() && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        service = new ReportingService(mq);
        return service;
    }

    public ReportingService(MessageQueue q) {
        queue = q;
        queue.addHandler(REPORTING_GET_PAYMENTS_FETCHED, this::handleReportingServiceResponse);
        queue.addHandler(REPORTING_CUSTOMER_PAYMENTS_FETCHED, this::handleReportingServiceResponse);
        queue.addHandler(REPORTING_MERCHANT_PAYMENTS_FETCHED, this::handleReportingServiceResponse);
    }

    private void handleReportingServiceResponse(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        ReportingEventMessage eventMessage = e.getArgument(1, ReportingEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public ReportingEventMessage getAllPayments() {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<ReportingEventMessage> getPaymentsFuture = correlations.get(correlationId);
        correlations.put(correlationId, getPaymentsFuture);
        ReportingEventMessage eventMessage = new ReportingEventMessage();

        Event event = new Event(REPORTING_GET_PAYMENTS_REQUESTED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
        return getPaymentsFuture.join();
    }

    public ReportingEventMessage getAllCustomerPayments(UUID customerId) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<ReportingEventMessage> getCustomerPaymentsFuture = correlations.get(correlationId);
        correlations.put(correlationId, getCustomerPaymentsFuture);

        ReportingEventMessage eventMessage = new ReportingEventMessage();
        eventMessage.setCustomerId(customerId);

        Event event = new Event(REPORTING_GET_CUSTOMER_PAYMENTS_REQUESTED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
        return getCustomerPaymentsFuture.join();
    }

    public ReportingEventMessage getAllMerchantPayments(UUID merchantId) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<ReportingEventMessage> getCustomerPaymentsFuture = correlations.get(correlationId);
        correlations.put(correlationId, getCustomerPaymentsFuture);

        ReportingEventMessage eventMessage = new ReportingEventMessage();
        eventMessage.setMerchantId(merchantId);

        Event event = new Event(REPORTING_GET_MERCHANT_PAYMENTS_REQUESTED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
        return getCustomerPaymentsFuture.join();
    }
}
