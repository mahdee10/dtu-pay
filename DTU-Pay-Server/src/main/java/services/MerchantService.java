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
    private MessageQueue queue;
    private ConcurrentHashMap<CorrelationId, CompletableFuture<AccountEventMessage>> correlations = new ConcurrentHashMap<>();

    static MerchantService service = null;

    public static synchronized MerchantService getService() {
        if (service != null) {
            return service;
        }

        String hostname = System.getenv("Environment").equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        service = new MerchantService(mq);
        return service;
    }

    public MerchantService(MessageQueue q) {
        queue = q;
        queue.addHandler("MerchantCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantDeregistered", this::handleDeregisteredMerchant);
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
}
