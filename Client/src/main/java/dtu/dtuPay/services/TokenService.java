package dtu.dtuPay.services;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import dtu.dtuPay.dtos.TokenRequestDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import dtu.dtuPay.services.interfaces.ITokenService;

import java.util.List;
import java.util.UUID;

/**
 * @author Luis Freire s233483
 */
public class TokenService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
    ITokenService service = baseURL.proxy(ITokenService.class);

    public Integer createTokens(TokenRequestDto tokenRequestDto) throws Exception {
        Response response = service.createTokens(tokenRequestDto);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(Integer.class);
    }

    public List<UUID> getTokens(UUID customerId) throws Exception {
        Response response = service.getTokens(customerId);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<List<UUID>>(){});
    }
}
