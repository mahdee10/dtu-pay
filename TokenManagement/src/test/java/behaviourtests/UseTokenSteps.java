package behaviourtests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.models.CorrelationId;
import org.example.models.Token;
import org.example.models.TokenEventMessage;
import org.example.repositories.TokenRepository;
import org.example.services.TokenService;

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

    @Given("a payment service supplies a token with UUID {string} to pay")
    public void aPaymentServiceSuppliesATokenWithUUIDToPay(String tokenUUID) {
        receivedTokenUUID = UUID.fromString(tokenUUID);
    }

    @Given("the token is valid")
    public void theTokenIsValid() {

        token = new Token(receivedTokenUUID, true);
        tokenList.add(token);
    }

    @Given("the token has been used and is invalid")
    public void theTokenHasBeenUsedAndIsInvalid() {
        token = new Token(receivedTokenUUID, false);
        tokenList.add(token);
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
        tokenEventMessage.setExceptionMessage(exceptionMessage);
        tokenEventMessage.setIsValid(false);
        event = new Event(response, new Object[]{ correlationId, tokenEventMessage});
        verify(queue).publish(event);
    }

    @Given("a payment service supplies an token with UUID {string} to pay")
    public void aPaymentServiceSuppliesAnTokenWithUUIDToPay(String tokenUUID) {
        nonExistentTokenUUID = UUID.fromString(tokenUUID);
    }


}
