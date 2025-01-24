package dtu.dtuPay.services.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface IReportingService {

    @GET
    @Path("reports")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPayments();

    @GET
    @Path("customers/reports/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerPayments(@PathParam("customerId") UUID customerId);

    @GET
    @Path("merchants/reports/{merchantId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMerchantPayments(@PathParam("merchantId") UUID merchantId);
}
