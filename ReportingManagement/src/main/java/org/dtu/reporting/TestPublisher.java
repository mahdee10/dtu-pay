package org.dtu.reporting;
import messaging.Event;
import messaging.implementations.RabbitMqQueue;

import java.util.UUID;

public class TestPublisher {
    public static void main(String[] args) {
        RabbitMqQueue messageQueue = new RabbitMqQueue("localhost");

        // Create a test payment
        UUID paymentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        double amount = 100.00;

        Event event = new Event("PaymentsFetched", new Object[]{
                "[{\"id\":\"" + paymentId + "\",\"customerToken\":\"" + customerId + "\",\"merchantId\":\"" + merchantId + "\",\"amount\":" + amount + "}]"
        });

        messageQueue.publish(event);
        System.out.println("Test event published to RabbitMQ.");
    }
}
