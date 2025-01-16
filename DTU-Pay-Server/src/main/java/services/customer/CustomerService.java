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
        queue.addHandler("CustomerDeregistered", this::handleDeregisteredCustomer);
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
        Event event = new Event("CustomerDeregistrationRequested", new Object[] { customerId });
        queue.publish(event);
        boolean b=deregisteredCustomer.join();
        System.out.println(b);
        return b;
    }

    public void handleDeregisteredCustomer(Event e) {
        System.out.println("I am deleting now");
        System.out.println(e);

        Boolean isDeregistered = e.getArgument(1, Boolean.class);
        deregisteredCustomer.complete(isDeregistered);
    }
}
