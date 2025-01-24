
package dtu.dtuPay.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.ReportingEventMessage;
import dtu.dtuPay.models.TokenEventMessage;
import dtu.dtuPay.models.dtos.CreateCustomerDto;
import dtu.dtuPay.models.dtos.TokenRequestDto;
import dtu.dtuPay.services.CustomerService;
import dtu.dtuPay.services.ReportingService;
import dtu.dtuPay.services.TokenService;

import java.util.UUID;

@Path("customers")
public class CustomerResource {
    CustomerService service = CustomerService.getService();
    ReportingService reportingService = ReportingService.getInstance();
    TokenService tokenService = TokenService.getInstance();

    /**
     * @author Mahdi El Dirani s233031
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomer(CreateCustomerDto customerRequest) {
            AccountEventMessage eventMessage = service.createCustomer(customerRequest);

            if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(eventMessage.getExceptionMessage())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return Response.status(Response.Status.OK)
                    .entity(eventMessage.getCustomerId())
                    .build();
    }

    /**
     * @author Mahdi El Dirani s233031
     */
    @DELETE
    @Path("/deregister/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("customerId") UUID id) {
        AccountEventMessage eventMessage = service.deregisterCustomer(id);

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

    /**
     * @author Mihai Munteanu s242996
     */
    @GET
    @Path("/reports/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@PathParam("customerId") UUID customerId) {
        ReportingEventMessage eventMessage = reportingService.getAllCustomerPayments(customerId);

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

    /**
     * @author Ugne Adamonyte s194705
     */
    @POST
    @Path("/tokens/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTokens(TokenRequestDto tokenRequestDto) {
        TokenEventMessage eventMessage = tokenService.createTokens(tokenRequestDto.getCustomerId(), tokenRequestDto.getNTokens());

        if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(eventMessage.getExceptionMessage())
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(eventMessage.getCreatedTokens())
                .build();
    }

    /**
     * @author Ugne Adamonyte s194705
     */
    @GET
    @Path("/tokens/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokens(@PathParam("customerId") UUID customerId) {
        TokenEventMessage eventMessage = tokenService.getTokens(customerId);

        if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(eventMessage.getExceptionMessage())
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(eventMessage.getTokenList())
                .build();
    }

}
