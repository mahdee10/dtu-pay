package reporting;

import io.cucumber.java.en.*;
import messaging.implementations.RabbitMqQueue;
import org.dtu.reporting.models.Payment;
import org.dtu.reporting.services.ReportingService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReportingServiceSteps {

    private ReportingService reportingService;
    private List<Payment> payments;

    @Given("the reporting service is running")
    public void the_reporting_service_is_running() {
        var mq = new RabbitMqQueue("localhost");
        reportingService = new ReportingService(mq);
    }

    @When("I request all payments")
    public void i_request_all_payments() throws InterruptedException {
        reportingService.requestPayments();
        Thread.sleep(3000);
        payments = reportingService.getAllPayments();
    }

    @Then("I should see all payments stored in the repository")
    public void i_should_see_all_payments_stored_in_the_repository() {
        assertNotNull(payments, "Payments list should not be null.");
        assertFalse(payments.isEmpty(), "Payments list should not be empty.");
        System.out.println("All Payments:");
        payments.forEach(System.out::println);
    }

    @Given("a customer with ID {string} exists")
    public void a_customer_with_id_exists(String customerId) {
        UUID.fromString(customerId);
    }

    @When("I request payments for the customer")
    public void i_request_payments_for_the_customer() throws InterruptedException {
        var customerId = UUID.fromString("7911f9a4-440f-41b5-ae69-1082ddc7be69");
        reportingService.requestCustomerPayments(customerId);
        Thread.sleep(3000);
        payments = reportingService.getCustomerPayments(customerId);
    }

    @Then("I should see payments for the customer stored in the repository")
    public void i_should_see_payments_for_the_customer_stored_in_the_repository() {
        assertNotNull(payments, "Payments list should not be null.");
        assertFalse(payments.isEmpty(), "Payments list should not be empty.");
        payments.forEach(payment -> assertEquals(
                UUID.fromString("7911f9a4-440f-41b5-ae69-1082ddc7be69"),
                payment.getCustomerToken(),
                "Customer token should match the expected customer ID."
        ));
        System.out.println("Customer Payments:");
        payments.forEach(System.out::println);
    }

    @Given("a merchant with ID {string} exists")
    public void a_merchant_with_id_exists(String merchantId) {
        UUID.fromString(merchantId);
    }

    @When("I request payments for the merchant")
    public void i_request_payments_for_the_merchant() throws InterruptedException {
        var merchantId = UUID.fromString("5a51e254-e9bf-4762-81d7-eeadf10347b6");
        reportingService.requestMerchantPayments(merchantId);
        Thread.sleep(3000);
        payments = reportingService.getMerchantPayments(merchantId);
    }

    @Then("I should see payments for the merchant stored in the repository")
    public void i_should_see_payments_for_the_merchant_stored_in_the_repository() {
        assertNotNull(payments, "Payments list should not be null.");
        assertFalse(payments.isEmpty(), "Payments list should not be empty.");
        payments.forEach(payment -> assertEquals(
                UUID.fromString("5a51e254-e9bf-4762-81d7-eeadf10347b6"),
                payment.getMerchantId(),
                "Merchant ID should match the expected merchant ID."
        ));
        System.out.println("Merchant Payments:");
        payments.forEach(System.out::println);
    }
}