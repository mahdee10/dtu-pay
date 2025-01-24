package dtu.dtuPay.services;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import dtu.dtuPay.dtos.Payment;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import dtu.dtuPay.services.interfaces.IReportingService;

import java.util.List;
import java.util.UUID;

public class ReportingService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
    IReportingService service = baseURL.proxy(IReportingService.class);

    public List<Payment> getAllPayments() throws Exception {
        Response response = service.getPayments();

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<List<Payment>>() {});
    }

    public List<Payment> getCustomerPayments(UUID customerId) throws Exception {
        Response response = service.getCustomerPayments(customerId);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<List<Payment>>() {});
    }

    public List<Payment> getMerchantPayments(UUID merchantId) throws Exception {
        Response response = service.getMerchantPayments(merchantId);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<List<Payment>>() {});
    }
}
