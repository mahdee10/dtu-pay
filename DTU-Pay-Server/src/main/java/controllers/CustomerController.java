package controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.dtos.CreateCustomerDto;
import services.customer.CustomerService;
import services.customer.CustomerServiceFactory;

import java.util.UUID;

@Path("/customer")
public class CustomerController {
    CustomerService service = new CustomerServiceFactory().getService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomer(CreateCustomerDto customerRequest) {
        try {
            var newCustomer = service.createCustomer(
                    customerRequest
            );

            return Response.status(Response.Status.OK)
                    .entity(newCustomer)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Customer creation failed\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("id") UUID id) {
        System.out.println("Attempting to delete customer with ID: " + id);
        try {
            boolean isDeleted = service.deregisterCustomer(id);
            if (!isDeleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Customer does not exist\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return Response.status(Response.Status.OK)
                    .entity("{\"message\": \"Customer deleted successfully\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected error occurred\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}
