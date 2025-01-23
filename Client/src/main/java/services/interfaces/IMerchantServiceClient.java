package services.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.dtos.UserRequestDto;

import java.util.UUID;

@Path("merchants")
public interface IMerchantServiceClient {

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postMerchant(UserRequestDto user);

    @DELETE
    @Path("/deregister/{merchantId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unregisterMerchant(@PathParam("merchantId") UUID merchantId);
}
