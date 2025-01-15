package services;

import messaging.Event;
import messaging.MessageQueue;
import models.Customer;
import models.dtos.CustomerDto;
import repositories.CustomerRepository;

import java.util.UUID;

public class CustomerService {
    MessageQueue queue;
    CustomerRepository customerRepository = CustomerRepository.getInstance();

    public CustomerService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("CustomerRegistrationRequested", this::handleCustomerRegistrationRequested);
        this.queue.addHandler("CustomerDeregistrationRequested", this::handleCustomerDeregistrationRequested);
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

        Event event = new Event("CustomerCreated", new Object[] { customer.getId() });
        queue.publish(event);
    }
    public void handleCustomerDeregistrationRequested(Event ev) {
        UUID customerId = ev.getArgument(0, UUID.class);
        boolean isDeleted = customerRepository.removeCustomer(customerId);
        System.out.println(isDeleted);

        Event event = new Event("CustomerDeregistered", new Object[]{customerId, isDeleted});
        queue.publish(event);
    }
}
