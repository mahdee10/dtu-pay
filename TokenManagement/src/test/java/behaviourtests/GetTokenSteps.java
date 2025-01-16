package behaviourtests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

public class GetTokenSteps {

	private CompletableFuture<Event> publishedEvent = new CompletableFuture<>();

	private MessageQueue q = new MessageQueue() {

		@Override
		public void publish(Event event) {
			publishedEvent.complete(event);
		}

		@Override
		public void addHandler(String eventType, Consumer<Event> handler) {
		}
		
	};


	public GetTokenSteps() {
	}
	
	@Given("a registered customer with id {string} with at least {int} token")
	public void a_registered_customer_with_id_with_at_least_token(String string, Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("the customer requests the token system to retreive a token")
	public void the_customer_requests_the_token_system_to_retreive_a_token() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("the event {string} is sent")
	public void the_event_is_sent(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("a response  {string} is sent")
	public void a_response_is_sent(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("a client receives a token with token id {string}")
	public void a_client_receives_a_token_with_token_id(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Given("a registered customer with id {string} with {int} tokens")
	public void a_registered_customer_with_id_with_tokens(String string, Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("the system throws an exception with message {string}")
	public void the_system_throws_an_exception_with_message(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

}
