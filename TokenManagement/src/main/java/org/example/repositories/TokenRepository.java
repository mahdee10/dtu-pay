package org.example.repositories;

import org.example.models.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

// Create Repository class with singleton
class TokenRepository {
    private static TokenRepository instance;
    private HashMap<UUID, List<Token>> validTokens;
    private HashMap<UUID, List<Token>> invalidTokens;

    private TokenRepository() {
        this.invalidTokens = new HashMap<>();
        this.validTokens = new HashMap<>();
    }

    public static TokenRepository getInstance() {
        if (instance == null) {
            synchronized (TokenRepository.class) {
                if (instance == null) {
                    instance = new TokenRepository();
                }
            }
        }
        return instance;
    }

    public void useToken(UUID userUUID, UUID tokenUUID) {
        Token tokenToUse = null;
        if (validTokens.containsKey(userUUID)) {
            List<Token> tokens = validTokens.get(userUUID);
            if (!tokens.isEmpty()) {
                tokenToUse = tokens.stream()
                        .filter(token -> token.getUuid().equals(tokenUUID))
                        .findFirst().orElse(null);
                if (tokenToUse != null) {
                    validTokens.get(userUUID).remove(tokenToUse);
                }
            }
        }

        if (invalidTokens.containsKey(userUUID)) {
            if (tokenToUse != null) {
                invalidTokens.get(userUUID).add(tokenToUse);
            }
        }
    }

    public void addTokens(UUID userUUID, List<Token> tokens) {
        if (validTokens.containsKey(userUUID)) {
            validTokens.put(userUUID, tokens);
        }
    }

    public List<Token> getTokens(UUID userUUID) {
        if (validTokens.containsKey(userUUID)) {
            return validTokens.get(userUUID);
        }
        return new ArrayList<>();
    }
}