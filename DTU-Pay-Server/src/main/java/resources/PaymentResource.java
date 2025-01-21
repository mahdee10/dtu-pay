package resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.PaymentEventMessage;
import models.dtos.PaymentRequestDto;
import services.PaymentService;


@Path("payments")
public class PaymentResource {
    PaymentService service = PaymentService.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response paymentRequest(PaymentRequestDto paymentRequest) {
        PaymentEventMessage eventMessage = service.pay(paymentRequest);

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
