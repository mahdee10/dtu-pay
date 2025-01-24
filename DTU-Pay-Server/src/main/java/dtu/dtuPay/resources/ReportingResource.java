package dtu.dtuPay.resources;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dtu.dtuPay.models.ReportingEventMessage;
import dtu.dtuPay.services.ReportingService;

@Path("reports")
public class ReportingResource {
    ReportingService service = ReportingService.getInstance();

    /**
     * @author Mihai Munteanu s242996
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPayments() {
        ReportingEventMessage eventMessage = service.getAllPayments();

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

}
