package org.dtu.reporting.models;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Data
public class ReportingEventMessage {
    private UUID customerId;
    private UUID merchantId;
    private List<Payment> paymentList;
    private int requestResponseCode;
    private String exceptionMessage;
}
