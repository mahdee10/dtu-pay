package dtu.dtuPay.services;

import com.google.gson.Gson;
import dtu.dtuPay.models.*;
import dtu.dtuPay.repositeries.PaymentRepository;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    // External events
    private static final String TOKEN_VALIDATION_REQUESTED = "TokenValidationRequest";
    private static final String TOKEN_VALIDATION_RETURNED = "TokenValidationReturned";
    private static final String GET_CUSTOMER_BANK_ACCOUNT_REQUESTED = "GetCustomerBankAccountRequested";
    private static final String CUSTOMER_BANK_ACCOUNT_RESPONSE = "CustomerBankAccountResponse";
    private static final String VALIDATE_MERCHANT_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATION_RESPONSE = "MerchantAccountValidationResponse";

    private static final String USE_TOKEN_REQUEST = "UseTokenRequest";
    private static final String USE_TOKEN_RESPONSE = "UseTokenResponse";

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

        this.queue.addHandler(TOKEN_VALIDATION_RETURNED, this::handleTokenValidationReturned);
        this.queue.addHandler(USE_TOKEN_RESPONSE, this::handleUseTokenResponse);
        this.queue.addHandler(MERCHANT_ACCOUNT_VALIDATION_RESPONSE, this::handleMerchantAccountValidationResponse);
        this.queue.addHandler(CUSTOMER_BANK_ACCOUNT_RESPONSE, this::handleGetCustomerBankAccountResponse);
    }

    public void publishResponse(CorrelationId correlationId, PaymentEventMessage paymentEventMessage) {
        Event failureEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId, paymentEventMessage});
        queue.publish(failureEvent);
    }

    private void handleTokenValidationReturned(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = ev.getArgument(1, TokenEventMessage.class);

        tokenCorrelations.get(correlationId).complete(eventMessage);
    }

    private TokenEventMessage validateCustomerToken(UUID customerToken) {
        CorrelationId customerValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<TokenEventMessage> futureCustomerTokenValidation = new CompletableFuture<>();
        tokenCorrelations.put(customerValidationCorrelationId, futureCustomerTokenValidation);

        TokenEventMessage tokenEventMessage = new TokenEventMessage();
        tokenEventMessage.setTokenUUID(customerToken);

        Event customerTokenValidationEvent = new Event(TOKEN_VALIDATION_REQUESTED,
                new Object[] { customerValidationCorrelationId, tokenEventMessage });
        queue.publish(customerTokenValidationEvent);

        return futureCustomerTokenValidation.join();
    }

    private void handleMerchantAccountValidationResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        accountCorrelations.get(correlationId).complete(eventMessage);
    }

    public AccountEventMessage validateMerchantAccount(UUID merchantId) {
        CorrelationId merchantValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<AccountEventMessage> futureMerchantValidation = new CompletableFuture<>();
        accountCorrelations.put(merchantValidationCorrelationId, futureMerchantValidation);

        AccountEventMessage accountEventMessage = new AccountEventMessage();
        accountEventMessage.setMerchantId(merchantId);

        Event merchantAccountValidationEvent = new Event(VALIDATE_MERCHANT_ACCOUNT_REQUESTED,
                new Object[] { merchantValidationCorrelationId, accountEventMessage });
        queue.publish(merchantAccountValidationEvent);

        return futureMerchantValidation.join();
    }

    private void handleGetCustomerBankAccountResponse(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = e.getArgument(1, AccountEventMessage.class);

        accountCorrelations.get(correlationId).complete(eventMessage);
    }

    public AccountEventMessage getCustomerBankAccount(UUID customerId) {
        CorrelationId customerGetBankAccountCorrelationId = CorrelationId.randomId();
        CompletableFuture<AccountEventMessage> futureGetCustomerBankAccount = new CompletableFuture<>();
        accountCorrelations.put(customerGetBankAccountCorrelationId, futureGetCustomerBankAccount);

        AccountEventMessage accountEventMessage = new AccountEventMessage();
        accountEventMessage.setCustomerId(customerId);

        Event customerTokenValidationEvent = new Event(GET_CUSTOMER_BANK_ACCOUNT_REQUESTED,
                new Object[] { customerGetBankAccountCorrelationId, accountEventMessage });
        queue.publish(customerTokenValidationEvent);

        return futureGetCustomerBankAccount.join();
    }

    private void handleUseTokenResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = ev.getArgument(1, TokenEventMessage.class);

        tokenCorrelations.get(correlationId).complete(eventMessage);
    }

    private TokenEventMessage markTokenAsUsed(UUID customerToken) {
        CorrelationId useTokenCorrelationId = CorrelationId.randomId();
        CompletableFuture<TokenEventMessage> futureUseToken = new CompletableFuture<>();
        tokenCorrelations.put(useTokenCorrelationId, futureUseToken);

        TokenEventMessage tokenEventMessage = new TokenEventMessage();
        tokenEventMessage.setTokenUUID(customerToken);

        Event useTokenEvent = new Event(USE_TOKEN_REQUEST, new Object[] { useTokenCorrelationId, tokenEventMessage });
        queue.publish(useTokenEvent);

        return futureUseToken.join();
    }

    public void handlePaymentRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        PaymentEventMessage paymentEventMessage = ev.getArgument(1, PaymentEventMessage.class);

        // Merchant account validation
        AccountEventMessage responseMerchantValidation = validateMerchantAccount(paymentEventMessage.getMerchantId());
        if (!responseMerchantValidation.getIsValidAccount() || responseMerchantValidation.getRequestResponseCode() != OK) {
            paymentEventMessage.setExceptionMessage(responseMerchantValidation.getExceptionMessage());
            paymentEventMessage.setRequestResponseCode(BAD_REQUEST);

            publishResponse(correlationId, paymentEventMessage);
            return;
        }

        // Customer Token Validation
        TokenEventMessage responseValidateCustomerToken = validateCustomerToken(paymentEventMessage.getCustomerToken());
        if (responseValidateCustomerToken.getRequestResponseCode() != OK || !responseValidateCustomerToken.getIsValid()) {
            paymentEventMessage.setExceptionMessage(responseValidateCustomerToken.getExceptionMessage());
            paymentEventMessage.setRequestResponseCode(BAD_REQUEST);

            publishResponse(correlationId, paymentEventMessage);
            return;
        }

        // Get Customer Bank Account
        AccountEventMessage responseGetCustomerBankAccount = getCustomerBankAccount(responseValidateCustomerToken.getCustomerId());
        if (responseGetCustomerBankAccount.getRequestResponseCode() != OK) {
            paymentEventMessage.setExceptionMessage(responseGetCustomerBankAccount.getExceptionMessage());
            paymentEventMessage.setRequestResponseCode(BAD_REQUEST);

            publishResponse(correlationId, paymentEventMessage);
            return;
        }

        // Mark token as used
        TokenEventMessage responseUseToken = markTokenAsUsed(paymentEventMessage.getCustomerToken());
        if (responseUseToken.getRequestResponseCode() != OK || !responseUseToken.getIsTokenUsed()) {
            paymentEventMessage.setExceptionMessage(responseGetCustomerBankAccount.getExceptionMessage());
            paymentEventMessage.setRequestResponseCode(BAD_REQUEST);

            publishResponse(correlationId, paymentEventMessage);
            return;
        }

        try {
            // Execute Payment
            bankService.transferMoney(
                    responseGetCustomerBankAccount.getBankAccount(), // Debtor account
                    responseMerchantValidation.getBankAccount(), // Creditor account
                    BigDecimal.valueOf(paymentEventMessage.getAmount()),       // Amount to transfer
                    "Money is being transferred"                               // Empty description
            );
        } catch (BankServiceException_Exception e) {
            paymentEventMessage.setExceptionMessage("Bank payment execution failed.");
            paymentEventMessage.setRequestResponseCode(BAD_REQUEST);

            publishResponse(correlationId, paymentEventMessage);
            return;
        }

        // Save customer and merchant payment info
        Payment payment = new Payment(
                paymentEventMessage.getCustomerToken(),
                paymentEventMessage.getMerchantId(),
                paymentEventMessage.getAmount()
        );

        paymentRepository.addCustomerPayment(responseValidateCustomerToken.getCustomerId(), payment.getId());
        paymentRepository.addMerchantPayment(paymentEventMessage.getMerchantId(), payment.getId());
        paymentRepository.addPayment(payment);

        // Publish completion event
        paymentEventMessage.setRequestResponseCode(OK);
        paymentEventMessage.setPaymentId(payment.getId());

        publishResponse(correlationId, paymentEventMessage);
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
