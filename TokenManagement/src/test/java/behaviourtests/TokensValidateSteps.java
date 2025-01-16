package behaviourtests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.example.services.TokenService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;

public class TokensValidateSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TokenService s = new TokenService(queue);


    @Given("a payment service supplies a token with id {string} to validate")
    public void a_payment_service_supplies_a_token_with_id_to_validate(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("the event {string} is received")
    public void the_event_is_received(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the event {string} is sent")
    public void the_event_is_sent(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("a token with id {string} exists and is active")
    public void a_token_with_id_exists_and_is_active(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("token validation is successful with value {string}")
    public void token_validation_is_successful_with_value(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("a token with id {string} exists but is inactive")
    public void a_token_with_id_exists_but_is_inactive(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("token validation is not successful and contains value {string}")
    public void token_validation_is_not_successful_and_contains_value(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("a payment service supplies a token with {string} to validate")
    public void a_payment_service_supplies_a_token_with_to_validate(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("a token with id {string} does not exist")
    public void a_token_with_id_does_not_exist(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("an exception is thrown with message {string}")
    public void an_exception_is_thrown_with_message(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    public TokensValidateSteps() {}




}
