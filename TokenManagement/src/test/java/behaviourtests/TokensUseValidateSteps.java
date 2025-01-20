package behaviourtests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.example.models.Token;
import org.example.repositories.TokenRepository;
import org.example.services.TokenService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TokensUseValidateSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TokenService s = new TokenService(queue);
    TokenRepository tokenRepository = TokenRepository.getInstance();
    UUID receivedTokenUUID;
    Token receivedToken;

    @Given("a payment service supplies a {string} token with id {string} to validate")
    public void a_payment_service_supplies_a_token_with_id_to_validate(String string, String tokenId) {
        boolean isMissing = string.equalsIgnoreCase("missing");
        if (isMissing) return;

        boolean isValid = string.equalsIgnoreCase("valid");
        receivedTokenUUID = UUID.fromString(tokenId);
        receivedToken = new Token(receivedTokenUUID, isValid);
        List<Token> tokens = new ArrayList<>();
        tokens.add(receivedToken);

        //assume data is already in the "database"
        tokenRepository.addTokens(UUID.randomUUID(), tokens);
    }
    @When("the event {string} is received")
    public void the_event_is_received(String eventName) {
        if (eventName.equals("TokenValidationRequest")) s.handleTokenValidationRequest(new Event(eventName, new Object[] {receivedTokenUUID}));
        if (eventName.equals("UseTokenRequest")) s.handleUseTokenRequest(new Event(eventName, new Object[] {receivedTokenUUID}));
    }

    @Then("a response event {string} is sent and contains the value {string}")
    public void the_response_event_is_sent_and_contains_the_value(String eventName, String expected) {
        verify(queue).publish(new Event(eventName, new Object[]{receivedToken.isValid()}));
        assertEquals(Boolean.parseBoolean(expected),receivedToken.isValid());
    }

    @Then("a response event {string} is sent and throws an exception {string}")
    public void a_response_event_is_sent_and_throws_an_exception(String string, String string2) {
        verify(queue,atLeastOnce()).publish(new Event(string, new Object[]{string2}));
    }

    public TokensUseValidateSteps() {}

    @Given("a payment service supplies a valid token with UUID {string} to pay")
    public void aPaymentServiceSuppliesAValidTokenWithUUIDToPay(String arg0) {
        a_payment_service_supplies_a_token_with_id_to_validate("valid", arg0);
    }
}
