package services;

import messaging.Event;
import messaging.MessageQueue;
import models.AccountEventMessage;
import models.CorrelationId;
import models.Merchant;
import repositories.MerchantRepository;

public class MerchantService {
    private static final String Merchant_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    private static final String Merchant_CREATED = "MerchantCreated";
    private static final String Merchant_DEREGISTRATION_REQUESTED = "MerchantDeregistrationRequested";
    private static final String Merchant_DEREGISTERED = "MerchantDeregistered";
    private static final String VALIDATE_Merchant_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATION_RESPONSE = "MerchantAccountValidationResponse";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    MessageQueue queue;
    MerchantRepository merchantRepository = MerchantRepository.getInstance();


    public MerchantService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(Merchant_REGISTRATION_REQUESTED, this::handleMerchantRegistrationRequested);
        this.queue.addHandler(Merchant_DEREGISTRATION_REQUESTED, this::handleMerchantDeregistrationRequested);
        this.queue.addHandler(VALIDATE_Merchant_ACCOUNT_REQUESTED, this::handleValidateMerchantAccountRequested);
    }

    public void handleMerchantRegistrationRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        Merchant merchant=
                new Merchant(
                        eventMessage.getFirstName(),
                        eventMessage.getLastName(),
                        eventMessage.getCpr(),
                        eventMessage.getBankAccount());
        merchantRepository.addMerchant(merchant);

        System.out.println("I created "+merchant.getFirstName());

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setMerchantId(merchant.getId());

        Event event = new Event(Merchant_CREATED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleMerchantDeregistrationRequested(Event ev) {
        CorrelationId correlationId=ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        boolean isDeleted = merchantRepository.removeMerchant(eventMessage.getMerchantId());
        System.out.println(isDeleted);

        eventMessage.setIsAccountDeleted(isDeleted);
        eventMessage.setRequestResponseCode(OK);

        Event event = new Event(Merchant_DEREGISTERED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleValidateMerchantAccountRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        Merchant merchant = merchantRepository.getMerchant(eventMessage.getMerchantId());
        boolean isValid = merchant != null;

        eventMessage.setBankAccount(isValid ? merchant.getBankAccountId() : null);
        eventMessage.setIsValidAccount(isValid);
        eventMessage.setRequestResponseCode(isValid ? OK : BAD_REQUEST);
        eventMessage.setExceptionMessage(isValid ? null : "Merchant account does not exist.");

        Event event = new Event(MERCHANT_ACCOUNT_VALIDATION_RESPONSE, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }
}
