package com.banking.mock.banking.init;

import com.banking.mock.banking.db.model.DepositAccountEntity;
import com.banking.mock.banking.db.repo.DepositAccountRepository;
import com.banking.utils.ResourceLoaderUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("test")
@Slf4j
@RequiredArgsConstructor
public class SandboxInitializer  {

    private final DepositAccountRepository depositAccountRepository;
    private static final String ACCOUNTS = "classpath:/sandbox/bank-accounts.json";


    @EventListener(ContextRefreshedEvent.class)
    public void run() {
        log.info("Initializing sandbox content");
        initBankAccounts();
        log.info("Sandbox content initialized");
    }

    private void initBankAccounts() {
        log.info("Init sandbox accounts");
        List<DepositAccountEntity> depositAccountEntities = getSandboxAccounts();
        depositAccountRepository.saveAll(depositAccountEntities);
        log.info("Sandbox accounts initialized");
    }

    private List<DepositAccountEntity> getSandboxAccounts() {
        return ResourceLoaderUtil.readResource(ACCOUNTS, new TypeReference<>() {
        });
    }
}
