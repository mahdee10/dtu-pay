package resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.dtos.CreateMerchantDto;
import services.merchant.MerchantService;
import services.merchant.MerchantServiceFactory;

import java.util.UUID;

@Path("merchants")
public class MerchantResource {
    MerchantService service = new MerchantServiceFactory().getService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerMerchant(CreateMerchantDto merchantRequest) {
        try {
            var newMerchant = service.createMerchant(merchantRequest);

            return Response.status(Response.Status.OK)
                    .entity(newMerchant)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Merchant creation failed")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMerchant(@PathParam("merchantId") UUID id) {
        boolean isDeleted = service.deregisterMerchant(id);

        if (!isDeleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Merchant does not exist")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return Response.status(Response.Status.OK)
                .entity("Merchant deleted successfully")
                .type(MediaType.APPLICATION_JSON)
                .build();

    }

}
