package org.example.repositories;

import org.example.models.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

// Create Repository class with singleton
public class TokenRepository {
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

    public void useToken(UUID tokenUUID) {
        boolean isValid = getAllTokens().stream().anyMatch(t -> t.getUuid().equals(tokenUUID));
        if(!isValid) throw new RuntimeException("Invalid token");

        AtomicReference<UUID> userUUID = new AtomicReference<>();
        AtomicReference<Token> tokenToUse = new AtomicReference<>();

        for (HashMap.Entry<UUID, List<Token>> entry : validTokens.entrySet()) {
            UUID uuid = entry.getKey();
            List<Token> tokens = entry.getValue();

            Token token = tokens.stream().filter(t -> t.getUuid().equals(tokenUUID)).findFirst().orElse(null);
            if (token != null) {
                userUUID.set(uuid);
                tokenToUse.set(token);
                break;
            }
        }
        if (tokenToUse.get() == null) throw new RuntimeException("Token not found");

        List<Token> validTokenList = validTokens.get(userUUID.get());
        List<Token> invalidTokenList = invalidTokens.get(userUUID.get());

        if (validTokenList != null) {
            validTokenList.remove(tokenToUse.get());
            invalidTokenList.add(tokenToUse.get());
        }

        validTokens.put(userUUID.get(), validTokenList);
        invalidTokens.put(userUUID.get(), invalidTokenList);
    }

    public void addTokens(UUID userUUID, List<Token> tokens) {
        if (validTokens.containsKey(userUUID)) {
            validTokens.get(userUUID).addAll(tokens);
        }
        else {
            validTokens.put(userUUID, tokens);
        }
    }

    public List<Token> getTokens(UUID userUUID) {
        if (validTokens.containsKey(userUUID)) {
            return validTokens.get(userUUID);
        }
        return new ArrayList<>();
    }

    public List<Token> getAllTokens() {
        List<Token> tokens = new ArrayList<>();
        validTokens.forEach((key, value) -> tokens.addAll(value));
        return tokens;
    }
}