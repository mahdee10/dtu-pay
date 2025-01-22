package resources;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.AccountEventMessage;
import models.PaymentEventMessage;
import models.ReportingEventMessage;
import models.dtos.CreateMerchantDto;
import models.dtos.PaymentRequestDto;
import services.MerchantService;
import services.PaymentService;
import services.ReportingService;


import java.util.UUID;

@Path("merchants")
public class MerchantResource {
    MerchantService service = MerchantService.getService();
    PaymentService paymentService = PaymentService.getInstance();
    ReportingService reportingService = ReportingService.getInstance();

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
    
    @GET
    @Path("reports/{merchantId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMerchantPayments(@PathParam("merchantId") UUID merchantId) {
        ReportingEventMessage eventMessage = reportingService.getAllMerchantPayments(merchantId);

        if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(eventMessage.getExceptionMessage())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(eventMessage.getPaymentList())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response paymentRequest(PaymentRequestDto paymentRequest) {
        PaymentEventMessage eventMessage = paymentService.pay(paymentRequest);

        if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(eventMessage.getExceptionMessage())
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(true)
                .build();
    }
}
