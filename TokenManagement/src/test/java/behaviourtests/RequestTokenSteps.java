package behaviourtests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.models.CorrelationId;
import org.example.models.Token;
import org.example.models.TokenEventMessage;
import org.example.repositories.TokenRepository;
import org.example.services.TokenService;

public class RequestTokenSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TokenService tokenService = new TokenService(queue);
    TokenRepository tokenRepository = TokenRepository.getInstance();

    UUID userUUID;
	List<Token> receivedTokensList = new ArrayList<>();
	CorrelationId correlationId;
	TokenEventMessage tokenEventMessage;

	public static final int BAD_REQUEST = 400;
	public static final int OK = 200;

	public RequestTokenSteps() {
	}
	
	@Given("an existing registered customer with id {string} with more than {int} active token")
	public void an_existing_registered_customer_with_id_with_more_than_active_token(String uuid, Integer int1) {
		Token token1 = new Token(UUID.randomUUID(), true);
		Token token2 = new Token(UUID.randomUUID(), true);
		
		ArrayList<Token> tokenList = new ArrayList<Token>();
		userUUID = UUID.fromString(uuid);
		tokenList.add(token1);
		tokenList.add((token2));
		tokenRepository.addTokens(userUUID, tokenList);
        assertTrue(tokenRepository.getTokens(userUUID).size() > int1);
	}	
	
	@Given("a registered customer with id {string} with more than {int} active token")

	@When("a registered customer with id {string} requests {int} tokens and an event RequestTokensEvent {string} is sent")
	public void a_registered_customer_with_id_requests_tokens_and_an_event_request_tokens_event_is_sent(String uuid, Integer int1, String RequestTokensEvent) {
		correlationId = CorrelationId.randomId();
		tokenEventMessage = new TokenEventMessage();
		tokenEventMessage.setCustomerId(UUID.fromString(uuid));
		tokenEventMessage.setRequestedTokens(int1);
		tokenService.handleRequestTokensEvent(new Event(RequestTokensEvent, new Object[] { correlationId, tokenEventMessage}));
	}

	@Then("a response RequestTokensResponse {string} is sent and throws and exception {string}")
	public void a_response_request_tokens_response_is_sent_and_throws_and_exception(String RequestTokensResponse, String exceptionMessage) {
		tokenEventMessage.setRequestResponseCode(BAD_REQUEST);
		tokenEventMessage.setExceptionMessage(exceptionMessage);
		verify(queue).publish(new Event(RequestTokensResponse, new Object[]{ correlationId, tokenEventMessage}));
	}

	@Given("an existing registered customer with id {string} with {int} or less active tokens")
	public void an_existing_registered_customer_with_id_with_or_less_active_tokens(String uuid, Integer int1) {
		Token token1 = new Token(UUID.randomUUID(), true);
		
		ArrayList<Token> tokenList = new ArrayList<Token>();
		userUUID = UUID.fromString(uuid);
		tokenList.add(token1);
		tokenRepository.addTokens(userUUID, tokenList);
        assertTrue(tokenRepository.getTokens(userUUID).size() <= int1);
	}

	@When("a registered customer with id {string} requests {int} tokens an event RequestTokensEvent {string} is sent")
	public void a_registered_customer_with_id_requests_tokens_an_event_request_tokens_event_is_sent(String uuid, Integer int1, String RequestTokensEvent) {
		correlationId = CorrelationId.randomId();
		tokenEventMessage = new TokenEventMessage();
		tokenEventMessage.setCustomerId(UUID.fromString(uuid));
		tokenEventMessage.setRequestedTokens(int1);
		tokenService.handleRequestTokensEvent(new Event(RequestTokensEvent, new Object[] { correlationId, tokenEventMessage}));
	}

	@Then("a response RequestTokensResponse {string} is sent containing a list with {int} new tokens")
	public void a_response_request_tokens_response_is_sent_containing_a_list_with_new_tokens(String RequestTokensResponse, Integer int1) {
		
		for (int i = 0; i < int1; i++) {
			Token newToken = new Token(UUID.randomUUID(), true);
			receivedTokensList.add(newToken);	  
		}

		tokenEventMessage.setCreatedTokens(receivedTokensList.size());
		tokenEventMessage.setRequestResponseCode(OK);
		verify(queue).publish(new Event(RequestTokensResponse, new Object[]{ correlationId, tokenEventMessage}));
	}

	@Then("a customer with id {string} has {int} active tokens")
	public void a_customer_with_id_has_active_tokens(String uuid, Integer int1) {
	    assertTrue(tokenRepository.getTokens(UUID.fromString(uuid)).size() == int1);
	}

	@Then("a response RequestTokensResponse {string} is sent and throws an exception {string}")
	public void a_response_request_tokens_response_is_sent_and_throws_an_exception(String RequestTokensResponse, String exceptionMessage) {
		tokenEventMessage.setRequestResponseCode(BAD_REQUEST);
		tokenEventMessage.setExceptionMessage(exceptionMessage);
		verify(queue).publish(new Event(RequestTokensResponse, new Object[]{ correlationId, tokenEventMessage}));
	}


}