package services;

import messaging.Event;
import messaging.MessageQueue;
import models.Merchant;
import models.Merchant;
import models.dtos.MerchantDto;
import repositories.MerchantRepository;

import java.util.UUID;

public class MerchantService {
    private static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    private static final String MERCHANT_CREATED = "MerchantCreated";
    private static final String MERCHANT_DEREGISTRATION_REQUESTED = "MerchantDeregistrationRequested";
    private static final String MERCHANT_DEREGISTERED = "MerchantDeregistered";
    private static final String GET_MERCHANT_BANK_ACCOUNT_REQUESTED = "GetMerchantBankAccountRequested";
    private static final String MERCHANT_BANK_ACCOUNT_RESPONSE = "MerchantBankAccountResponse";
    private static final String VALIDATE_MERCHANT_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATION_RESPONSE = "MerchantAccountValidationResponse";

    MessageQueue queue;
    MerchantRepository merchantRepository = MerchantRepository.getInstance();

    public MerchantService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(MERCHANT_REGISTRATION_REQUESTED, this::handleMerchantRegistrationRequested);
        this.queue.addHandler(MERCHANT_DEREGISTRATION_REQUESTED, this::handleMerchantDeregistrationRequested);
        this.queue.addHandler(GET_MERCHANT_BANK_ACCOUNT_REQUESTED, this::handleGetMerchantBankAccountRequested);
        this.queue.addHandler(VALIDATE_MERCHANT_ACCOUNT_REQUESTED, this::handleValidateMerchantAccountRequested);
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

        Event event = new Event(MERCHANT_CREATED, new Object[] { merchant.getId() });
        queue.publish(event);
    }

    public void handleMerchantDeregistrationRequested(Event ev) {
        UUID merchantId = ev.getArgument(0, UUID.class);
        boolean isDeleted = merchantRepository.removeMerchant(merchantId);
        System.out.println(isDeleted);

        Event event = new Event(MERCHANT_DEREGISTERED, new Object[]{merchantId, isDeleted});
        queue.publish(event);
    }
    public void handleGetMerchantBankAccountRequested(Event ev) {
        UUID merchantId = ev.getArgument(0, UUID.class);
        Merchant merchant = merchantRepository.getMerchant(merchantId);
        String bankAccountId = merchant != null ? merchant.getBankAccountId() : null;

        Event event = new Event(MERCHANT_BANK_ACCOUNT_RESPONSE, new Object[]{merchantId, bankAccountId});
        queue.publish(event);
    }

    public void handleValidateMerchantAccountRequested(Event ev) {
        UUID merchantId = ev.getArgument(0, UUID.class);
        boolean isValid = merchantRepository.getMerchant(merchantId) != null;

        Event event = new Event(MERCHANT_ACCOUNT_VALIDATION_RESPONSE, new Object[]{merchantId, isValid});
        queue.publish(event);
    }
}
