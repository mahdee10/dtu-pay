package services.merchant;

import messaging.implementations.RabbitMqQueue;

public class MerchantServiceFactory {
    static MerchantService service = null;

    public synchronized MerchantService getService() {
        if (service != null) {
            return service;
        }

        var mq = new RabbitMqQueue("localhost");
        service = new MerchantService(mq);
        return service;
    }
}
