package reporting;

import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.Payment;
import dtu.dtuPay.models.PaymentEventMessage;
import dtu.dtuPay.models.ReportingEventMessage;
import dtu.dtuPay.services.ReportingService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReportingServiceSteps {

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    private MessageQueue queue = mock(MessageQueue.class);
    private ReportingService reportingService = new ReportingService(queue);
    private UUID customerId;
    private UUID merchantId;
    private CorrelationId correlationId;
    ReportingEventMessage eventMessage;
    private List<Payment> expectedPaymentList;

    /**
     * @author Ionut Andrici s242956
     */
    @Given("a list of payments are present in the payment repository for customer with ID {string}")
    public void aListOfPaymentsArePresentInThePaymentRepositoryForCustomerWithID(String customerIdString) {
        customerId = UUID.fromString(customerIdString);
        aListOfPaymentsArePresentInThePaymentRepository();
    }

    @Given("a list of payments are present in the payment repository for merchant with ID {string}")
    public void aListOfPaymentsArePresentInThePaymentRepositoryForMerchantWithID(String merchantIdString) {
        merchantId = UUID.fromString(merchantIdString);
        aListOfPaymentsArePresentInThePaymentRepository();
    }

    /**
     * @author Raihanullah Mehran s233837
     */
    @Given("a list of payments are present in the payment repository")
    public void aListOfPaymentsArePresentInThePaymentRepository() {
        expectedPaymentList = new ArrayList<>(){};
        customerId = customerId == null ? UUID.randomUUID() : customerId;
        merchantId = merchantId == null ? UUID.randomUUID() : merchantId;

        for (int i = 0; i < 3; i++) {
            UUID customerToken = UUID.randomUUID();
            Payment payment = new Payment(customerToken, merchantId, 10 + i);
            expectedPaymentList.add(payment);
        }

        // Stub queue.publish to simulate event handling
        doAnswer(invocation -> {
            Event publishedEvent = invocation.getArgument(0, Event.class);
            CorrelationId validationCorrelationId = publishedEvent.getArgument(0, CorrelationId.class);

            PaymentEventMessage paymentEventMessage;

            switch (publishedEvent.getTopic()) {
                case "GetPaymentsRequested":
                    paymentEventMessage = new PaymentEventMessage();
                    paymentEventMessage.setRequestResponseCode(OK);
                    paymentEventMessage.setPaymentList(expectedPaymentList);
                    reportingService.paymentCorrelations.get(validationCorrelationId).complete(paymentEventMessage);
                    break;
                case "GetCustomerPaymentsRequested":
                    paymentEventMessage = new PaymentEventMessage();
                    paymentEventMessage.setRequestResponseCode(OK);
                    paymentEventMessage.setPaymentList(expectedPaymentList);
                    paymentEventMessage.setCustomerId(customerId);
                    reportingService.paymentCorrelations.get(validationCorrelationId).complete(paymentEventMessage);
                    break;
                case "GetMerchantPaymentsRequested":
                    paymentEventMessage = new PaymentEventMessage();
                    paymentEventMessage.setRequestResponseCode(OK);
                    paymentEventMessage.setPaymentList(expectedPaymentList);
                    paymentEventMessage.setMerchantId(merchantId);
                    reportingService.paymentCorrelations.get(validationCorrelationId).complete(paymentEventMessage);
                    break;
            }

            return null;
        }).when(queue).publish(any(Event.class));
    }

    @When("{string} event to get all payments is received")
    public void eventToGetAllPaymentsIsReceived(String eventName) {
        correlationId = CorrelationId.randomId();
        eventMessage = new ReportingEventMessage();
        Event event;

        switch (eventName) {
            case "ReportingGetPaymentsRequested":
                event = new Event(eventName, new Object[] { correlationId, eventMessage });
                reportingService.handleReportingGetPaymentsRequested(event);
                break;
            case "ReportingGetCustomerPaymentsRequested":
                eventMessage.setCustomerId(customerId);
                event = new Event(eventName, new Object[] { correlationId, eventMessage });
                reportingService.handleReportingGetCustomerPaymentsRequested(event);
                break;
            case "ReportingGetMerchantPaymentsRequested":
                eventMessage.setMerchantId(merchantId);
                event = new Event(eventName, new Object[] { correlationId, eventMessage });
                reportingService.handleReportingGetMerchantPaymentsRequested(event);
                break;
        }
    }

    @Then("the payments are fetched and the {string} event is sent")
    public void thePaymentsAreFetchedAndTheEventIsSent(String eventName) {
        eventMessage.setRequestResponseCode(OK);
        eventMessage.setPaymentList(expectedPaymentList);

        Event event = new Event(eventName, new Object[] { correlationId, eventMessage });
        verify(queue).publish(event);
    }

    @Then("the user gets the list of payments")
    public void theUserGetsTheListOfPayments() {
        assertFalse(expectedPaymentList.isEmpty());
    }
}