package services;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import models.dtos.UserRequestDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import services.interfaces.IMerchantServiceClient;

import java.util.UUID;

public class MerchantService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
    IMerchantServiceClient service = baseURL.proxy(IMerchantServiceClient.class);

    public UUID createMerchant(UserRequestDto user) throws Exception {
        Response response = service.postMerchant(user);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(UUID.class);
    }

    public boolean unregisterCustomer(UUID merchantId) throws Exception {
        Response response = service.unregisterMerchant(merchantId);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(Boolean.class);
    }

}
