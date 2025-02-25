package com.banking.mock.banking.services;

import com.banking.mock.banking.api.model.account.SandboxBalanceAmount;
import com.banking.mock.banking.db.services.DepositAccountRepoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SandboxAccountService {

    private final DepositAccountRepoService depositAccountRepoService;

    public SandboxBalanceAmount getAccountBalance(String accountId) {
        return depositAccountRepoService.getBalanceByIban(accountId);
    }

    public boolean accountExistsByIban(String iban) {
        return !depositAccountRepoService.accountExistsByIban(iban);
    }

    public void changeBalance(String iban, BigDecimal balanceChange) {
        depositAccountRepoService.changeBalance(iban, balanceChange);
    }

    public boolean checkSufficientFunds(String iban, BigDecimal amount) {
        return depositAccountRepoService.checkSufficientFunds(iban, amount);
    }
}
