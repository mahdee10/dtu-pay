package resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.AccountEventMessage;
import models.dtos.CreateMerchantDto;
import services.MerchantService;

import java.util.UUID;

@Path("merchants")
public class MerchantResource {
    MerchantService service = MerchantService.getService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerMerchant(CreateMerchantDto merchantRequest) {
        AccountEventMessage eventMessage = service.createMerchant(merchantRequest);

        if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(eventMessage.getExceptionMessage())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(eventMessage.getMerchantId())
                .build();
    }

    @DELETE
    @Path("/{merchantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMerchant(@PathParam("merchantId") UUID id) {
        AccountEventMessage eventMessage = service.deregisterMerchant(id);

        if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(eventMessage.getExceptionMessage())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(true)
                .type(MediaType.APPLICATION_JSON)
                .build();

    }

}
