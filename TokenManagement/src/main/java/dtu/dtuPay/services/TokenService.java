package dtu.dtuPay.services;

import messaging.Event;
import messaging.MessageQueue;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.Token;
import dtu.dtuPay.models.TokenEventMessage;
import dtu.dtuPay.repositories.TokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenService {

    private static final String CREATE_TOKENS_REQUESTED = "CreateTokensRequested";
    private static final String RESPONSE_TOKENS_CREATED = "ResponseTokensCreated";
    private static final String GET_TOKENS_REQUESTED = "GetTokensRequested";
    private static final String RESPONSE_GET_TOKENS_RETURNED = "ResponseGetTokensReturned";
    private static final String USE_TOKEN_REQUESTED = "UseTokenRequested";
    private static final String RESPONSE_TOKEN_USED = "ResponseTokenUsed";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    MessageQueue queue;
    TokenRepository tokenRepository = TokenRepository.getInstance();

    public TokenService(MessageQueue queue) {
        this.queue = queue;
        this.queue.addHandler(GET_TOKENS_REQUESTED, this::handleGetTokensRequested);
        this.queue.addHandler(CREATE_TOKENS_REQUESTED, this::handleCreateTokensRequested);
        this.queue.addHandler(USE_TOKEN_REQUESTED, this::handleUseTokenRequest);
    }

    public void handleGetTokensRequested(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);
    	UUID uuid = eventMessage.getCustomerId();
    	List<Token> tokens = tokenRepository.getTokens(uuid);
    	if(tokens.isEmpty()) {
            eventMessage.setRequestResponseCode(BAD_REQUEST);
            eventMessage.setExceptionMessage("You have no more tokens. Request more tokens.");
            Event event = new Event(RESPONSE_GET_TOKENS_RETURNED, new Object[] {
                    correlationId, eventMessage
            });
            queue.publish(event);
            return;
        }

        eventMessage.setTokenList(tokens.stream().map((Token::getUuid)).toList());
        eventMessage.setRequestResponseCode(OK);
        Event event = new Event(RESPONSE_GET_TOKENS_RETURNED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleCreateTokensRequested(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);
    	UUID uuid = eventMessage.getCustomerId();
    	Integer requestedTokens = eventMessage.getRequestedTokens();
    	List<Token> tokenList = tokenRepository.getTokens(uuid);

    	if(requestedTokens <= 5) {

    		if(tokenList.size() <= 1) {
    			List<Token> newTokenList = new ArrayList<>();
    			for (int i = 0; i <= requestedTokens; i++) {
    				Token newToken = new Token(UUID.randomUUID(), true);
    				  newTokenList.add(newToken);
                }

    			tokenRepository.addTokens(uuid, newTokenList);
                eventMessage.setRequestResponseCode(OK);
                eventMessage.setCreatedTokens(newTokenList.size());
    			Event event = new Event(RESPONSE_TOKENS_CREATED, new Object[] { correlationId, eventMessage });
                queue.publish(event);
                return;
    		}

            eventMessage.setRequestResponseCode(BAD_REQUEST);
            eventMessage.setExceptionMessage("Too many active tokens");
    		Event event = new Event(RESPONSE_TOKENS_CREATED, new Object[] { correlationId, eventMessage });
            queue.publish(event);
            return;

    	}

        eventMessage.setRequestResponseCode(BAD_REQUEST);
        eventMessage.setExceptionMessage("Too many tokens requested");
        Event event = new Event(RESPONSE_TOKENS_CREATED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleUseTokenRequest(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        TokenEventMessage eventMessage = e.getArgument(1, TokenEventMessage.class);

        try {
            UUID customerId = tokenRepository.getCustomerId(eventMessage.getTokenUUID());
            eventMessage.setCustomerId(customerId);
            tokenRepository.useToken(eventMessage.getTokenUUID());
        } catch (Exception ex) {
            eventMessage.setRequestResponseCode(BAD_REQUEST);
            eventMessage.setExceptionMessage(ex.getMessage());
            eventMessage.setIsValid(false);
            Event event = new Event(RESPONSE_TOKEN_USED, new Object[] { correlationId, eventMessage });
            queue.publish(event);
            return;
        }

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setCustomerId(eventMessage.getCustomerId());
        eventMessage.setIsValid(true);
        Event event = new Event(RESPONSE_TOKEN_USED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }







}
