package behaviourtests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.UUID;
import org.example.models.Token;
import org.example.repositories.TokenRepository;
import org.example.services.TokenService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GetTokenSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TokenService tokenService = new TokenService(queue);
    TokenRepository tokenRepository = TokenRepository.getInstance();

    UUID userUUID;
    UUID tokenUUID = UUID.randomUUID();
    UUID expectedUUID;

	public GetTokenSteps() {
	}
	
	@Given("a registered customer with id {string} with at least {int} token")
	public void a_registered_customer_with_id_with_at_least_token(String uuid, Integer int1) {
		Token token = new Token(tokenUUID, true);
		ArrayList<Token> tokenList = new ArrayList<Token>();
		userUUID = UUID.fromString(uuid);
		tokenList.add(token);
		tokenRepository.addTokens(userUUID, tokenList);
        assertTrue(tokenRepository.getTokens(userUUID).size() == int1);
	}

	@When("the event CustomerTokensRequest {string} is sent")
	public void the_event_customer_tokens_request_is_sent(String CustomerTokensRequest) {
		 tokenService.handleCustomerTokenRequest(new Event(CustomerTokensRequest, new Object[] {userUUID}));
	}

	@Then("a response CustomerTokensReturned {string} is sent and a customer receives a token")
	public void a_response_customer_tokens_returned_is_sent_and_a_customer_receives_a_token(String CustomerTokensReturned) {
		
		 verify(queue).publish(new Event(CustomerTokensReturned, new Object[]{expectedUUID}));
		 assertNotNull(expectedUUID);
	}

	@Given("a registered customer with id {string} with {int} tokens")
	public void a_registered_customer_with_id_with_tokens(String uuid, Integer int1) {
		ArrayList<Token> tokenList = new ArrayList<Token>();
		userUUID = UUID.fromString(uuid);
		tokenRepository.addTokens(userUUID, tokenList);
        assertTrue(tokenRepository.getTokens(userUUID).size() < 1);
	}

	@Then("a response CustomerTokensReturned {string} is sent and the system throws an exception with message {string}")
	public void a_response_customer_tokens_returned_is_sent_and_the_system_throws_an_exception_with_message(String CustomerTokensReturned, String string2) {

		  verify(queue).publish(new Event(CustomerTokensReturned, new Object[]{new Exception(string2)}));
	}

}
