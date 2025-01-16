package services.merchant;

import messaging.Event;
import messaging.MessageQueue;
import models.dtos.CreateMerchantDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MerchantService {
    private MessageQueue queue;
    private CompletableFuture<UUID> registeredMerchant;
    private CompletableFuture<Boolean> deregisteredMerchant;

    public MerchantService(MessageQueue q) {
        queue = q;
        queue.addHandler("MerchantCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantDeregistered", this::handleDeregisteredMerchant);
    }

    public UUID createMerchant(CreateMerchantDto merchant) {
        registeredMerchant = new CompletableFuture<>();
        Event event = new Event("MerchantRegistrationRequested", new Object[] { merchant });
        queue.publish(event);
        return registeredMerchant.join();
    }

    public void handleMerchantCreated(Event e) {
        System.out.println("reachable?");
        UUID merchantId = e.getArgument(0, UUID.class);
        registeredMerchant.complete(merchantId);
    }

    public boolean deregisterMerchant(UUID merchantId) {
        deregisteredMerchant = new CompletableFuture<>();
        Event event = new Event("MerchantDeregistrationRequested", new Object[] { merchantId });
        queue.publish(event);
        boolean b=deregisteredMerchant.join();
        System.out.println(b);
        return b;
    }

    public void handleDeregisteredMerchant(Event e) {
        System.out.println("I am deleting now");
        System.out.println(e);

        Boolean isDeregistered = e.getArgument(1, Boolean.class);
        deregisteredMerchant.complete(isDeregistered);
    }
}
