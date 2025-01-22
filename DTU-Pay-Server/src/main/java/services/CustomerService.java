package services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import models.AccountEventMessage;
import models.dtos.CreateCustomerDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import models.CorrelationId;

public class CustomerService {
    private MessageQueue queue;
    private ConcurrentHashMap<CorrelationId, CompletableFuture<AccountEventMessage>> correlations = new ConcurrentHashMap<>();

    static CustomerService service = null;

    public static synchronized CustomerService getService() {
        if (service != null) {
            return service;
        }

        String hostname = System.getenv("Environment").equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        service = new CustomerService(mq);
        return service;
    }

    public CustomerService(MessageQueue q) {
        queue = q;
        queue.addHandler("CustomerCreated", this::handleCustomerCreated);
        queue.addHandler("CustomerDeregistered", this::handleDeregisteredCustomer);
    }

    public AccountEventMessage createCustomer(CreateCustomerDto customer) {
        CorrelationId correlationId = CorrelationId.randomId();
        correlations.put(correlationId, new CompletableFuture<>());

        AccountEventMessage eventMessage = new AccountEventMessage();
        eventMessage.setFirstName(customer.getFirstName());
        eventMessage.setLastName(customer.getLastName());
        eventMessage.setCpr(customer.getCpr());
        eventMessage.setBankAccount(customer.getBankAccountId());

        Event event = new Event("CustomerRegistrationRequested", new Object[] { correlationId, eventMessage });
        queue.publish(event);

        return correlations.get(correlationId).join();
    }

    public void handleCustomerCreated(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = e.getArgument(1, AccountEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public AccountEventMessage deregisterCustomer(UUID customerId) {
        CorrelationId correlationId = CorrelationId.randomId();
        correlations.put(correlationId, new CompletableFuture<>());

        AccountEventMessage eventMessage = new AccountEventMessage();
        eventMessage.setCustomerId(customerId);

        Event event = new Event("CustomerDeregistrationRequested", new Object[] { correlationId, eventMessage });
        queue.publish(event);

        return correlations.get(correlationId).join();
    }

    public void handleDeregisteredCustomer(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = e.getArgument(1, AccountEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }
}
