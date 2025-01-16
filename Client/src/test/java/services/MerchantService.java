package services;

//import Exceptions.UserException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
//import models.Customer;
import models.dtos.UserRequestDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import services.interfaces.ICustomerServiceClient;
import services.interfaces.IMerchantServiceClient;

import java.text.ParseException;
import java.util.UUID;
public class MerchantService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
        IMerchantServiceClient service = baseURL.proxy(IMerchantServiceClient.class);

    public UUID createMerchant(UserRequestDto user){
        Response response = service.postMerchant(user);

        return response.readEntity(UUID.class);
    }





}
