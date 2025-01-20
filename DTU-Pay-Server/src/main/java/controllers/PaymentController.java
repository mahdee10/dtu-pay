package controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.dtos.PaymentRequestDto;
import services.PaymentService;


@Path("payments")
public class PaymentController {
    PaymentService service = PaymentService.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response paymentRequest(PaymentRequestDto paymentRequest) {
        Boolean isPaymentSuccessfull = service.pay(paymentRequest);

        return Response.status(Response.Status.OK)
                .entity(isPaymentSuccessfull)
                .build();
    }
}
