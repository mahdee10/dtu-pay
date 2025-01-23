package services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import models.AccountEventMessage;
import models.CorrelationId;
import models.dtos.CreateMerchantDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MerchantService {
    private static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    private static final String MERCHANT_CREATED = "MerchantCreated";
    private static final String MERCHANT_DEREGISTRATION_REQUESTED = "MerchantDeregistrationRequested";
    private static final String MERCHANT_DEREGISTERED = "MerchantDeregistered";
    private static final String VALIDATE_MERCHANT_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATION_RESPONSE = "MerchantAccountValidationResponse";

    private MessageQueue queue;
    private ConcurrentHashMap<CorrelationId, CompletableFuture<AccountEventMessage>> correlations = new ConcurrentHashMap<>();

    static MerchantService service = null;

    public static synchronized MerchantService getService() {
        if (service != null) {
            return service;
        }

        String environment = System.getenv("Environment");
        String hostname = environment != null && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        service = new MerchantService(mq);
        return service;
    }

    public MerchantService(MessageQueue q) {
        queue = q;
        queue.addHandler(MERCHANT_CREATED, this::handleMerchantCreated);
        queue.addHandler(MERCHANT_DEREGISTERED, this::handleDeregisteredMerchant);
        queue.addHandler(MERCHANT_ACCOUNT_VALIDATION_RESPONSE, this::handleMerchantAccountValidationResponse);
    }

    public AccountEventMessage createMerchant(CreateMerchantDto merchant) {
        CorrelationId correlationId = CorrelationId.randomId();
        correlations.put(correlationId, new CompletableFuture<>());

        AccountEventMessage eventMessage = new AccountEventMessage();
        eventMessage.setFirstName(merchant.getFirstName());
        eventMessage.setLastName(merchant.getLastName());
        eventMessage.setCpr(merchant.getCpr());
        eventMessage.setBankAccount(merchant.getBankAccountId());

        Event event = new Event("MerchantRegistrationRequested", new Object[]{ correlationId, eventMessage });
        queue.publish(event);

        return correlations.get(correlationId).join();
    }

    public void handleMerchantCreated(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = e.getArgument(1, AccountEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public AccountEventMessage deregisterMerchant(UUID merchantId) {
        CorrelationId correlationId = CorrelationId.randomId();
        correlations.put(correlationId, new CompletableFuture<>());

        AccountEventMessage eventMessage = new AccountEventMessage();
        eventMessage.setMerchantId(merchantId);

        Event event = new Event("MerchantDeregistrationRequested", new Object[]{ correlationId, eventMessage });
        queue.publish(event);

        return correlations.get(correlationId).join();
    }

    public void handleDeregisteredMerchant(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = e.getArgument(1, AccountEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    private void handleMerchantAccountValidationResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public AccountEventMessage validateMerchantAccount(UUID merchantId) {
        CorrelationId merchantValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<AccountEventMessage> futureMerchantValidation = new CompletableFuture<>();
        correlations.put(merchantValidationCorrelationId, futureMerchantValidation);

        AccountEventMessage accountEventMessage = new AccountEventMessage();
        accountEventMessage.setMerchantId(merchantId);

        Event merchantAccountValidationEvent = new Event(VALIDATE_MERCHANT_ACCOUNT_REQUESTED,
                new Object[] { merchantValidationCorrelationId, accountEventMessage });
        queue.publish(merchantAccountValidationEvent);

        return futureMerchantValidation.join();
    }
}
