/**
 * @author Hussein Dirani s223518
 */
package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.Merchant;
import dtu.dtuPay.repositories.MerchantRepository;
import dtu.dtuPay.services.MerchantService;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class CreateMerchantSteps {
    MessageQueue queue = mock(MessageQueue.class);
    MerchantService service = new MerchantService(queue);
    private MerchantRepository merchantRepository = MerchantRepository.getInstance();
    UUID expected;
    Merchant userMerchant;
    AccountEventMessage eventMessage;
    private CorrelationId correlationId;

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    @When("a {string} event for registering a merchant is received")
    public void a_event_for_registering_a_merchant_is_received(String eventName) {
        correlationId = CorrelationId.randomId();
        eventMessage = new AccountEventMessage();
        eventMessage.setFirstName("James");
        eventMessage.setLastName("Gerard");
        eventMessage.setCpr("324-23-4324");
        eventMessage.setBankAccount("432424f33q");

        service.handleMerchantRegistrationRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the {string} merchant event is sent")
    public void the_merchant_event_is_sent(String eventName) {
        if (eventName.equals("MerchantCreated")) {
            expected = merchantRepository.getMerchantByCPR(eventMessage.getCpr());
            eventMessage.setMerchantId(expected);
            eventMessage.setRequestResponseCode(OK);
        } else if (eventName.equals("MerchantDeregistered")) {
            eventMessage.setIsAccountDeleted(true);
            eventMessage.setRequestResponseCode(OK);
        } else if (eventName.equals("MerchantAccountValidated")) {
            eventMessage.setBankAccount(userMerchant.getBankAccountId());
            eventMessage.setRequestResponseCode(OK);
            eventMessage.setIsValidAccount(true);
        }

        // Capture the published event
        verify(queue).publish(new Event(eventName, new Object[] { correlationId, eventMessage }));

    }

    @Then("the merchant gets a merchant id")
    public void the_merchant_gets_a_merchant_id() {
        assertNotNull("Merchant ID should not be null", expected);
    }

    @Given("a merchant with name {string}, last name {string}, CPR {string} and bank account {string} is registered with DTU Pay")
    public void a_merchant_with_name_last_name_cpr_and_bank_account_is_registered_with_dtu_pay(
            String firstName, String lastName, String cpr, String bankAccountId) {
        userMerchant = new Merchant(firstName, lastName, cpr, bankAccountId);
        merchantRepository.addMerchant(userMerchant);
    }

    @When("a {string} event for deregistering a merchant is received")
    public void a_event_for_deregistering_a_merchant_is_received(String eventName) {
        correlationId = CorrelationId.randomId();
        eventMessage = new AccountEventMessage();
        eventMessage.setMerchantId(userMerchant.getId());

        service.handleMerchantDeregistrationRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the merchant is not registered anymore")
    public void the_merchant_is_not_registered_anymore() {
        assertNull(merchantRepository.getMerchant(userMerchant.getId()));
    }

    @When("a {string} event for validating a merchant is received")
    public void aEventForValidatingAMerchantIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        eventMessage = new AccountEventMessage();
        eventMessage.setMerchantId(userMerchant.getId());

        service.handleValidateMerchantAccountRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the merchant account is valid")
    public void theMerchantAccountIsValid() {
        assertTrue(eventMessage.getIsValidAccount());
    }
}
