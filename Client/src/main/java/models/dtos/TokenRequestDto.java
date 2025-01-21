package models.dtos;

import java.util.UUID;

public class TokenRequestDto {

    private UUID customerId;
    private Integer nTokens;

    public TokenRequestDto() {}

    public TokenRequestDto(UUID customerId, Integer nTokens) {
        this.customerId = customerId;
        this.nTokens = nTokens;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Integer getNTokens() {
        return nTokens;
    }

    public void setNTokens(Integer nTokens) {
        this.nTokens = nTokens;
    }
}
