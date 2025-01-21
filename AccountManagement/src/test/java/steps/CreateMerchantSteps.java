package steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import models.AccountEventMessage;
import models.CorrelationId;
import repositories.MerchantRepository;
import services.MerchantService;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertNotNull;


public class CreateMerchantSteps {
    MessageQueue queue = mock(MessageQueue.class);
    MerchantService service = new MerchantService(queue);
    private MerchantRepository merchantRepository = MerchantRepository.getInstance();
    UUID expected;
    AccountEventMessage eventMessage;
    private CorrelationId correlationId;

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    @When("a {string} event for a merchant is received")
    public void aEventForAStudentIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        eventMessage = new AccountEventMessage();
        eventMessage.setFirstName("James");
        eventMessage.setLastName("Gerard");
        eventMessage.setCpr("324-23-4324");
        eventMessage.setBankAccount("432424f33q");

        service.handleMerchantRegistrationRequested(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("The {string} event is sent")
    public void theEventIsSent(String eventName) {
        expected = merchantRepository.getMerchantByCPR(eventMessage.getCpr());
        eventMessage.setMerchantId(expected);
        eventMessage.setRequestResponseCode(OK);

        // Capture the published event
        verify(queue).publish(new Event(eventName, new Object[] { correlationId, eventMessage }));
    }

    @Then("the merchant gets a merchant id")
    public void theMerchantGetsAMerchantId() {
        assertNotNull("Merchant ID should not be null", expected);
    }
}
