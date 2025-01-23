package services;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import models.dtos.UserRequestDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import services.interfaces.ICustomerServiceClient;

import java.util.UUID;

public class CustomerService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
    ICustomerServiceClient service = baseURL.proxy(ICustomerServiceClient.class);

    public UUID createCustomer(UserRequestDto user) throws Exception {
        Response response = service.registerCustomer(user);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(UUID.class);
    }

    public boolean unregisterCustomer(UUID customerId) throws Exception {
        Response response = service.unregisterCustomer(customerId);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(Boolean.class);
    }

}
