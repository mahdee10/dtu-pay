package services;

import messaging.Event;
import messaging.MessageQueue;
import models.Customer;
import models.dtos.CustomerDto;
import repositories.CustomerRepository;

public class CustomerService {
    MessageQueue queue;
    CustomerRepository customerRepository = CustomerRepository.getInstance();

    public CustomerService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("CustomerRegistrationRequested", this::handleCustomerRegistrationRequested);
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

        Event event = new Event("CustomerCreated", new Object[] { customer.getId() });
        queue.publish(event);
    }
}
