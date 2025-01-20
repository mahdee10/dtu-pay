package services.customer;

import messaging.Event;
import messaging.MessageQueue;
import models.dtos.CreateCustomerDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import services.CorrelationId;
public class CustomerService {
    private MessageQueue queue;
    private ConcurrentHashMap<CorrelationId, CompletableFuture<UUID>> registeredCustomerCorrelations = new ConcurrentHashMap<>();
    private ConcurrentHashMap<CorrelationId, CompletableFuture<Boolean>> deregisteredCustomerCorrelations = new ConcurrentHashMap<>();

    public CustomerService(MessageQueue q) {
        queue = q;
        queue.addHandler("CustomerCreated", this::handleCustomerCreated);
        queue.addHandler("CustomerDeregistered", this::handleDeregisteredCustomer);
    }

    public UUID createCustomer(CreateCustomerDto customer) {
        CorrelationId correlationId = CorrelationId.randomId();
        registeredCustomerCorrelations.put(correlationId, new CompletableFuture<UUID>());

        Event event = new Event("CustomerRegistrationRequested", new Object[]{correlationId, customer});
        queue.publish(event);

        return registeredCustomerCorrelations.get(correlationId).join();
    }

    public void handleCustomerCreated(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        UUID customerId = e.getArgument(1, UUID.class);

        registeredCustomerCorrelations.get(correlationId).complete(customerId);
    }

    public boolean deregisterCustomer(UUID customerId) {
        CorrelationId correlationId = CorrelationId.randomId();
        deregisteredCustomerCorrelations.put(correlationId, new CompletableFuture<Boolean>());

        Event event = new Event("CustomerDeregistrationRequested", new Object[]{correlationId, customerId});
        queue.publish(event);

        return deregisteredCustomerCorrelations.get(correlationId).join();
    }

    public void handleDeregisteredCustomer(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        Boolean isDeregistered = e.getArgument(1, Boolean.class);

        deregisteredCustomerCorrelations.get(correlationId).complete(isDeregistered);


    }
}
