package resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.AccountEventMessage;
import models.ReportingEventMessage;
import models.TokenEventMessage;
import models.dtos.CreateCustomerDto;
import models.dtos.TokenRequestDto;
import services.CustomerService;
import services.ReportingService;
import services.TokenService;

import java.util.UUID;

@Path("customers")
public class CustomerResource {
    CustomerService service = CustomerService.getService();
    ReportingService reportingService = ReportingService.getInstance();
    TokenService tokenService = TokenService.getInstance();

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

    @GET
    @Path("/tokens/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokens(@PathParam("customerId") UUID customerId) {
        TokenEventMessage eventMessage = tokenService.getToken(customerId);

        if (eventMessage.getRequestResponseCode() != Response.Status.OK.getStatusCode()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(eventMessage.getExceptionMessage())
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(eventMessage.getTokenUUID())
                .build();
    }

}
