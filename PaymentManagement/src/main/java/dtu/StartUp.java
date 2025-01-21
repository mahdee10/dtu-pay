package dtu;

import dtu.dtuPay.services.BankServiceImplementation;
import dtu.dtuPay.services.PaymentService;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        var mq = new RabbitMqQueue("localhost");
        BankServiceImplementation bankService = new BankServiceImplementation();
        new PaymentService(mq, bankService);
    }
}
