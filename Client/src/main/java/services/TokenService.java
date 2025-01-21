package services;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import models.dtos.TokenRequestDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import services.interfaces.ITokenService;

import java.util.UUID;

public class TokenService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
    ITokenService service = baseURL.proxy(ITokenService.class);

    public Integer createTokens(TokenRequestDto tokenRequestDto) {
        Response response = service.createTokens(tokenRequestDto);

        return response.readEntity(Integer.class);
    }

    public UUID getToken(UUID customerId) {
        Response response = service.getToken(customerId);

        return response.readEntity(UUID.class);
    }
}
