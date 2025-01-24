package steps;

import dtu.dtuPay.models.*;
import dtu.dtuPay.repositeries.PaymentRepository;
import dtu.dtuPay.services.BankServiceImplementation;
import dtu.dtuPay.services.PaymentService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private BankServiceImplementation bankService = mock(BankServiceImplementation.class);
    private PaymentService service = new PaymentService(queue, bankService);
    private PaymentRepository repository = PaymentRepository.getInstance();
    private UUID merchantId;
    private UUID customerToken;
    private double amount;
    private Payment expectedPayment;
    private List<Payment> expectedPaymentList;
    private CorrelationId correlationId;
    PaymentEventMessage eventMessage;

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    public PaymentSteps() {}

    /**
     * @author Ionut Andrici s242956
     */
    // pay service test
    @When("{string} event to execute a payment is received")
    public void eventToExecuteAPaymentIsReceived(String eventName) throws BankServiceException_Exception {
        correlationId = CorrelationId.randomId();
        customerToken = UUID.randomUUID();
        merchantId = UUID.randomUUID();
        amount = 20.0;

        eventMessage = new PaymentEventMessage();
        eventMessage.setCustomerToken(customerToken);
        eventMessage.setMerchantId(merchantId);
        eventMessage.setAmount(amount);

        String validCustomerBankAccount = String.valueOf(UUID.randomUUID());
        String validMerchantBankAccount = String.valueOf(UUID.randomUUID());

        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });

        // Stub queue.publish to simulate event handling
        doAnswer(invocation -> {
            Event publishedEvent = invocation.getArgument(0, Event.class);
            CorrelationId validationCorrelationId = publishedEvent.getArgument(0, CorrelationId.class);
            AccountEventMessage accountEventMessage;
            TokenEventMessage tokenEventMessage;
            UUID customerId = UUID.randomUUID();

            switch (publishedEvent.getTopic()) {
                case "ValidateMerchantAccountRequested":
                    accountEventMessage = new AccountEventMessage();
                    accountEventMessage.setMerchantId(merchantId);
                    accountEventMessage.setIsValidAccount(true);
                    accountEventMessage.setRequestResponseCode(OK);
                    accountEventMessage.setBankAccount(validMerchantBankAccount);
                    service.accountCorrelations.get(validationCorrelationId).complete(accountEventMessage);
                    break;
                case "GetCustomerBankAccountRequested":
                    accountEventMessage = new AccountEventMessage();
                    accountEventMessage.setCustomerId(customerId);
                    accountEventMessage.setRequestResponseCode(OK);
                    accountEventMessage.setBankAccount(validCustomerBankAccount);
                    service.accountCorrelations.get(validationCorrelationId).complete(accountEventMessage);
                    break;
                case "TokenValidationRequest":
                    tokenEventMessage = new TokenEventMessage();
                    tokenEventMessage.setTokenUUID(customerToken);
                    tokenEventMessage.setCustomerId(customerId);
                    tokenEventMessage.setRequestResponseCode(OK);
                    tokenEventMessage.setIsValid(true);
                    service.tokenCorrelations.get(validationCorrelationId).complete(tokenEventMessage);
                    break;
                case "UseTokenRequest":
                    tokenEventMessage = new TokenEventMessage();
                    tokenEventMessage.setTokenUUID(customerToken);
                    tokenEventMessage.setRequestResponseCode(OK);
                    tokenEventMessage.setIsTokenUsed(true);
                    service.tokenCorrelations.get(validationCorrelationId).complete(tokenEventMessage);
                    break;
            }

            return null;
        }).when(queue).publish(any(Event.class));

        service.handlePaymentRequested(event);
    }

    @Then("the payment is executed and the {string} event is sent")
    public void thePaymentIsExecutedAndTheEventIsSent(String eventName) {
        expectedPayment = repository.getMerchantPayments(merchantId)
                .stream().filter(payment ->
                        payment.getCustomerToken().equals(customerToken) &&
                        payment.getAmount() == amount)
                .findFirst().orElse(null);

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setPaymentId(expectedPayment.getId());
        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });
        verify(queue).publish(event);
    }

    @Then("the payment confirmation is received by the merchant")
    public void thePaymentConfirmationIsReceivedByTheMerchant() {
        assertNotNull(expectedPayment.getId());
    }


    /**
     * @author Mihai Munteanu s242996
     */
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
        eventMessage = new PaymentEventMessage();

        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });
        service.handleGetPaymentsRequested(event);
    }

    @Then("the payments are fetched and the {string} event is sent")
    public void thePaymentsAreFetchedAndTheEventIsSent(String eventName) {
        expectedPaymentList.sort(Comparator.comparing(Payment::getId));

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setPaymentList(expectedPaymentList);

        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });
        verify(queue).publish(event);
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
        eventMessage = new PaymentEventMessage();
        eventMessage.setCustomerId(UUID.fromString(customerId));

        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });
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
        eventMessage = new PaymentEventMessage();
        eventMessage.setMerchantId(UUID.fromString(merchantId));

        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });
        service.handleGetMerchantPaymentsRequested(event);
    }
}
