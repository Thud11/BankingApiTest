package com.banking.mock.banking.api.model.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@Builder
public class SandboxTransactionFees {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;
    private Currency currency;
}
