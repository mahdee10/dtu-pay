package dtu.dtuPay.services.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dtu.dtuPay.dtos.UserRequestDto;

import java.util.UUID;

@Path("customers")
public interface ICustomerServiceClient {

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomer(UserRequestDto user);

    @DELETE
    @Path("/deregister/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unregisterCustomer(@PathParam("customerId") UUID customerId);
}
