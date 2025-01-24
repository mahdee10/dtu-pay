/**
 * @author Hussein Dirani s223518
 */
package dtu.dtuPay.resources;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.PaymentEventMessage;
import dtu.dtuPay.models.ReportingEventMessage;
import dtu.dtuPay.models.dtos.CreateMerchantDto;
import dtu.dtuPay.models.dtos.PaymentRequestDto;
import dtu.dtuPay.services.MerchantService;
import dtu.dtuPay.services.PaymentService;
import dtu.dtuPay.services.ReportingService;


import java.util.UUID;

@Path("merchants")
public class MerchantResource {
    MerchantService service = MerchantService.getService();
    PaymentService paymentService = PaymentService.getInstance();
    ReportingService reportingService = ReportingService.getInstance();

    @POST
    @Path("/register")
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
    @Path("/deregister/{merchantId}")
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
    @Path("/reports/{merchantId}")
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
    @Path("/payment")
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
