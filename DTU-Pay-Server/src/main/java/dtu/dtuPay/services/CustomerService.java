package dtu.dtuPay.services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.dtos.CreateCustomerDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import dtu.dtuPay.models.CorrelationId;

public class CustomerService {
    private static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
    private static final String CUSTOMER_CREATED = "CustomerCreated";
    private static final String CUSTOMER_DEREGISTRATION_REQUESTED = "CustomerDeregistrationRequested";
    private static final String CUSTOMER_DEREGISTERED = "CustomerDeregistered";
    private static final String VALIDATE_CUSTOMER_ACCOUNT_REQUESTED = "ValidateCustomerAccountRequested";
    private static final String CUSTOMER_ACCOUNT_VALIDATED = "CustomerAccountValidated";

    private MessageQueue queue;
    private ConcurrentHashMap<CorrelationId, CompletableFuture<AccountEventMessage>> correlations = new ConcurrentHashMap<>();

    static CustomerService service = null;

    public static synchronized CustomerService getService() {
        if (service != null) {
            return service;
        }

        String environment = System.getenv("Environment");
        String hostname = environment != null && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        service = new CustomerService(mq);
        return service;
    }

    public CustomerService(MessageQueue q) {
        queue = q;
        queue.addHandler(CUSTOMER_CREATED, this::handleCustomerCreated);
        queue.addHandler(CUSTOMER_DEREGISTERED, this::handleDeregisteredCustomer);
        queue.addHandler(CUSTOMER_ACCOUNT_VALIDATED, this::handleValidateCustomerAccountResponse);
    }

    public AccountEventMessage createCustomer(CreateCustomerDto customer) {
        CorrelationId correlationId = CorrelationId.randomId();
        correlations.put(correlationId, new CompletableFuture<>());

        AccountEventMessage eventMessage = new AccountEventMessage();
        eventMessage.setFirstName(customer.getFirstName());
        eventMessage.setLastName(customer.getLastName());
        eventMessage.setCpr(customer.getCpr());
        eventMessage.setBankAccount(customer.getBankAccountId());

        Event event = new Event(CUSTOMER_REGISTRATION_REQUESTED, new Object[] { correlationId, eventMessage });
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

        Event event = new Event(CUSTOMER_DEREGISTRATION_REQUESTED, new Object[] { correlationId, eventMessage });
        queue.publish(event);

        return correlations.get(correlationId).join();
    }

    public void handleDeregisteredCustomer(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = e.getArgument(1, AccountEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    private void handleValidateCustomerAccountResponse(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = e.getArgument(1, AccountEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public AccountEventMessage validateCustomerAccount(UUID customerId) {
        CorrelationId customerGetBankAccountCorrelationId = CorrelationId.randomId();
        CompletableFuture<AccountEventMessage> futureGetCustomerBankAccount = new CompletableFuture<>();
        correlations.put(customerGetBankAccountCorrelationId, futureGetCustomerBankAccount);

        AccountEventMessage accountEventMessage = new AccountEventMessage();
        accountEventMessage.setCustomerId(customerId);

        Event customerTokenValidationEvent = new Event(VALIDATE_CUSTOMER_ACCOUNT_REQUESTED,
                new Object[] { customerGetBankAccountCorrelationId, accountEventMessage });
        queue.publish(customerTokenValidationEvent);

        return futureGetCustomerBankAccount.join();
    }
}
