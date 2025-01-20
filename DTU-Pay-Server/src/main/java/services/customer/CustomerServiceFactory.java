package services.customer;

import messaging.implementations.RabbitMqQueue;

public class CustomerServiceFactory {
    static CustomerService service = null;

    public synchronized CustomerService getService() {
        if (service != null) {
            return service;
        }

        var mq = new RabbitMqQueue("localhost");
        service = new CustomerService(mq);
        return service;
    }
}
