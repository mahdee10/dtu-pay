package services.customer;

import messaging.Event;
import messaging.MessageQueue;
import models.dtos.CreateCustomerDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CustomerService {
    private MessageQueue queue;
    private CompletableFuture<UUID> registeredCustomer;
    private CompletableFuture<Boolean> deregisteredCustomer;

    public CustomerService(MessageQueue q) {
        queue = q;
        queue.addHandler("CustomerCreated", this::handleCustomerCreated);
        queue.addHandler("DeregisteredCustomer", this::handleDeregisteredCustomer);
    }

    public UUID createCustomer(CreateCustomerDto customer) {
        registeredCustomer = new CompletableFuture<>();
        Event event = new Event("CustomerRegistrationRequested", new Object[] { customer });
        queue.publish(event);
        return registeredCustomer.join();
    }

    public void handleCustomerCreated(Event e) {
        UUID customerId = e.getArgument(0, UUID.class);
        registeredCustomer.complete(customerId);
    }

    public boolean deregisterCustomer(UUID customerId) {
        deregisteredCustomer = new CompletableFuture<>();
        Event event = new Event("DeregisterCustomerRequested", new Object[] { customerId });
        queue.publish(event);
        return deregisteredCustomer.join();
    }

    public void handleDeregisteredCustomer(Event e) {
        Boolean isDeregistered = e.getArgument(0, Boolean.class);
        deregisteredCustomer.complete(isDeregistered);
    }
}
