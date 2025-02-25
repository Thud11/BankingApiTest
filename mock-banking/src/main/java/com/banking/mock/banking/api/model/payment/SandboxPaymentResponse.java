package com.banking.mock.banking.api.model.payment;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SandboxPaymentResponse {
    private UUID paymentId;
    private SandboxTransactionStatus status;
    private SandboxTransactionFees transactionFees;
}