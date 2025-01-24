package dtu.dtuPay;

import messaging.implementations.RabbitMqQueue;
import dtu.dtuPay.services.ReportingService;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        var mq = new RabbitMqQueue("rabbitMq_container");
        new ReportingService(mq);
    }
}
