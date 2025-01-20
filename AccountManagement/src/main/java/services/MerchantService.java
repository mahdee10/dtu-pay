package services;

import messaging.Event;
import messaging.MessageQueue;
import models.CorrelationId;
import models.Merchant;
import models.dtos.MerchantDto;
import repositories.MerchantRepository;

import java.util.UUID;

public class MerchantService {
    private static final String Merchant_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    private static final String Merchant_CREATED = "MerchantCreated";
    private static final String Merchant_DEREGISTRATION_REQUESTED = "MerchantDeregistrationRequested";
    private static final String Merchant_DEREGISTERED = "MerchantDeregistered";
    private static final String GET_Merchant_BANK_ACCOUNT_REQUESTED = "GetMerchantBankAccountRequested";
    private static final String Merchant_BANK_ACCOUNT_RESPONSE = "MerchantBankAccountResponse";
    private static final String VALIDATE_Merchant_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATION_RESPONSE = "MerchantAccountValidationResponse";

    MessageQueue queue;
    MerchantRepository merchantRepository = MerchantRepository.getInstance();


    public MerchantService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(Merchant_REGISTRATION_REQUESTED, this::handleMerchantRegistrationRequested);
        this.queue.addHandler(Merchant_DEREGISTRATION_REQUESTED, this::handleMerchantDeregistrationRequested);
//        this.queue.addHandler(GET_Merchant_BANK_ACCOUNT_REQUESTED, this::handleGetMerchantBankAccountRequested);
        this.queue.addHandler(VALIDATE_Merchant_ACCOUNT_REQUESTED, this::handleValidateMerchantAccountRequested);
    }

    public void handleMerchantRegistrationRequested(Event ev) {

        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        System.out.println(correlationId);
        var merchantDto = ev.getArgument(1, MerchantDto.class);


        Merchant merchant=
                new Merchant(
                        merchantDto.getFirstName(),
                        merchantDto.getLastName(),
                        merchantDto.getCpr(),
                        merchantDto.getBankAccountId());
        merchantRepository.addMerchant(merchant);

        System.out.println("I created "+merchant.getFirstName());

        Event event = new Event(Merchant_CREATED, new Object[] { correlationId,merchant.getId() });
        queue.publish(event);
    }

    public void handleMerchantDeregistrationRequested(Event ev) {
        CorrelationId correlationId=ev.getArgument(0, CorrelationId.class);
        UUID merchantId = ev.getArgument(1, UUID.class);
        boolean isDeleted = merchantRepository.removeMerchant(merchantId);
        System.out.println(isDeleted);

        Event event = new Event(Merchant_DEREGISTERED, new Object[]{correlationId, isDeleted});
        queue.publish(event);
    }

//    public void handleGetMerchantBankAccountRequested(Event ev) {
//        UUID merchantId = ev.getArgument(0, UUID.class);
//        Merchant merchant = merchantRepository.getMerchant(merchantId);
//        String bankAccountId = merchant != null ? merchant.getBankAccountId() : null;
//
//        Event event = new Event(Merchant_BANK_ACCOUNT_RESPONSE, new Object[]{merchantId, bankAccountId});
//        queue.publish(event);
//    }

    public void handleValidateMerchantAccountRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        UUID merchantId = ev.getArgument(1, UUID.class);
        Merchant merchant = merchantRepository.getMerchant(merchantId);
        boolean isValid = merchant != null;

        String merchantAccountNumber = isValid ? merchant.getBankAccountId() : null;
        Event event = new Event(MERCHANT_ACCOUNT_VALIDATION_RESPONSE, new Object[]{ correlationId, merchantAccountNumber, isValid});
        queue.publish(event);
    }
}
