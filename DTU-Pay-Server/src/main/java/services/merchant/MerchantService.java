package services.merchant;

import messaging.Event;
import messaging.MessageQueue;
import models.CorrelationId;
import models.dtos.CreateMerchantDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MerchantService {
    private MessageQueue queue;
    private ConcurrentHashMap<CorrelationId, CompletableFuture<UUID>> registeredMerchantCorrelations = new ConcurrentHashMap<>();
    private ConcurrentHashMap<CorrelationId, CompletableFuture<Boolean>> deregisteredMerchantCorrelations = new ConcurrentHashMap<>();

    public MerchantService(MessageQueue q) {
        queue = q;
        queue.addHandler("MerchantCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantDeregistered", this::handleDeregisteredMerchant);
    }

    public UUID createMerchant(CreateMerchantDto merchant) {
        CorrelationId correlationId = CorrelationId.randomId();
        registeredMerchantCorrelations.put(correlationId, new CompletableFuture<UUID>());

        Event event = new Event("MerchantRegistrationRequested", new Object[]{correlationId, merchant});
        queue.publish(event);

        return registeredMerchantCorrelations.get(correlationId).join();
    }

    public void handleMerchantCreated(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        UUID merchantId = e.getArgument(1, UUID.class);

        registeredMerchantCorrelations.get(correlationId).complete(merchantId);
    }

    public boolean deregisterMerchant(UUID merchantId) {
        CorrelationId correlationId = CorrelationId.randomId();
        deregisteredMerchantCorrelations.put(correlationId, new CompletableFuture<Boolean>());

        Event event = new Event("MerchantDeregistrationRequested", new Object[]{correlationId, merchantId});
        queue.publish(event);

        return deregisteredMerchantCorrelations.get(correlationId).join();
    }

    public void handleDeregisteredMerchant(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        Boolean isDeregistered = e.getArgument(1, Boolean.class);

        deregisteredMerchantCorrelations.get(correlationId).complete(isDeregistered);


    }
}
