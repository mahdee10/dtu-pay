package steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import models.dtos.MerchantDto;
import models.CorrelationId;
import services.MerchantService;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertNotNull;


public class CreateMerchantSteps {
    MessageQueue queue = mock(MessageQueue.class);
    MerchantService c = new MerchantService(queue);
    MerchantDto merchant;
    UUID expected;
    private CorrelationId correlationId;

    @When("a {string} event for a merchant is received")
    public void aEventForAStudentIsReceived(String eventName) {
        merchant = new MerchantDto();
        merchant.setFirstName("James");
        merchant.setLastName("Gerard");
        merchant.setCpr("324-23-4324");
        merchant.setBankAccountId("432424f33q");

        correlationId = CorrelationId.randomId();


        assertNull("Merchant ID should initially be null", expected);
        c.handleMerchantRegistrationRequested(new Event(eventName,new Object[] {correlationId,merchant}));
    }

    @Then("The {string} event is sent")
    public void theEventIsSent(String eventName) {
        // Capture the published event
        verify(queue).publish(argThat(event -> {
            // Assert the event type matches
            assertEquals(eventName, event.getType());


            UUID merchantId = event.getArgument(1, UUID.class);
            assertNotNull("Merchant ID should not be null", merchantId);


            expected = merchantId;
            // Indicate the argument matches expectations
            return true;
        }));

    }

    @Then("the merchant gets a merchant id")
    public void theMerchantGetsAMerchantId() {
        assertNotNull("Merchant ID should not be null", expected);
    }
}
