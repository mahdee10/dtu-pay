package dtu.dtuPay.services;

import com.google.gson.Gson;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.Payment;
import dtu.dtuPay.models.PaymentRequestDto;
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

    private MessageQueue queue;
    private PaymentRepository paymentRepository = PaymentRepository.getInstance();
    BankServiceImplementation bankService;
    public Map<CorrelationId, CompletableFuture<Boolean>> correlations = new ConcurrentHashMap<>();
    public Map<CorrelationId, CompletableFuture<UUID>> tokenValidationCorrelations = new ConcurrentHashMap<>();
    public Map<CorrelationId, CompletableFuture<String>> bankAccountCorrelations = new ConcurrentHashMap<>();

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

    private void publishPaymentExceptionally(CorrelationId correlationId, boolean isPaymentSuccessful, String exceptionMessage) {
        Event failureEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId, isPaymentSuccessful, exceptionMessage});
        queue.publish(failureEvent);
    }

    private void publishPaymentCompleted(CorrelationId correlationId, boolean isPaymentSuccessful, UUID paymentId) {
        Event failureEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId, isPaymentSuccessful, paymentId});
        queue.publish(failureEvent);
    }

    private void handleTokenValidationReturned(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        boolean isValid = ev.getArgument(1, boolean.class);

        if (!isValid)
        {
            tokenValidationCorrelations.get(correlationId).complete(null);
            return;
        }

        UUID customerId = ev.getArgument(2, UUID.class);
        tokenValidationCorrelations.get(correlationId).complete(customerId);
    }

    private UUID validateCustomerToken(UUID customerToken) {
        CorrelationId customerValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<UUID> futureCustomerTokenValidation = new CompletableFuture<>();
        tokenValidationCorrelations.put(customerValidationCorrelationId, futureCustomerTokenValidation);

        Event customerTokenValidationEvent = new Event(TOKEN_VALIDATION_REQUESTED,
                new Object[] { customerValidationCorrelationId, customerToken });
        queue.publish(customerTokenValidationEvent);

        return futureCustomerTokenValidation.join();
    }

    private void handleMerchantAccountValidationResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        String merchantBankAccount = ev.getArgument(1, String.class);
        boolean isValid = ev.getArgument(2, boolean.class);

        bankAccountCorrelations.get(correlationId).complete(isValid ? merchantBankAccount : null);
    }

    public String validateMerchantAccount(UUID merchantId) {
        CorrelationId merchantValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<String> futureMerchantValidation = new CompletableFuture<>();
        bankAccountCorrelations.put(merchantValidationCorrelationId, futureMerchantValidation);

        Event merchantAccountValidationEvent = new Event(VALIDATE_MERCHANT_ACCOUNT_REQUESTED,
                new Object[] { merchantValidationCorrelationId, merchantId });
        queue.publish(merchantAccountValidationEvent);

        return futureMerchantValidation.join();
    }

    private void handleGetCustomerBankAccountResponse(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        String customerBankAccount = e.getArgument(2, String.class);

        if (customerBankAccount.isEmpty()) {
            bankAccountCorrelations.get(correlationId).complete(null);
            return;
        }

        bankAccountCorrelations.get(correlationId).complete(customerBankAccount);
    }

    public String getCustomerBankAccount(UUID customerId) {
        CorrelationId customerGetBankAccountCorrelationId = CorrelationId.randomId();
        CompletableFuture<String> futureGetCustomerBankAccount = new CompletableFuture<>();
        bankAccountCorrelations.put(customerGetBankAccountCorrelationId, futureGetCustomerBankAccount);

        Event customerTokenValidationEvent = new Event(GET_CUSTOMER_BANK_ACCOUNT_REQUESTED,
                new Object[] { customerGetBankAccountCorrelationId, customerId });
        queue.publish(customerTokenValidationEvent);

        return futureGetCustomerBankAccount.join();
    }

    private void handleUseTokenResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        boolean tokenUsedSuccessfully = ev.getArgument(1, boolean.class);

        correlations.get(correlationId).complete(tokenUsedSuccessfully);
    }

    private boolean markTokenAsUsed(UUID customerToken) {
        CorrelationId useTokenCorrelationId = CorrelationId.randomId();
        CompletableFuture<Boolean> futureUseToken = new CompletableFuture<>();
        correlations.put(useTokenCorrelationId, futureUseToken);

        Event useTokenEvent = new Event(USE_TOKEN_REQUEST, new Object[] { useTokenCorrelationId, customerToken });
        queue.publish(useTokenEvent);

        return futureUseToken.join();
    }

    public void handlePaymentRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        PaymentRequestDto paymentRequestDto = ev.getArgument(1, PaymentRequestDto.class);

        Payment payment = new Payment(
                paymentRequestDto.getCustomerToken(),
                paymentRequestDto.getMerchantId(),
                paymentRequestDto.getAmount()
        );

        // Merchant account validation
        String merchantBankAccount = validateMerchantAccount(paymentRequestDto.getMerchantId());
        if (merchantBankAccount.isEmpty()) {
            String exceptionMessage = "Merchant Bank Account Not Found";
            publishPaymentExceptionally(correlationId, false, exceptionMessage);
            return;
        }

        // Customer Token Validation, saves customerId -> PaymentId in repository
        UUID customerId = validateCustomerToken(paymentRequestDto.getCustomerToken());
        if (customerId == null) {
            String exceptionMessage = "Customer Token Validation Failed";
            publishPaymentExceptionally(correlationId, false, exceptionMessage);
            return;
        }

        String customerBankAccount = getCustomerBankAccount(customerId);

        // Mark token as used
        boolean tokenIsUsed = markTokenAsUsed(paymentRequestDto.getCustomerToken());
        if (!tokenIsUsed) {
            String exceptionMessage = "Using Customer Token Failed";
            publishPaymentExceptionally(correlationId, false, exceptionMessage);
        }

        try {
            // Execute Payment
            bankService.transferMoney(
                    customerBankAccount, // Debtor account
                    merchantBankAccount, // Creditor account
                    BigDecimal.valueOf(paymentRequestDto.getAmount()),       // Amount to transfer
                    "Money is being transferred"                               // Empty description
            );
        } catch (BankServiceException_Exception e) {
            String exceptionMessage = "Payment execution failed.";
            publishPaymentExceptionally(correlationId, false, exceptionMessage);
            return;
        }

        // Save customer and merchant payment info
        paymentRepository.addCustomerPayment(customerId, payment.getId());
        paymentRepository.addMerchantPayment(paymentRequestDto.getMerchantId(), payment.getId());
        paymentRepository.addPayment(payment);

        // Publish completion event
        publishPaymentCompleted(correlationId, true, payment.getId());
    }

    public void handleGetMerchantPaymentsRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        UUID merchantId = ev.getArgument(1, UUID.class);

        List<Payment> paymentList = paymentRepository.getMerchantPayments(merchantId);
        paymentList.sort(Comparator.comparing(Payment::getId));

        Event event = new Event(MERCHANT_PAYMENTS_FETCHED,
                new Object[] { correlationId, serialisePaymentListToJson(paymentList) });
        queue.publish(event);
    }

    public void handleGetCustomerPaymentsRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        UUID customerId = ev.getArgument(1, UUID.class);

        List<Payment> paymentList = paymentRepository.getCustomerPayments(customerId);
        paymentList.sort(Comparator.comparing(Payment::getId));

        Event event = new Event(CUSTOMER_PAYMENTS_FETCHED,
                new Object[] { correlationId, serialisePaymentListToJson(paymentList) });
        queue.publish(event);
    }

    public void handleGetPaymentsRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        List<Payment> paymentList = paymentRepository.getPayments();
        paymentList.sort(Comparator.comparing(Payment::getId));

        Event event = new Event(PAYMENTS_FETCHED, new Object[] { correlationId, serialisePaymentListToJson(paymentList) });
        queue.publish(event);
    }

    private String serialisePaymentListToJson(List<Payment> paymentList) {
        Gson gson = new Gson();
        return gson.toJson(paymentList);
    }

}
