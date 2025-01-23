package behaviourtests;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GetTokenSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TokenService tokenService = new TokenService(queue);
    TokenRepository tokenRepository = TokenRepository.getInstance();

    UUID userUUID;
    UUID tokenUUID = UUID.randomUUID();
	CorrelationId correlationId;
	TokenEventMessage tokenEventMessage;

	public static final int BAD_REQUEST = 400;
	public static final int OK = 200;

	public GetTokenSteps() {
	}
	
	@Given("a registered customer with id {string} with at least {int} tokens")
	public void a_registered_customer_with_id_with_at_least_token(String uuid, Integer nTokens) {
		Token token = new Token(tokenUUID, true);
		ArrayList<Token> tokenList = new ArrayList<>();

		for (int i = 0; i < nTokens; i++) {
			userUUID = UUID.fromString(uuid);
			tokenList.add(token);
		}

		tokenRepository.addTokens(userUUID, tokenList);
	}

	@When("the event CustomerTokensRequest {string} is sent")
	public void the_event_customer_tokens_request_is_sent(String CustomerTokensRequest) {
		correlationId = CorrelationId.randomId();
		tokenEventMessage = new TokenEventMessage();
		tokenEventMessage.setCustomerId(userUUID);
		tokenService.handleGetCustomerTokensRequest(new Event(CustomerTokensRequest, new Object[] { correlationId, tokenEventMessage}));
	}

	@Then("a response CustomerTokensReturned {string} is sent and a customer receives a list of tokens")
	public void a_response_customer_tokens_returned_is_sent_and_a_customer_receives_a_token(String CustomerTokensReturned) {
		tokenEventMessage.setTokenList(tokenRepository.getTokens(userUUID).stream().map((Token::getUuid)).toList());
		tokenEventMessage.setRequestResponseCode(OK);
		Event event = new Event(CustomerTokensReturned, new Object[]{ correlationId, tokenEventMessage});
		verify(queue).publish(event);
		assertNotNull(tokenEventMessage.getTokenList());
	}

	@Given("a registered customer with id {string} with {int} tokens")
	public void a_registered_customer_with_id_with_tokens(String uuid, Integer nTokens) {
		List<Token> tokenList = new ArrayList<>();

		userUUID = UUID.fromString(uuid);
		tokenRepository.addTokens(userUUID, tokenList);
        assertTrue(tokenRepository.getTokens(userUUID).isEmpty());
	}

	@Then("a response CustomerTokensReturned {string} is sent and the system throws an exception with message {string}")
	public void a_response_customer_tokens_returned_is_sent_and_the_system_throws_an_exception_with_message(
			String CustomerTokensReturned, String exceptionMessage) {
		tokenEventMessage.setRequestResponseCode(BAD_REQUEST);
		tokenEventMessage.setExceptionMessage(exceptionMessage);
		Event event = new Event(CustomerTokensReturned, new Object[]{ correlationId, tokenEventMessage});
		verify(queue).publish(event);
	}

}
