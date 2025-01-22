package resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.TokenEventMessage;
import models.dtos.PaymentRequestDto;
import models.dtos.TokenRequestDto;
import services.TokenService;

import java.util.List;
import java.util.UUID;

@Path("customers/tokens")
public class TokenResource {
    TokenService service = TokenService.getInstance();

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTokens(TokenRequestDto tokenRequestDto) {
        TokenEventMessage eventMessage = service.createTokens(tokenRequestDto.getCustomerId(), tokenRequestDto.getNTokens());

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
    @Path("/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokens(@PathParam("customerId") UUID customerId) {
        TokenEventMessage eventMessage = service.getToken(customerId);

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
