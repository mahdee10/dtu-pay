package services;

import messaging.Event;
import messaging.MessageQueue;
import models.Merchant;
import models.dtos.MerchantDto;
import repositories.MerchantRepository;

import java.util.UUID;

public class MerchantService {
    MessageQueue queue;
    MerchantRepository merchantRepository = MerchantRepository.getInstance();

    public MerchantService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("MerchantRegistrationRequested", this::handleMerchantRegistrationRequested);
        this.queue.addHandler("MerchantDeregistrationRequested", this::handleMerchantDeregistrationRequested);
    }

    public void handleMerchantRegistrationRequested(Event ev) {
        var merchantDto = ev.getArgument(0, MerchantDto.class);
        Merchant merchant=
                new Merchant(
                        merchantDto.getFirstName(),
                        merchantDto.getLastName(),
                        merchantDto.getCpr(),
                        merchantDto.getBankAccountId());
        merchantRepository.addMerchant(merchant);

        Event event = new Event("MerchantCreated", new Object[] { merchant.getId() });
        queue.publish(event);
    }
    public void handleMerchantDeregistrationRequested(Event ev) {
        UUID merchantId = ev.getArgument(0, UUID.class);
        boolean isDeleted = merchantRepository.removeMerchant(merchantId);
        System.out.println(isDeleted);

        Event event = new Event("MerchantDeregistered", new Object[]{merchantId, isDeleted});
        queue.publish(event);
    }
}
