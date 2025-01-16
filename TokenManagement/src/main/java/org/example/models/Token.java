package org.example.models;

import java.util.UUID;

public class Token {
    private final UUID uuid;
    private boolean valid;

    public Token(UUID uuid, boolean valid) {
        this.uuid = uuid;
        this.valid = valid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}