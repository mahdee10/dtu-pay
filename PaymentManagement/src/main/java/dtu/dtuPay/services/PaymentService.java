package dtu.dtuPay.services;

import dtu.dtuPay.models.*;
import dtu.dtuPay.repositeries.PaymentRepository;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

    private static final String GET_PAYMENTS_REQUESTED = "GetPaymentsRequested";
    private static final String PAYMENTS_FETCHED = "PaymentsFetched";
    private static final String GET_CUSTOMER_PAYMENTS_REQUESTED = "GetCustomerPaymentsRequested";
    private static final String CUSTOMER_PAYMENTS_FETCHED = "CustomerPaymentsFetched";
    private static final String GET_MERCHANT_PAYMENTS_REQUESTED = "GetMerchantPaymentsRequested";
    private static final String MERCHANT_PAYMENTS_FETCHED = "MerchantPaymentsFetched";
    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_COMPLETED = "PaymentCompleted";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    private MessageQueue queue;
    private PaymentRepository paymentRepository = PaymentRepository.getInstance();
    BankServiceImplementation bankService;

    public Map<CorrelationId, CompletableFuture<AccountEventMessage>> accountCorrelations = new ConcurrentHashMap<>();
    public Map<CorrelationId, CompletableFuture<TokenEventMessage>> tokenCorrelations = new ConcurrentHashMap<>();

    public PaymentService(MessageQueue mq, BankServiceImplementation bankService) {
        this.bankService =  bankService;
        this.queue = mq;
        // Get payments
        this.queue.addHandler(GET_PAYMENTS_REQUESTED, this::handleGetPaymentsRequested);
        // Get customerPayments
        this.queue.addHandler(GET_CUSTOMER_PAYMENTS_REQUESTED, this::handleGetCustomerPaymentsRequested);
        // Get merchantPayments
        this.queue.addHandler(GET_MERCHANT_PAYMENTS_REQUESTED, this::handleGetMerchantPaymentsRequested);
        // Request payment
        this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
    }

    public void publishResponse(CorrelationId correlationId, PaymentEventMessage paymentEventMessage) {
        Event failureEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId, paymentEventMessage});
        queue.publish(failureEvent);
    }
    /**
     * @author Ionut Andrici s242956
     */
    public void handlePaymentRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = ev.getArgument(1, PaymentEventMessage.class);

        try {
            // Execute Payment
            bankService.transferMoney(
                    eventMessage.getCustomerBankAccount(), // Debtor account
                    eventMessage.getMerchantBankAccount(), // Creditor account
                    BigDecimal.valueOf(eventMessage.getAmount()),       // Amount to transfer
                    "Money is being transferred"                               // Empty description
            );
        } catch (BankServiceException_Exception e) {
            eventMessage.setExceptionMessage(e.getMessage());
            eventMessage.setRequestResponseCode(BAD_REQUEST);

            publishResponse(correlationId, eventMessage);
            return;
        }

        // Save customer and merchant payment info
        Payment payment = new Payment(
                eventMessage.getCustomerToken(),
                eventMessage.getMerchantId(),
                eventMessage.getAmount()
        );

        paymentRepository.addCustomerPayment(eventMessage.getCustomerId(), payment.getId());
        paymentRepository.addMerchantPayment(eventMessage.getMerchantId(), payment.getId());
        paymentRepository.addPayment(payment);

        // Publish completion event
        eventMessage.setRequestResponseCode(OK);
        eventMessage.setPaymentId(payment.getId());

        publishResponse(correlationId, eventMessage);
    }

    public void handleGetMerchantPaymentsRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = ev.getArgument(1, PaymentEventMessage.class);

        List<Payment> paymentList = paymentRepository.getMerchantPayments(eventMessage.getMerchantId());
        paymentList.sort(Comparator.comparing(Payment::getId));

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setPaymentList(paymentList);

        Event event = new Event(MERCHANT_PAYMENTS_FETCHED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    /**
     * @author Mihai Munteanu s242996
     */
    public void handleGetCustomerPaymentsRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = ev.getArgument(1, PaymentEventMessage.class);

        List<Payment> paymentList = paymentRepository.getCustomerPayments(eventMessage.getCustomerId());
        paymentList.sort(Comparator.comparing(Payment::getId));

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setPaymentList(paymentList);

        Event event = new Event(CUSTOMER_PAYMENTS_FETCHED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleGetPaymentsRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        PaymentEventMessage eventMessage = ev.getArgument(1, PaymentEventMessage.class);

        List<Payment> paymentList = paymentRepository.getPayments();
        paymentList.sort(Comparator.comparing(Payment::getId));

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setPaymentList(paymentList);

        Event event = new Event(PAYMENTS_FETCHED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }
}
