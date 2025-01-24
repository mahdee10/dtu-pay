package behaviourtests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.UUID;

import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.Token;
import dtu.dtuPay.models.TokenEventMessage;
import dtu.dtuPay.repositories.TokenRepository;
import dtu.dtuPay.services.TokenService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

public class UseTokenSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TokenService tokenService = new TokenService(queue);
    TokenRepository tokenRepository = TokenRepository.getInstance();

    TokenEventMessage tokenEventMessage;
    UUID nonExistentTokenUUID;
    UUID associatedCustomerId;
    UUID receivedTokenUUID;
    CorrelationId correlationId;
    Event event;
    Token token;
    ArrayList<Token> tokenList = new ArrayList<>();

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    public UseTokenSteps() {
    }

    @Given("a payment service supplies {int} valid token with UUID {string} to pay")
    public void aPaymentServiceSuppliesValidTokenWithUUIDToPay(int nTokens, String tokenUUID) {
        receivedTokenUUID = UUID.fromString(tokenUUID);

        token = new Token(receivedTokenUUID, true);
        tokenList.add(token);
        assertEquals(nTokens, tokenList.size());
    }

    @Given("a payment service supplies {int} used invalid token with UUID {string} to pay")
    public void aPaymentServiceSuppliesUsedInvalidTokenWithUUIDToPay(int nTokens, String tokenUUID) {
        receivedTokenUUID = UUID.fromString(tokenUUID);

        token = new Token(receivedTokenUUID, false);
        tokenList.add(token);
        assertEquals(nTokens, tokenList.size());
    }

    @Given("a registered DTU pay customer with UUID {string} is associated with the token")
    public void aRegisteredDTUPayCustomerWithUUIDIsAssociatedWithTheToken(String customerUUID) {
        associatedCustomerId = UUID.fromString(customerUUID);
        tokenRepository.addTokens(associatedCustomerId, tokenList);
    }

    @When("the event {string} is received")
    public void theEventIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        tokenEventMessage = new TokenEventMessage();
        tokenEventMessage.setTokenUUID(receivedTokenUUID);

        tokenService.handleUseTokenRequest(new Event(eventName, new Object[]{ correlationId, tokenEventMessage}));
    }

    @Then("a response {string} is sent and contains the customer UUID {string}")
    public void aResponseIsSentAndContainsTheCustomerUUID(String response, String customerUUID) {

        tokenEventMessage.setRequestResponseCode(OK);
        tokenEventMessage.setCustomerId(associatedCustomerId);
        tokenEventMessage.setIsValid(true);

        event = new Event(response, new Object[]{ correlationId, tokenEventMessage});
        verify(queue).publish(event);
    }

    @Then("a response contains the token UUID {string}")
    public void aResponseContainsTheTokenUUID(String tokenUUID) {
        assertEquals(tokenEventMessage.getTokenUUID(),UUID.fromString(tokenUUID)) ;
    }

    @Then("a response event {string} is sent and contains an exception {string}")
    public void aResponseEventIsSentAndContainsAnException(String response, String  exceptionMessage) {
        tokenEventMessage.setRequestResponseCode(BAD_REQUEST);
        tokenEventMessage.setCustomerId(associatedCustomerId);
        tokenEventMessage.setExceptionMessage(exceptionMessage);
        tokenEventMessage.setIsValid(false);
        event = new Event(response, new Object[]{ correlationId, tokenEventMessage});
        verify(queue).publish(event);
    }

    @Given("a payment service supplies an token with UUID {string} to pay")
    public void aPaymentServiceSuppliesAnTokenWithUUIDToPay(String tokenUUID) {
        nonExistentTokenUUID = UUID.fromString(tokenUUID);
    }

    @Then("the customer has {int} tokens")
    public void theCustomerHasTokens(int nTokens) {
        int nTokenActive = tokenRepository.getTokens(associatedCustomerId).size();
        assertEquals(nTokens, nTokenActive);
    }
}
