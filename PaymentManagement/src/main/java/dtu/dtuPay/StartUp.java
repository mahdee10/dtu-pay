package dtu.dtuPay;

import dtu.dtuPay.services.PaymentService;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        var mq = new RabbitMqQueue("localhost");
        new PaymentService(mq);
    }
}
