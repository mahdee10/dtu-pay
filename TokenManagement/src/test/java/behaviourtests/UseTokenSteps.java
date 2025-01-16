
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

public class UseTokenSteps {

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


	public UseTokenSteps() {
	}
	
	@Given("a payment service supplies a valid token with UUID {string} to pay")
	public void a_payment_service_supplies_a_valid_token_with_uuid_to_pay(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("a response {string} is sent")
	public void a_response_is_sent(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("a token with id {string} is marked as invalid")
	public void a_token_with_id_is_marked_as_invalid(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("a token with id {string} is removed from the active token list and placed in an inactive token list")
	public void a_token_with_id_is_removed_from_the_active_token_list_and_placed_in_an_inactive_token_list(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}


}
