package org.dtu.reporting;

import messaging.implementations.RabbitMqQueue;
import org.dtu.reporting.models.Payment;
import org.dtu.reporting.services.ReportingService;

import java.util.List;
import java.util.UUID;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        var mq = new RabbitMqQueue("localhost");
        ReportingService reportingService = new ReportingService(mq);

        reportingService.requestPayments();
        Thread.sleep(3000);
        printPayments("All", reportingService.getAllPayments());

        var customerId = UUID.fromString("7911f9a4-440f-41b5-ae69-1082ddc7be69");
        reportingService.requestCustomerPayments(customerId);
        Thread.sleep(3000);
        printPayments("Customer", reportingService.getCustomerPayments(customerId));

        var merchantId = UUID.fromString("5a51e254-e9bf-4762-81d7-eeadf10347b6");
        reportingService.requestMerchantPayments(merchantId);
        Thread.sleep(3000);
        printPayments("Merchant", reportingService.getMerchantPayments(merchantId));
    }

    public static void printPayments(String title, List<Payment> payments) {
        System.out.println(title);
        if (payments.isEmpty()) {
            System.out.println("  No payments found.");
        } else {
            for (Payment payment : payments) {
                System.out.println("  - ID: " + payment.getId());
                System.out.println("    Customer Token: " + payment.getCustomerToken());
                System.out.println("    Merchant ID: " + payment.getMerchantId());
                System.out.println("    Amount: " + payment.getAmount());
            }
        }
    }
}
