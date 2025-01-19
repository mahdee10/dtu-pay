package org.example.services;

import messaging.Event;
import messaging.MessageQueue;
import org.example.models.Token;
import org.example.repositories.TokenRepository;

import java.util.List;
import java.util.UUID;

public class TokenService {

    private static final String TOKEN_VALIDATION_REQUESTED = "TokenValidationRequest";
    private static final String TOKEN_VALIDATION_RETURNED = "TokenValidationReturned";
    private static final String CUSTOMER_TOKENS_REQUESTED = "CustomerTokensRequest";
    private static final String CUSTOMER_TOKENS_RETURNED = "CustomerTokensReturned";
    private static final String REQUEST_TOKENS_EVENT = "RequestTokensEvent";
    private static final String REQUEST_TOKENS_RESONSE = "RequestTokensResponse";
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
        UUID uuid = e.getArgument(0, UUID.class);
        boolean isValid = tokenRepository.getAllTokens()
                .stream()
                .anyMatch(token -> token.getUuid().equals(uuid) && token.isValid());

        if (!isValid) {
            boolean exists = tokenRepository.getAllTokens().stream().anyMatch(token -> token.getUuid().equals(uuid));
            if (!exists) {
                Event event = new Event(TOKEN_VALIDATION_RETURNED, new Object[] {new Exception("Token not found.")});
                queue.publish(event);
                return;
            }
        }

        Event event = new Event(TOKEN_VALIDATION_RETURNED, new Object[] {isValid});
        queue.publish(event);
    }

    public void handleCustomerTokenRequest(Event e) {
    	 UUID uuid = e.getArgument(0, UUID.class);
    	 Token token = tokenRepository.getTokens(uuid).stream().findAny().orElse(null);
    	 if(token == null) {
    		 Event event = new Event(CUSTOMER_TOKENS_RETURNED, new Object[] {new Exception("You have no more tokens. Request more tokens.")});
             queue.publish(event);
             return;
         }
    		 
         Event event = new Event(CUSTOMER_TOKENS_RETURNED, new Object[] {token.getUuid()});
         queue.publish(event);
    }

    public void handleRequestTokensEvent(Event e) {
        //logic
        Event event = new Event(REQUEST_TOKENS_RESONSE, new Object[] {});
        queue.publish(event);
    }

    public void handleUseTokenRequest(Event e) {
        //logic
        Event event = new Event(USE_TOKEN_RESPONSE, new Object[] {});
        queue.publish(event);
    }







}
