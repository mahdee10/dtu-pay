package services;

import messaging.Event;
import messaging.MessageQueue;
import models.Customer;
import models.dtos.CustomerDto;
import repositories.CustomerRepository;

import java.util.UUID;

public class CustomerService {
    private static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
    private static final String CUSTOMER_CREATED = "CustomerCreated";
    private static final String CUSTOMER_DEREGISTRATION_REQUESTED = "CustomerDeregistrationRequested";
    private static final String CUSTOMER_DEREGISTERED = "CustomerDeregistered";
    private static final String GET_CUSTOMER_BANK_ACCOUNT_REQUESTED = "GetCustomerBankAccountRequested";
    private static final String CUSTOMER_BANK_ACCOUNT_RESPONSE = "CustomerBankAccountResponse";
    private static final String VALIDATE_CUSTOMER_ACCOUNT_REQUESTED = "ValidateCustomerAccountRequested";
    private static final String CUSTOMER_ACCOUNT_VALIDATION_RESPONSE = "CustomerAccountValidationResponse";

    MessageQueue queue;
    CustomerRepository customerRepository = CustomerRepository.getInstance();

    public CustomerService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(CUSTOMER_REGISTRATION_REQUESTED, this::handleCustomerRegistrationRequested);
        this.queue.addHandler(CUSTOMER_DEREGISTRATION_REQUESTED, this::handleCustomerDeregistrationRequested);
        this.queue.addHandler(GET_CUSTOMER_BANK_ACCOUNT_REQUESTED, this::handleGetCustomerBankAccountRequested);
        this.queue.addHandler(VALIDATE_CUSTOMER_ACCOUNT_REQUESTED, this::handleValidateCustomerAccountRequested);
    }

    public void handleCustomerRegistrationRequested(Event ev) {
        var customerDto = ev.getArgument(0, CustomerDto.class);
        Customer customer=
                new Customer(
                        customerDto.getFirstName(),
                        customerDto.getLastName(),
                        customerDto.getCpr(),
                        customerDto.getBankAccountId());
        customerRepository.addCustomer(customer);

        System.out.println("I created "+customer.getFirstName());

        Event event = new Event(CUSTOMER_CREATED, new Object[] { customer.getId() });
        queue.publish(event);
    }

    public void handleCustomerDeregistrationRequested(Event ev) {
        UUID customerId = ev.getArgument(0, UUID.class);
        boolean isDeleted = customerRepository.removeCustomer(customerId);
        System.out.println(isDeleted);

        Event event = new Event(CUSTOMER_DEREGISTERED, new Object[]{customerId, isDeleted});
        queue.publish(event);
    }

    public void handleGetCustomerBankAccountRequested(Event ev) {
        UUID customerId = ev.getArgument(0, UUID.class);
        Customer customer = customerRepository.getCustomer(customerId);
        String bankAccountId = customer != null ? customer.getBankAccountId() : null;

        Event event = new Event(CUSTOMER_BANK_ACCOUNT_RESPONSE, new Object[]{customerId, bankAccountId});
        queue.publish(event);
    }

    public void handleValidateCustomerAccountRequested(Event ev) {
        UUID customerId = ev.getArgument(0, UUID.class);
        boolean isValid = customerRepository.getCustomer(customerId) != null;

        Event event = new Event(CUSTOMER_ACCOUNT_VALIDATION_RESPONSE, new Object[]{customerId, isValid});
        queue.publish(event);
    }
}
