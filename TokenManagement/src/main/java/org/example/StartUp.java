package org.example;

import messaging.implementations.RabbitMqQueue;
import org.example.services.TokenService;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        var mq = new RabbitMqQueue("localhost");
        new TokenService(mq);
    }
}
