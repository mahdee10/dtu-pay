package resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.dtos.PaymentRequestDto;
import models.dtos.TokenRequestDto;
import services.TokenService;

import java.util.List;
import java.util.UUID;

@Path("tokens")
public class TokenResource {
    TokenService service = TokenService.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTokens(TokenRequestDto tokenRequestDto) {
        int nTokensCreated = service.createTokens(tokenRequestDto.getCustomerId(), tokenRequestDto.getNTokens());

        return Response.status(Response.Status.OK)
                .entity(nTokensCreated)
                .build();
    }

    @GET
    @Path("/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@PathParam("customerId") UUID customerId) {
        UUID tokenUUID = service.getToken(customerId);

        return Response.status(Response.Status.OK)
                .entity(tokenUUID)
                .build();
    }
}
