package com.banking.mock.banking.db.model;

import com.banking.mock.banking.db.converters.CurrencyAttributeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Data
@Entity
@Table(name = "deposit_account")
@NoArgsConstructor
@AllArgsConstructor
public class DepositAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String iban;

    @Convert(converter = CurrencyAttributeConverter.class)
    private Currency currency;

    private boolean active;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountBalanceEntity accountBalance;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private List<AccountTransactionEntity> transactions;

    @CreationTimestamp
    private LocalDateTime created;
}
