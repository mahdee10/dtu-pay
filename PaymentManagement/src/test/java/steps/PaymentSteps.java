package steps;

import com.google.gson.Gson;
import dtu.dtuPay.models.Payment;
import dtu.dtuPay.repositeries.PaymentRepository;
import dtu.dtuPay.services.CorrelationId;
import dtu.dtuPay.services.PaymentService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PaymentSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private PaymentService service = new PaymentService(queue);
    private PaymentRepository repository = PaymentRepository.getInstance();
    private UUID merchantId;
    private UUID customerToken;
    private double amount;
    private Payment expectedPayment;
    private List<Payment> expectedPaymentList;
    private CorrelationId correlationId;

    public PaymentSteps() {}

    // pay service test
    @When("{string} event to execute a payment is received")
    public void eventToExecuteAPaymentIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        customerToken = UUID.randomUUID();
        merchantId = UUID.randomUUID();
        amount = 20.0;

        Event event = new Event(eventName, new Object[] { correlationId, customerToken, merchantId, amount });
        service.handlePaymentRequested(event);
    }

    @Then("the payment is executed and the {string} event is sent")
    public void thePaymentIsExecutedAndTheEventIsSent(String eventName) {
        expectedPayment = repository.getMerchantPayments(merchantId)
                .stream().filter(payment ->
                        payment.getCustomerToken().equals(customerToken) &&
                        payment.getAmount() == amount)
                .findFirst().orElse(null);

        Event event = new Event(eventName, new Object[] { correlationId, true, expectedPayment.getId()});
        verify(queue).publish(event);
    }

    @Then("the payment confirmation is received by the merchant")
    public void thePaymentConfirmationIsReceivedByTheMerchant() {
        assertNotNull(expectedPayment.getId());
    }

    // getPayments service test
    @Given("a list of payments are present in the payment repository")
    public void aListOfPaymentsArePresentInThePaymentRepository() {
        repository.dropData();
        expectedPaymentList = new ArrayList<>(){};

        for (int i = 0; i < 3; i++) {
            Payment payment = new Payment(UUID.randomUUID(),UUID.randomUUID(),10 + i);
            expectedPaymentList.add(payment);
            repository.addPayment(payment);
        }
    }

    @When("{string} event to get all payments is received")
    public void eventToGetAllPaymentsIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[] { correlationId });
        service.handleGetPaymentsRequested(event);
    }

    @Then("the payments are fetched and the {string} event is sent")
    public void thePaymentsAreFetchedAndTheEventIsSent(String eventName) {
        expectedPaymentList.sort(Comparator.comparing(Payment::getId));
        Gson gson = new Gson();
        String jsonString = gson.toJson(expectedPaymentList.toArray());

        Event event = new Event(eventName, new Object[] { correlationId, jsonString });
        verify(queue).publish(event);

        // Deserialize List object
//        Gson gson = new Gson();
//        String jsonResponse = actualEvent.getArgument(0, String.class);
//        List<Payment> actualPayments = gson.fromJson(jsonResponse, new GenericType<List<Payment>>(){}.getType());
    }

    @Then("the user gets the list of payments")
    public void the_user_gets_the_list_of_payments() {
        assertFalse(expectedPaymentList.isEmpty());
    }

    // getCustomerPayments service test
    @Given("a list of payments are present in the payment repository for customer {string}")
    public void aListOfPaymentsArePresentInThePaymentRepositoryForCustomer(String customerId) {
        expectedPaymentList = new ArrayList<>(){};

        for (int i = 0; i < 3; i++) {
            Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(),10 + i);

            expectedPaymentList.add(payment);
            repository.addPayment(payment);
            repository.addCustomerPayment(UUID.fromString(customerId), payment.getId());
        }
    }

    @When("{string} event to get all the customer payments is received for customer {string}")
    public void eventToGetAllTheCustomerPaymentsIsReceived(String eventName, String customerId) {
        correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[] { correlationId, UUID.fromString(customerId) });
        service.handleGetCustomerPaymentsRequested(event);
    }

    // getMerchantPayments service test
    @Given("a list of payments are present in the payment repository for merchant {string}")
    public void aListOfPaymentsArePresentInThePaymentRepositoryForMerchant(String merchantIdValue) {
        expectedPaymentList = new ArrayList<>(){};
        UUID merchantId = UUID.fromString(merchantIdValue);
        for (int i = 0; i < 3; i++) {
            Payment payment = new Payment(UUID.randomUUID(),merchantId,10 + i);

            expectedPaymentList.add(payment);
            repository.addPayment(payment);
            repository.addMerchantPayment(merchantId, payment.getId());
        }
    }

    @When("{string} event to get all the merchant payments is received for merchant {string}")
    public void eventToGetAllTheMerchantPaymentsIsReceived(String eventName, String merchantId) {
        correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[] { correlationId, UUID.fromString(merchantId) });
        service.handleGetMerchantPaymentsRequested(event);
    }
}
