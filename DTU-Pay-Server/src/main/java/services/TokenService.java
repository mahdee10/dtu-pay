package services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import models.CorrelationId;
import models.TokenEventMessage;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {
    private static final String REQUEST_TOKENS_EVENT = "RequestTokensEvent";
    private static final String REQUEST_TOKENS_RESPONSE = "RequestTokensResponse";
    private static final String CUSTOMER_TOKENS_REQUESTED = "CustomerTokensRequest";
    private static final String CUSTOMER_TOKENS_RETURNED = "CustomerTokensReturned";

    static TokenService service = null;

    private MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<TokenEventMessage>> correlations = new ConcurrentHashMap<>();

    public static synchronized TokenService getInstance() {
        if (service != null) {
            return service;
        }

        var mq = new RabbitMqQueue("localhost");
        service = new TokenService(mq);
        return service;
    }

    public TokenService(MessageQueue q) {
        queue = q;
        queue.addHandler(REQUEST_TOKENS_RESPONSE, this::handleRequestTokensResponse);
        queue.addHandler(CUSTOMER_TOKENS_RETURNED, this::handleCustomerTokensReturned);
    }

    private void handleCustomerTokensReturned(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);

        correlations.get(correlationId).complete(eventMessage);
    }

    public TokenEventMessage getToken(UUID customerId) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<TokenEventMessage> futureGetTokenCompleted = new CompletableFuture<>();
        correlations.put(correlationId, futureGetTokenCompleted);

        TokenEventMessage eventMessage = new TokenEventMessage();
        eventMessage.setCustomerId(customerId);

        Event event = new Event(CUSTOMER_TOKENS_REQUESTED, new Object[] { correlationId, eventMessage });
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

        Event event = new Event(REQUEST_TOKENS_EVENT, new Object[] { correlationId, eventMessage });
        queue.publish(event);

        return futureCreateTokensCompleted.join();
    }
}
