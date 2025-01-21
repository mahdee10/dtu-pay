package steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import models.AccountEventMessage;
import models.CorrelationId;
import repositories.CustomerRepository;
import services.CustomerService;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertNotNull;


public class CreateCustomerSteps {
    MessageQueue queue = mock(MessageQueue.class);
    CustomerService service = new CustomerService(queue);
    private CustomerRepository customerRepository = CustomerRepository.getInstance();
    UUID expected;
    AccountEventMessage eventMessage;
    private CorrelationId correlationId;

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    @When("a {string} event for a customer is received")
    public void aEventForAStudentIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();

        eventMessage = new AccountEventMessage();
        eventMessage.setFirstName("James");
        eventMessage.setLastName("Gerard");
        eventMessage.setCpr("324-23-4324");
        eventMessage.setBankAccount("432424f33q");

        service.handleCustomerRegistrationRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        expected = customerRepository.getCustomerByCPR(eventMessage.getCpr());
        eventMessage.setCustomerId(expected);
        eventMessage.setRequestResponseCode(OK);

        // Capture the published event
        verify(queue).publish(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the customer gets a customer id")
    public void theCustomerGetsACustomerId() {
        assertNotNull("Customer ID should not be null", expected);
    }
}
