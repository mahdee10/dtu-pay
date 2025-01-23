package org.example;

import messaging.implementations.RabbitMqQueue;
import org.example.services.TokenService;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        String environment = System.getenv("Environment");
        String hostname = environment != null && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";
        var mq = new RabbitMqQueue(hostname);
        new TokenService(mq);
    }
}
