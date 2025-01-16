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

public class RequestTokenSteps {

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


	public RequestTokenSteps() {
	}

	@Given("a registered customer with id {string} with more than {int} active token")
	public void a_registered_customer_with_id_with_more_than_active_token(String string, Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("a registered customer with id {string} requests tokens")
	public void a_registered_customer_with_id_requests_tokens(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("an event {string} is sent")
	public void an_event_is_sent(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("a response event {string} is sent")
	public void a_response_event_is_sent(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("the response is unsuccessful")
	public void the_response_is_unsuccessful() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("the request is denied")
	public void the_request_is_denied() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Given("a registered customer with id {string} with {int} or less active tokens")
	public void a_registered_customer_with_id_with_or_less_active_tokens(String string, Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("a registered customer with id {string} requests less than {int} tokens")
	public void a_registered_customer_with_id_requests_less_than_tokens(String string, Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("a response event \"RequestTokensResponse\"is sent")
	public void a_response_event_request_tokens_response_is_sent() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("the response is successful")
	public void the_response_is_successful() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("the service will grant {int} active tokens to user")
	public void the_service_will_grant_active_tokens_to_user(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("a registered customer with id {string} requests more than {int} tokens")
	public void a_registered_customer_with_id_requests_more_than_tokens(String string, Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("response is unsuccessful")
	public void response_is_unsuccessful() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

}