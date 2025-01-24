/**
 * @author Mahdi El Dirani s233031
 */
package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.Customer;
import dtu.dtuPay.repositories.CustomerRepository;
import dtu.dtuPay.services.CustomerService;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class CreateCustomerSteps {
    MessageQueue queue = mock(MessageQueue.class);
    CustomerService service = new CustomerService(queue);
    private CustomerRepository customerRepository = CustomerRepository.getInstance();
    UUID expected;
    AccountEventMessage eventMessage;
    private CorrelationId correlationId;
    private Customer userCustomer;

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    @When("a {string} event for registering a customer is received")
    public void aEventForRegisteringACustomerIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();

        eventMessage = new AccountEventMessage();
        eventMessage.setFirstName("James");
        eventMessage.setLastName("Gerard");
        eventMessage.setCpr("324-23-4324");
        eventMessage.setBankAccount("432424f33q");

        service.handleCustomerRegistrationRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the {string} customer event is sent")
    public void theCustomerEventIsSent(String eventName) {
        if (eventName.equals("CustomerCreated")) {
            expected = customerRepository.getCustomerByCPR(eventMessage.getCpr());
            eventMessage.setCustomerId(expected);
            eventMessage.setRequestResponseCode(OK);
        } else if (eventName.equals("CustomerDeregistered")) {
            eventMessage.setIsAccountDeleted(true);
            eventMessage.setRequestResponseCode(OK);
        } else if (eventName.equals("CustomerAccountValidated")) {
            eventMessage.setBankAccount(userCustomer.getBankAccountId());
            eventMessage.setRequestResponseCode(OK);
            eventMessage.setIsValidAccount(true);
        }

        // Capture the published event
        verify(queue).publish(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the customer gets a customer id")
    public void theCustomerGetsACustomerId() {
        assertNotNull("Customer ID should not be null", expected);
    }

    @Given("a customer with name {string}, last name {string}, CPR {string} and bank account {string} is registered with DTU Pay")
    public void aCustomerWithNameLastNameCPRAndBankAccountIsRegisteredWithDTUPay(
            String firstName, String lastName, String cpr, String bankAccountId) {
        userCustomer = new Customer(firstName, lastName, cpr, bankAccountId);
        customerRepository.addCustomer(userCustomer);
    }

    @When("a {string} event for deregistering a customer is received")
    public void aEventForDeregisteringACustomerIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        eventMessage = new AccountEventMessage();
        eventMessage.setCustomerId(userCustomer.getId());

        service.handleCustomerDeregistrationRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the customer is not registered anymore")
    public void theCustomerIsNotRegisteredAnymore() {
        assertNull(customerRepository.getCustomer(userCustomer.getId()));
    }

    @When("a {string} event for validating a customer is received")
    public void aEventForValidatingACustomerIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        eventMessage = new AccountEventMessage();
        eventMessage.setCustomerId(userCustomer.getId());

        service.handleValidateCustomerAccountRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the customer account is valid")
    public void theCustomerAccountIsValid() {
        assertTrue(eventMessage.getIsValidAccount());
    }
}
