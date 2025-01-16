package services;

//import Exceptions.UserException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
//import models.Customer;
import models.dtos.UserRequestDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import services.interfaces.ICustomerServiceClient;

import java.text.ParseException;
import java.util.UUID;
public class CustomerService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
    ICustomerServiceClient service = baseURL.proxy(ICustomerServiceClient.class);

    public UUID createCustomer(UserRequestDto user){
        Response response = service.postCustomer(user);

        return response.readEntity(UUID.class);
    }

//    public Customer getCustomerById(UUID id) throws UserException {
//        Response response = service.getCustomerById(id);
//
//        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()){
//            throw new UserException(response.readEntity(String.class));
//        }
//        return response.readEntity(Customer.class);
//    }



}
