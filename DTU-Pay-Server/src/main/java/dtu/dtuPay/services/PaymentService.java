package dtu.dtuPay.services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.PaymentEventMessage;
import dtu.dtuPay.models.TokenEventMessage;
import dtu.dtuPay.models.dtos.PaymentRequestDto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_COMPLETED = "PaymentCompleted";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    static PaymentService paymentService = null;
    static MerchantService merchantService = MerchantService.getService();
    static TokenService tokenService = TokenService.getInstance();

    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<PaymentEventMessage>> correlations = new ConcurrentHashMap<>();

    public static synchronized PaymentService getInstance() {
        if (paymentService != null) {
            return paymentService;
        }

        String environment = System.getenv("Environment");
        String hostname = environment != null && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        paymentService = new PaymentService(mq);
        return paymentService;
    }

    public PaymentService(MessageQueue q) {
        queue = q;
        queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentComplete);
    }

    private void handlePaymentComplete(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = event.getArgument(1, PaymentEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public PaymentEventMessage pay(PaymentRequestDto paymentRequestDto) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<PaymentEventMessage> futurePaymentRequestCompleted = new CompletableFuture<>();
        correlations.put(correlationId, futurePaymentRequestCompleted);

        PaymentEventMessage eventMessage = new PaymentEventMessage();
        eventMessage.setCustomerToken(UUID.fromString(paymentRequestDto.getCustomerToken()));
        eventMessage.setMerchantId(UUID.fromString(paymentRequestDto.getMerchantId()));
        eventMessage.setAmount(paymentRequestDto.getAmount());

        // Merchant account validation
        AccountEventMessage responseMerchantValidation = merchantService.validateMerchantAccount(eventMessage.getMerchantId());
        if (!responseMerchantValidation.getIsValidAccount() || responseMerchantValidation.getRequestResponseCode() != OK) {
            eventMessage.setExceptionMessage(responseMerchantValidation.getExceptionMessage());
            eventMessage.setRequestResponseCode(BAD_REQUEST);

            return eventMessage;
        }

        // Customer Token Validation
        TokenEventMessage responseValidateCustomerToken = tokenService.useToken(eventMessage.getCustomerToken());
        if (responseValidateCustomerToken.getRequestResponseCode() != OK || !responseValidateCustomerToken.getIsValid()) {
            eventMessage.setExceptionMessage(responseValidateCustomerToken.getExceptionMessage());
            eventMessage.setRequestResponseCode(BAD_REQUEST);

            return eventMessage;
        }

        eventMessage.setMerchantBankAccount(responseMerchantValidation.getBankAccount());
        eventMessage.setCustomerBankAccount(responseValidateCustomerToken.getBankAccount());
        eventMessage.setCustomerId(responseValidateCustomerToken.getCustomerId());

        Event event = new Event(PAYMENT_REQUESTED, new Object[] { correlationId, eventMessage });
        queue.publish(event);

        return futurePaymentRequestCompleted.join();
    }
}
