package org.example.services;

import messaging.Event;
import messaging.MessageQueue;
import org.example.models.Token;
import org.example.repositories.TokenRepository;

import java.util.List;
import java.util.UUID;

public class TokenService {

    MessageQueue queue;
    TokenRepository tokenRepository = TokenRepository.getInstance();

    public TokenService(MessageQueue queue) {
        this.queue = queue;
        this.queue.addHandler("TokenValidationRequest", this::handleTokenValidationRequest);
        this.queue.addHandler("CustomerTokensRequest", this::handleCustomerTokenRequest);
        this.queue.addHandler("RequestTokensEvent", this::handleRequestTokensEvent);
        this.queue.addHandler("UseTokenRequest", this::handleUseTokenRequest);

    }

    public void handleTokenValidationRequest(Event e) {
        UUID uuid = e.getArgument(0, UUID.class);
        boolean isValid = tokenRepository.getAllTokens().stream().anyMatch(token -> token.getUuid().equals(uuid) && token.isValid());

        if (!isValid) {
            boolean exists = tokenRepository.getAllTokens().stream().anyMatch(token -> token.getUuid().equals(uuid));
            if (!exists) {
                Event event = new Event("TokenValidationReturned", new Object[] {new Exception("Token not found.")});
                queue.publish(event);
                return;
            }
        }

        Event event = new Event("TokenValidationReturned", new Object[] {isValid});
        queue.publish(event);
    }

    public void handleCustomerTokenRequest(Event e) {
    	 UUID uuid = e.getArgument(0, UUID.class);
    	 Token token = tokenRepository.getTokens(uuid).stream().findAny().orElse(null);
    	 if(token == null) {
    		 Event event = new Event("CustomerTokensReturned", new Object[] {new Exception("You have no more tokens. Request more tokens.")});
             queue.publish(event);
             return;
             }
    		 
         Event event = new Event("CustomerTokensReturned", new Object[] {token.getUuid()});
         queue.publish(event);
    }

    public void handleRequestTokensEvent(Event e) {
        //logic
        Event event = new Event("RequestTokensResponse", new Object[] {});
        queue.publish(event);
    }

    public void handleUseTokenRequest(Event e) {
        //logic
        Event event = new Event("UseTokenResponse", new Object[] {});
        queue.publish(event);
    }







}
