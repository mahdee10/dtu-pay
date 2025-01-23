package services;

import messaging.Event;
import messaging.MessageQueue;
import models.AccountEventMessage;
import models.CorrelationId;
import models.Customer;
import repositories.CustomerRepository;

import java.util.UUID;

public class CustomerService {
    private static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
    private static final String CUSTOMER_CREATED = "CustomerCreated";
    private static final String CUSTOMER_DEREGISTRATION_REQUESTED = "CustomerDeregistrationRequested";
    private static final String CUSTOMER_DEREGISTERED = "CustomerDeregistered";
    private static final String VALIDATE_CUSTOMER_ACCOUNT_REQUESTED = "ValidateCustomerAccountRequested";
    private static final String CUSTOMER_ACCOUNT_VALIDATED = "CustomerAccountValidated";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    MessageQueue queue;
    CustomerRepository customerRepository = CustomerRepository.getInstance();


    public CustomerService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(CUSTOMER_REGISTRATION_REQUESTED, this::handleCustomerRegistrationRequested);
        this.queue.addHandler(CUSTOMER_DEREGISTRATION_REQUESTED, this::handleCustomerDeregistrationRequested);
        this.queue.addHandler(VALIDATE_CUSTOMER_ACCOUNT_REQUESTED, this::handleValidateCustomerAccountRequested);
    }

    public void handleCustomerRegistrationRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        Customer customer =
                new Customer(
                        eventMessage.getFirstName(),
                        eventMessage.getLastName(),
                        eventMessage.getCpr(),
                        eventMessage.getBankAccount());
        customerRepository.addCustomer(customer);

        eventMessage.setCustomerId(customer.getId());
        eventMessage.setRequestResponseCode(OK);

        System.out.println("I created " + customer.getFirstName());

        Event event = new Event(CUSTOMER_CREATED, new Object[]{ correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleCustomerDeregistrationRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);
        UUID customerId = eventMessage.getCustomerId();

        boolean isDeleted = customerRepository.removeCustomer(customerId);
        System.out.println(isDeleted);

        eventMessage.setIsAccountDeleted(isDeleted);
        eventMessage.setRequestResponseCode(OK);

        Event event = new Event(CUSTOMER_DEREGISTERED, new Object[]{ correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleValidateCustomerAccountRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);
        UUID customerId = eventMessage.getCustomerId();
        Customer customer = customerRepository.getCustomer(customerId);
        boolean isValid = customer != null;

        eventMessage.setBankAccount(isValid ? customer.getBankAccountId() : null);
        eventMessage.setIsValidAccount(isValid);
        eventMessage.setRequestResponseCode(isValid ? OK : BAD_REQUEST);
        eventMessage.setExceptionMessage(isValid ? null : "Customer account does not exist.");

        Event event = new Event(CUSTOMER_ACCOUNT_VALIDATED, new Object[]{ correlationId, eventMessage });
        queue.publish(event);
    }
}
