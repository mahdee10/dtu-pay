package models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Data
public class TokenEventMessage {
    private UUID customerId;
    private UUID tokenUUID;
    private List<UUID> tokenList;
    private Integer requestedTokens;
    private Integer createdTokens;
    private Boolean isValid;
    private Boolean isTokenUsed;
    private int requestResponseCode;
    private String exceptionMessage;

    public TokenEventMessage() {}
}
