package com.banking.mock.banking.db.model;

import com.banking.mock.banking.db.converters.CurrencyAttributeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
@Entity
@Table(name = "account_balance")
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Convert(converter = CurrencyAttributeConverter.class)
    private Currency currency;
    private BigDecimal amount;

    @UpdateTimestamp
    private LocalDateTime updated;




}
