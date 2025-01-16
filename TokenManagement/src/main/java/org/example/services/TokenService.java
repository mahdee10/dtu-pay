package org.example.services;

import messaging.Event;
import messaging.MessageQueue;

public class TokenService {

    MessageQueue queue;

    public TokenService(MessageQueue queue) {
        this.queue = queue;
    }


}
