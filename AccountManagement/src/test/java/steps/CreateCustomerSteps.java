package steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import models.Customer;
import models.dtos.CustomerDto;
import services.CustomerService;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertNotNull;


public class CreateCustomerSteps {
    MessageQueue queue = mock(MessageQueue.class);
    CustomerService c = new CustomerService(queue);
    CustomerDto customer;
    UUID expected;

    @When("a {string} event for a customer is received")
    public void aEventForAStudentIsReceived(String eventName) {
        customer = new CustomerDto();
        customer.setFirstName("James");
        customer.setLastName("Gerard");
        customer.setCpr("324-23-4324");
        customer.setBankAccountId("432424f33q");

        assertNull("Customer ID should initially be null", expected);
        c.handleCustomerRegistrationRequested(new Event(eventName,new Object[] {customer}));
    }

    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        // Capture the published event
        verify(queue).publish(argThat(event -> {
            // Assert the event type matches
            assertEquals(eventName, event.getType());


            UUID customerId = event.getArgument(0, UUID.class);
            assertNotNull("Customer ID should not be null", customerId);


            expected = customerId;
            // Indicate the argument matches expectations
            return true;
        }));

    }

    @Then("the customer gets a customer id")
    public void theCustomerGetsACustomerId() {
        assertNotNull("Customer ID should not be null", expected);
    }
}
