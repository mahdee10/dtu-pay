package dtu.dtuPay.services;

import com.google.gson.Gson;
import dtu.dtuPay.models.Payment;
import dtu.dtuPay.repositeries.PaymentRepository;
import messaging.Event;
import messaging.MessageQueue;

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
    private static final String VALIDATE_MERCHANT_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATION_RESPONSE = "MerchantAccountValidationResponse";

    private static final String USE_TOKEN_REQUEST = "UseTokenRequest";
    private static final String USE_TOKEN_RESPONSE = "UseTokenResponse";

    private MessageQueue queue;
    private PaymentRepository paymentRepository = PaymentRepository.getInstance();
    private Map<CorrelationId, CompletableFuture<Boolean>> correlations = new ConcurrentHashMap<>();
    private Map<CorrelationId, CompletableFuture<UUID>> tokenValidationCorrelation = new ConcurrentHashMap<>();

    public PaymentService(MessageQueue mq) {
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
    }

    private void publishPaymentExceptionally(CorrelationId correlationId, boolean isPaymentSuccessful, String exceptionMessage) {
        Exception exception = new Exception(exceptionMessage);
        Event failureEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId, isPaymentSuccessful, exception});
        queue.publish(failureEvent);
    }

    private void publishPaymentCompleted(CorrelationId correlationId, boolean isPaymentSuccessful, UUID paymentId) {
        Event failureEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId, isPaymentSuccessful, paymentId});
        queue.publish(failureEvent);
    }

    private void handleTokenValidationReturned(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        UUID customerId = ev.getArgument(1, UUID.class);
        boolean isValid = ev.getArgument(2, boolean.class);

        if (!isValid) {
            Exception exception = new Exception("Customer Token Validation Failed");
            tokenValidationCorrelation.get(correlationId).completeExceptionally(exception);
        } else {
            tokenValidationCorrelation.get(correlationId).complete(customerId);
        }
    }

    private CompletableFuture<UUID>  validateCustomerToken(UUID customerToken, UUID paymentId, CorrelationId paymentCorrelationId) {
        CorrelationId customerValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<UUID> futureCustomerTokenValidation = new CompletableFuture<>();
        tokenValidationCorrelation.put(customerValidationCorrelationId, futureCustomerTokenValidation);

        Event customerTokenValidationEvent = new Event(TOKEN_VALIDATION_REQUESTED,
                new Object[] { customerValidationCorrelationId, customerToken });
        queue.publish(customerTokenValidationEvent);

        futureCustomerTokenValidation.whenComplete((responseCustomerId, throwable) -> {
            if (throwable != null || responseCustomerId == null) {
                publishPaymentExceptionally(paymentCorrelationId, false, throwable.getMessage());
            }
        });

        return futureCustomerTokenValidation;
    }

    private void handleMerchantAccountValidationResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        boolean isValid = ev.getArgument(2, boolean.class);

        if (!isValid) {
            Exception exception = new Exception("Customer Token Validation Failed");
            correlations.get(correlationId).completeExceptionally(exception);
        } else {
            correlations.get(correlationId).complete(isValid);
        }
    }

    public void validateMerchantAccount(UUID merchantId, CorrelationId paymentCorrelationId) {
        CorrelationId merchantValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<Boolean> futureMerchantValidation = new CompletableFuture<>();
        correlations.put(merchantValidationCorrelationId, futureMerchantValidation);

        Event merchantAccountValidationEvent = new Event(VALIDATE_MERCHANT_ACCOUNT_REQUESTED,
                new Object[] { merchantId, merchantValidationCorrelationId });
        queue.publish(merchantAccountValidationEvent);

        futureMerchantValidation.whenComplete((isAccountValid, throwable) -> {
            if (throwable != null || isAccountValid == null) {
                publishPaymentExceptionally(paymentCorrelationId, false, throwable.getMessage());
            }
        });
    }

    private void handleUseTokenResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        boolean tokenUsedSuccessfully = ev.getArgument(1, boolean.class);

        if (!tokenUsedSuccessfully) {
            Exception exception = new Exception("Using Customer Token Failed");
            correlations.get(correlationId).completeExceptionally(exception);
        } else {
            correlations.get(correlationId).complete(tokenUsedSuccessfully);
        }

    }

    private void markTokenAsUsed(UUID customerToken, CorrelationId paymentCorrelationId) {
        CorrelationId useTokenCorrelationId = CorrelationId.randomId();
        CompletableFuture<Boolean> futureUseToken = new CompletableFuture<>();
        correlations.put(useTokenCorrelationId, futureUseToken);

        Event useTokenEvent = new Event(USE_TOKEN_REQUEST, new Object[] { useTokenCorrelationId, customerToken });
        queue.publish(useTokenEvent);

        futureUseToken.whenComplete((isTokenUsed, throwable) -> {
            if (throwable != null || !isTokenUsed) {
                publishPaymentExceptionally(paymentCorrelationId, false, throwable.getMessage());
            }
        });
    }

    public void handlePaymentRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        UUID customerToken = ev.getArgument(1, UUID.class);
        UUID merchantId = ev.getArgument(2, UUID.class);
        double amount = ev.getArgument(3, double.class);

        Payment payment = new Payment(customerToken, merchantId, amount);

        // Merchant account validation
        validateMerchantAccount(merchantId, correlationId);

        // Customer Token Validation, saves customerId -> PaymentId in repository
        CompletableFuture<UUID> futureCustomerTokenValidation = validateCustomerToken(customerToken, payment.getId(), correlationId);

        // Mark token as used
        markTokenAsUsed(customerToken, correlationId);

        futureCustomerTokenValidation.thenAccept(responseCustomerId -> {
            paymentRepository.addCustomerPayment(responseCustomerId, payment.getId());
        });

        // Save customer and merchant payment info
        paymentRepository.addMerchantPayment(merchantId, payment.getId());
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
