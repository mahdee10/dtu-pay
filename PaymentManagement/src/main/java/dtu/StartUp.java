package dtu;

import dtu.dtuPay.services.BankServiceImplementation;
import dtu.dtuPay.services.PaymentService;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        String environment = System.getenv("Environment");
        String hostname = environment != null && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";

        var mq = new RabbitMqQueue(hostname);
        BankServiceImplementation bankService = new BankServiceImplementation();
        new PaymentService(mq, bankService);
    }
}
