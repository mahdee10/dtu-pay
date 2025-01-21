package org.dtu.reporting;

import messaging.implementations.RabbitMqQueue;
import org.dtu.reporting.services.ReportingService;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        var mq = new RabbitMqQueue("localhost");
        new ReportingService(mq);
    }
}
