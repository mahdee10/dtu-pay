package services;

import java.util.UUID;

public record CorrelationId(UUID id) {

    public static CorrelationId randomId() {
        return new CorrelationId(UUID.randomUUID());
    }
}


