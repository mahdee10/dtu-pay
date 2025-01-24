package dtu.dtuPay.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Data
public class AccountEventMessage {

    private UUID merchantId;
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String cpr;
    private String bankAccount;
    private Boolean isValidAccount;
    private int requestResponseCode;
    private String exceptionMessage;
    private Boolean isAccountDeleted;
    
    public AccountEventMessage() {}
}
