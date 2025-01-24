package dtu.dtuPay.services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.TokenEventMessage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {
    private static final String CREATE_TOKENS_REQUESTED = "CreateTokensRequested";
    private static final String RESPONSE_TOKENS_CREATED = "ResponseTokensCreated";
    private static final String GET_TOKENS_REQUESTED = "GetTokensRequested";
    private static final String RESPONSE_GET_TOKENS_RETURNED = "ResponseGetTokensReturned";
    private static final String USE_TOKEN_REQUESTED = "UseTokenRequested";
    private static final String RESPONSE_TOKEN_USED = "ResponseTokenUsed";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    static TokenService service = null;
    static CustomerService customerService = CustomerService.getService();

    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<TokenEventMessage>> correlations = new ConcurrentHashMap<>();

    public static synchronized TokenService getInstance() {
        if (service != null) {
            return service;
        }

        String environment = System.getenv("Environment");
        String hostname = environment != null && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        service = new TokenService(mq);
        return service;
    }

    public TokenService(MessageQueue q) {
        queue = q;
        queue.addHandler(RESPONSE_TOKENS_CREATED, this::handleRequestTokensResponse);
        queue.addHandler(RESPONSE_GET_TOKENS_RETURNED, this::handleCustomerTokensReturned);
        queue.addHandler(RESPONSE_TOKEN_USED, this::handleUseTokenResponse);
    }

    private void handleCustomerTokensReturned(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public TokenEventMessage getTokens(UUID customerId) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<TokenEventMessage> futureGetTokenCompleted = new CompletableFuture<>();
        correlations.put(correlationId, futureGetTokenCompleted);

        TokenEventMessage eventMessage = new TokenEventMessage();
        eventMessage.setCustomerId(customerId);

        Event event = new Event(GET_TOKENS_REQUESTED, new Object[] { correlationId, eventMessage });
        queue.publish(event);

        return futureGetTokenCompleted.join();
    }

    private void handleRequestTokensResponse(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public TokenEventMessage createTokens(UUID customerId, int nTokens) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<TokenEventMessage> futureCreateTokensCompleted = new CompletableFuture<>();
        correlations.put(correlationId, futureCreateTokensCompleted);

        TokenEventMessage eventMessage = new TokenEventMessage();
        eventMessage.setRequestedTokens(nTokens);
        eventMessage.setCustomerId(customerId);

        Event event = new Event(CREATE_TOKENS_REQUESTED, new Object[] { correlationId, eventMessage });
        queue.publish(event);

        return futureCreateTokensCompleted.join();
    }

    private void handleUseTokenResponse(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = ev.getArgument(1, TokenEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public TokenEventMessage useToken(UUID customerToken) {
        CorrelationId useTokenCorrelationId = CorrelationId.randomId();
        CompletableFuture<TokenEventMessage> futureUseToken = new CompletableFuture<>();
        correlations.put(useTokenCorrelationId, futureUseToken);

        TokenEventMessage tokenEventMessage = new TokenEventMessage();
        tokenEventMessage.setTokenUUID(customerToken);

        Event useTokenEvent = new Event(USE_TOKEN_REQUESTED, new Object[] { useTokenCorrelationId, tokenEventMessage });
        queue.publish(useTokenEvent);

        TokenEventMessage responseEventMessage =  futureUseToken.join();
        if (responseEventMessage.getRequestResponseCode() != 200) {
            responseEventMessage.setExceptionMessage(responseEventMessage.getExceptionMessage());
            responseEventMessage.setRequestResponseCode(BAD_REQUEST);

            return responseEventMessage;
        }

        AccountEventMessage accountMessageEvent = customerService.validateCustomerAccount(responseEventMessage.getCustomerId());
        if (!accountMessageEvent.getIsValidAccount() || accountMessageEvent.getRequestResponseCode() != 200) {
            responseEventMessage.setExceptionMessage(accountMessageEvent.getExceptionMessage());
            responseEventMessage.setRequestResponseCode(BAD_REQUEST);

            return responseEventMessage;
        }

        responseEventMessage.setBankAccount(accountMessageEvent.getBankAccount());
        responseEventMessage.setRequestResponseCode(OK);
        return responseEventMessage;

    }


}
