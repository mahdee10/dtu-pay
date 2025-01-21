package services;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import models.CorrelationId;

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
    private Map<CorrelationId, CompletableFuture<Integer>> createTokensCorrelations = new ConcurrentHashMap<>();
    private Map<CorrelationId, CompletableFuture<UUID>> requestTokensCorrelations = new ConcurrentHashMap<>();

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
        UUID token = e.getArgument(1, UUID.class);

        requestTokensCorrelations.get(correlationId).complete(token);
    }

    public UUID getToken(UUID customerId) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<UUID> futureGetTokenCompleted = new CompletableFuture<>();
        requestTokensCorrelations.put(correlationId, futureGetTokenCompleted);

        Event event = new Event(CUSTOMER_TOKENS_REQUESTED, new Object[] { correlationId, customerId });
        queue.publish(event);

        return futureGetTokenCompleted.join();
    }

    private void handleRequestTokensResponse(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        Integer nTokensCreated = e.getArgument(1, Integer.class);

        createTokensCorrelations.get(correlationId).complete(nTokensCreated);
    }

    public int createTokens(UUID customerId, int nTokens) {
        CorrelationId correlationId = CorrelationId.randomId();
        CompletableFuture<Integer> futureCreateTokensCompleted = new CompletableFuture<>();
        createTokensCorrelations.put(correlationId, futureCreateTokensCompleted);

        Event event = new Event(REQUEST_TOKENS_EVENT, new Object[] { correlationId, customerId, nTokens });
        queue.publish(event);

        return futureCreateTokensCompleted.join();
    }
}
