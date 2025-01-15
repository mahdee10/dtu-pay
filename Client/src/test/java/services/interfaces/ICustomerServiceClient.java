package services.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.dtos.UserRequestDto;

import java.util.UUID;

@Path("customer")
public interface ICustomerServiceClient {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCustomer(UserRequestDto user);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") UUID id);


}
