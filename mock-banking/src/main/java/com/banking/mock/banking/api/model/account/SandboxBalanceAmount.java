package com.banking.mock.banking.api.model.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SandboxBalanceAmount {
    Currency currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    BigDecimal amount;
}
