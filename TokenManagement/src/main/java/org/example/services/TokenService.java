package org.example.services;

import messaging.Event;
import messaging.MessageQueue;

public class TokenService {

    MessageQueue queue;

    public TokenService(MessageQueue queue) {
        this.queue = queue;
        this.queue.addHandler("TokenValidationRequest", this::handleTokenValidationRequest);
        this.queue.addHandler("CustomerTokensRequest", this::handleCustomerTokenRequest);
        this.queue.addHandler("RequestTokensEvent", this::handleRequestTokensEvent);
        this.queue.addHandler("UseTokenRequest", this::handleUseTokenRequest);

    }

    public void handleTokenValidationRequest(Event e) {
        //logic
        Event event = new Event("TokenValidationReturned", new Object[] {});
        queue.publish(event);
    }

    public void handleCustomerTokenRequest(Event e) {
        //logic
        Event event = new Event("CustomerTokensReturned", new Object[] {});
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
