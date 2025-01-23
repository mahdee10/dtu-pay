package org.example.services;

import messaging.Event;
import messaging.MessageQueue;
import org.example.models.CorrelationId;
import org.example.models.Token;
import org.example.models.TokenEventMessage;
import org.example.repositories.TokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenService {

    private static final String TOKEN_VALIDATION_REQUESTED = "TokenValidationRequest";
    private static final String TOKEN_VALIDATION_RETURNED = "TokenValidationReturned";
    private static final String CUSTOMER_TOKENS_REQUESTED = "CustomerTokensRequest";
    private static final String CUSTOMER_TOKENS_RETURNED = "CustomerTokensReturned";
    private static final String REQUEST_TOKENS_EVENT = "RequestTokensEvent";
    private static final String REQUEST_TOKENS_RESPONSE = "RequestTokensResponse";
    private static final String USE_TOKEN_REQUEST = "UseTokenRequest";
    private static final String USE_TOKEN_RESPONSE = "UseTokenResponse";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    MessageQueue queue;
    TokenRepository tokenRepository = TokenRepository.getInstance();

    public TokenService(MessageQueue queue) {
        this.queue = queue;
        this.queue.addHandler(TOKEN_VALIDATION_REQUESTED, this::handleTokenValidationRequest);
        this.queue.addHandler(CUSTOMER_TOKENS_REQUESTED, this::handleGetCustomerTokensRequest);
        this.queue.addHandler(REQUEST_TOKENS_EVENT, this::handleRequestTokensEvent);
        this.queue.addHandler(USE_TOKEN_REQUEST, this::handleUseTokenRequest);
    }

    public void handleTokenValidationRequest(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);
        UUID tokenUUID = eventMessage.getTokenUUID();
        boolean isValid = tokenRepository.getAllTokens()
                .stream()
                .anyMatch(token -> token.getUuid().equals(tokenUUID) && token.isValid());

        if (!isValid) {
            boolean exists = tokenRepository.getAllTokens().stream().anyMatch(token -> token.getUuid().equals(tokenUUID));
            if (!exists) {
                eventMessage.setRequestResponseCode(BAD_REQUEST);
                eventMessage.setExceptionMessage("Token not found.");
                Event event = new Event(TOKEN_VALIDATION_RETURNED,
                        new Object[]{ correlationId, eventMessage });
                queue.publish(event);
                return;
            }
        }

        UUID customerId = tokenRepository.getCustomerId(tokenUUID);
        eventMessage.setRequestResponseCode(OK);
        eventMessage.setCustomerId(customerId);
        eventMessage.setIsValid(isValid);
        Event event = new Event(TOKEN_VALIDATION_RETURNED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleGetCustomerTokensRequest(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);
    	UUID uuid = eventMessage.getCustomerId();
    	List<Token> tokens = tokenRepository.getTokens(uuid);
    	if(tokens.isEmpty()) {
            eventMessage.setRequestResponseCode(BAD_REQUEST);
            eventMessage.setExceptionMessage("You have no more tokens. Request more tokens.");
            Event event = new Event(CUSTOMER_TOKENS_RETURNED, new Object[] {
                    correlationId, eventMessage
            });
            queue.publish(event);
            return;
        }

        eventMessage.setTokenList(tokens.stream().map((Token::getUuid)).toList());
        eventMessage.setRequestResponseCode(OK);
        Event event = new Event(CUSTOMER_TOKENS_RETURNED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleRequestTokensEvent(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);
    	UUID uuid = eventMessage.getCustomerId();
    	Integer requestedTokens = eventMessage.getRequestedTokens();
    	List<Token> tokenList = tokenRepository.getTokens(uuid);

    	if(requestedTokens <= 5) {

    		if(tokenList.size() <= 1) {
    			List<Token> newTokenList = new ArrayList<>();
    			for (int i = 0; i < requestedTokens; i++) {
    				Token newToken = new Token(UUID.randomUUID(), true);
    				  newTokenList.add(newToken);
    				}
    			tokenRepository.addTokens(uuid, newTokenList);
                eventMessage.setRequestResponseCode(OK);
                eventMessage.setCreatedTokens(newTokenList.size());
    			Event event = new Event(REQUEST_TOKENS_RESPONSE, new Object[] { correlationId, eventMessage });
                queue.publish(event);
                return;
    		}

            eventMessage.setRequestResponseCode(BAD_REQUEST);
            eventMessage.setExceptionMessage("Too many active tokens");
    		Event event = new Event(REQUEST_TOKENS_RESPONSE, new Object[] { correlationId, eventMessage });
            queue.publish(event);
            return;

    	}

        eventMessage.setRequestResponseCode(BAD_REQUEST);
        eventMessage.setExceptionMessage("Too many tokens requested");
        Event event = new Event(REQUEST_TOKENS_RESPONSE, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleUseTokenRequest(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);

        eventMessage.setIsTokenUsed(true);
        eventMessage.setRequestResponseCode(OK);
        Event event = new Event(USE_TOKEN_RESPONSE, new Object[] { correlationId, eventMessage });
        try {
            tokenRepository.useToken(eventMessage.getTokenUUID());
        } catch (Exception exception) {
            eventMessage.setRequestResponseCode(BAD_REQUEST);
            eventMessage.setExceptionMessage(exception.getMessage());
            eventMessage.setIsTokenUsed(false);
            event = new Event(USE_TOKEN_RESPONSE, new Object[] { correlationId, eventMessage });
        }
        queue.publish(event);
    }







}
