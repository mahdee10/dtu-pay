package services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import models.CorrelationId;
import models.dtos.PaymentRequestDto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

    private static final String GET_PAYMENTS_REQUESTED = "GetPaymentsRequested";
    private static final String PAYMENTS_FETCHED = "PaymentsFetched";
    private static final String GET_CUSTOMER_PAYMENTS_REQUESTED = "GetCustomerPaymentsRequested";
    private static final String CUSTOMER_PAYMENTS_FETCHED = "CustomerPaymentsFetched";
    private static final String GET_MERCHANT_PAYMENTS_REQUESTED = "GetMerchantPaymentsRequested";
    private static final String MERCHANT_PAYMENTS_FETCHED = "MerchantPaymentsFetched";
    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_COMPLETED = "PaymentCompleted";

    static PaymentService service = null;

    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<Boolean>> correlations = new ConcurrentHashMap<>();

    public static synchronized PaymentService getInstance() {
        if (service != null) {
            return service;
        }

        var mq = new RabbitMqQueue("localhost");
        service = new PaymentService(mq);
        return service;
    }

    public PaymentService(MessageQueue q) {
        queue = q;
        queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentComplete);
    }


    public Boolean pay(PaymentRequestDto paymentRequestDto) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<Boolean> futurePaymentRequestCompleted = new CompletableFuture<>();
        correlations.put(correlationId, futurePaymentRequestCompleted);

        Event event = new Event(PAYMENT_REQUESTED, new Object[] { correlationId, paymentRequestDto });
        queue.publish(event);

        return futurePaymentRequestCompleted.join();
    }

    private void handlePaymentComplete(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        boolean isPaymentSuccessfull = event.getArgument(1, Boolean.class);

        if (!isPaymentSuccessfull) {
            String exceptionMessage = event.getArgument(2, String.class);
            correlations.get(correlationId).completeExceptionally(new Exception(exceptionMessage));
            return;
        }

        UUID paymentId = event.getArgument(2, UUID.class);
        correlations.get(correlationId).complete(isPaymentSuccessfull);
    }
}
