package com.banking.psd2.services;

import com.banking.exception.InsufficientFundsException;
import com.banking.psd2.api.model.account.BalanceAmount;
import com.banking.psd2.api.model.payment.Payment;
import com.banking.psd2.api.model.payment.PaymentResponse;
import com.banking.psd2.api.model.payment.PaymentStatus;
import com.banking.psd2.config.ExternalBankingApiConfig;
import com.banking.psd2.db.services.PaymentRepoService;
import com.banking.psd2.services.helpers.UrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service for processing payment operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountService accountService;
    private final PaymentRepoService paymentRepoService;
    private final ExternalBankingApiConfig externalBankingApiConfig;
    private final WebClient webClient;


    public PaymentResponse processPayment(Payment request) {
        log.info("Processing payment from account {} to account {}, amount: {}",
                request.getDebtorAccount().getIban(),
                request.getCreditorAccount().getIban(),
                request.getInstructedAmount().getAmount());

        String url = UrlBuilder.buildUrl(externalBankingApiConfig.getBaseUrl(), externalBankingApiConfig.getPaymentsUrl(), null, null);
        log.debug("Payment endpoint URL: {}", url);

        if (!checkSufficientFunds(request)) {
            log.error("Insufficient funds for payment from account {}", request.getDebtorAccount().getIban());
            throw new InsufficientFundsException("Insufficient funds for payment");
        }

        Long id = registerPayment(request);
        log.debug("Payment registered with internal ID: {}", id);

        PaymentResponse paymentResponse = sendPaymentRequest(url, request);
        log.info("Payment request sent successfully, external payment ID: {}, status: {}",
                paymentResponse.getPaymentId(), paymentResponse.getStatus());

        return updatePaymentStatus(paymentResponse, id);
    }

    private boolean checkSufficientFunds(Payment request) {
        String debtorIban = request.getDebtorAccount().getIban();
        BigDecimal amount = request.getInstructedAmount().getAmount();
        BalanceAmount balance = accountService.getAccountBalance(debtorIban);

        boolean isSufficient = balance.getAmount().compareTo(amount) >= 0;
        log.debug("Funds check for account {}: balance = {}, required = {}, sufficient = {}",
                debtorIban, balance.getAmount(), amount, isSufficient);

        return isSufficient;
    }

    private Long registerPayment(Payment request) {
        request.setStatus(PaymentStatus.PDNG);
        Long id = paymentRepoService.savePayment(request);
        log.debug("Payment registered with status PENDING, internal ID: {}", id);
        return id;
    }

    private PaymentResponse sendPaymentRequest(String url, Payment request) {
        log.debug("Sending payment request to external API: {}", url);

        Optional<PaymentResponse> paymentResponse = Optional.ofNullable(webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Remote server error: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Remote server encountered an error"));
                })
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("Client error when sending payment: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Payment request failed"));
                })
                .bodyToMono(PaymentResponse.class)
                .block());

        if (paymentResponse.isEmpty()) {
            log.error("Received null response from payment API");
        } else {
            log.debug("Payment response received successfully: ID={}", paymentResponse.get().getPaymentId());
        }
        return paymentResponse.orElseThrow(() -> new RuntimeException("Unable to process Payment request"));
    }

    private PaymentResponse updatePaymentStatus(PaymentResponse paymentResponse, Long id) {
        log.debug("Updating payment status for ID: {}, external ID: {}, status: {}",
                id, paymentResponse.getPaymentId(), paymentResponse.getStatus());

        paymentRepoService.updatePaymentStatus(id, paymentResponse.getPaymentId(), paymentResponse.getStatus());
        log.info("Payment status updated successfully");
        return paymentResponse;
    }
}