package com.dot.moneytransfer.scheduler;


import com.dot.moneytransfer.services.TransactionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionScheduler {

    private final TransactionService transactionService;

    public TransactionScheduler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void calculateCommissions() {
        transactionService.updateCommissionForSuccessfulTransactions();
    }

    @Scheduled(cron = "0 0 1 * * ?") // Runs daily at 1 AM
    public void generateDailyTransactionSummary() {
        transactionService.getTransactionSummary(LocalDateTime.now());
    }
}
