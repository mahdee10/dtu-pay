package org.example.services;

import io.cucumber.java.es.E;
import messaging.Event;
import messaging.MessageQueue;
import org.example.models.CorrelationId;
import org.example.models.Token;
import org.example.repositories.TokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {

    private static final String TOKEN_VALIDATION_REQUESTED = "TokenValidationRequest";
    private static final String TOKEN_VALIDATION_RETURNED = "TokenValidationReturned";
    private static final String CUSTOMER_TOKENS_REQUESTED = "CustomerTokensRequest";
    private static final String CUSTOMER_TOKENS_RETURNED = "CustomerTokensReturned";
    private static final String REQUEST_TOKENS_EVENT = "RequestTokensEvent";
    private static final String REQUEST_TOKENS_RESPONSE = "RequestTokensResponse";
    private static final String USE_TOKEN_REQUEST = "UseTokenRequest";
    private static final String USE_TOKEN_RESPONSE = "UseTokenResponse";

    MessageQueue queue;
    TokenRepository tokenRepository = TokenRepository.getInstance();

    public TokenService(MessageQueue queue) {
        this.queue = queue;
        this.queue.addHandler(TOKEN_VALIDATION_REQUESTED, this::handleTokenValidationRequest);
        this.queue.addHandler(CUSTOMER_TOKENS_REQUESTED, this::handleCustomerTokenRequest);
        this.queue.addHandler(REQUEST_TOKENS_EVENT, this::handleRequestTokensEvent);
        this.queue.addHandler(USE_TOKEN_REQUEST, this::handleUseTokenRequest);
    }

    public void handleTokenValidationRequest(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        UUID tokenUUID = e.getArgument(1, UUID.class);
        boolean isValid = tokenRepository.getAllTokens()
                .stream()
                .anyMatch(token -> token.getUuid().equals(tokenUUID) && token.isValid());

        if (!isValid) {
            boolean exists = tokenRepository.getAllTokens().stream().anyMatch(token -> token.getUuid().equals(tokenUUID));
            if (!exists) {
                Event event = new Event(TOKEN_VALIDATION_RETURNED, new Object[]{ correlationId, "Token not found."});
                queue.publish(event);
                return;
            }
        }

        UUID customerId = tokenRepository.getCustomerId(tokenUUID);
        Event event = new Event(TOKEN_VALIDATION_RETURNED, new Object[] { correlationId, isValid, customerId });
        queue.publish(event);
    }

    public void handleCustomerTokenRequest(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
    	 UUID uuid = e.getArgument(1, UUID.class);
    	 Token token = tokenRepository.getTokens(uuid).stream().findAny().orElse(null);
    	 if(token == null) {
    		 Event event = new Event(CUSTOMER_TOKENS_RETURNED, new Object[] {
                     correlationId, "You have no more tokens. Request more tokens."
             });
             queue.publish(event);
             return;
         }
    		 
         Event event = new Event(CUSTOMER_TOKENS_RETURNED, new Object[] { correlationId, token.getUuid() });
         queue.publish(event);
    }

    public void handleRequestTokensEvent(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
    	UUID uuid = e.getArgument(1, UUID.class);
    	Integer requestedTokens = e.getArgument(1, Integer.class);
    	List<Token> tokenList = tokenRepository.getTokens(uuid);
    	
    	if(requestedTokens <= 5) {
    		
    		if(tokenList.size() <= 1) {
    			List<Token> newTokenList = new ArrayList<>();
    			for (int i = 0; i < requestedTokens; i++) {
    				Token newToken = new Token(UUID.randomUUID(), true);
    				  newTokenList.add(newToken);	  
    				}
    			tokenRepository.addTokens(uuid, newTokenList);
    			Event event = new Event(REQUEST_TOKENS_RESPONSE, new Object[] { correlationId, newTokenList.size() });
                queue.publish(event);
                return;
    		}
    		
    		Event event = new Event(REQUEST_TOKENS_RESPONSE, new Object[] { correlationId, "Too many active tokens" });
            queue.publish(event);
            return;
    		
    	}

        Event event = new Event(REQUEST_TOKENS_RESPONSE, new Object[] { correlationId, "Too many tokens requested"});
        queue.publish(event);
    }

    public void handleUseTokenRequest(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        Event event = new Event(USE_TOKEN_RESPONSE, new Object[] { correlationId, true });
        try {
            tokenRepository.useToken(e.getArgument(1, UUID.class));
        } catch (Exception exception) {
           event = new Event(USE_TOKEN_RESPONSE, new Object[] { correlationId, exception.getMessage()});
        }
        queue.publish(event);
    }







}
