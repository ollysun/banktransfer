package com.dot.moneytransfer.services;


import com.dot.moneytransfer.constant.TransactionStatus;
import com.dot.moneytransfer.dto.AccountDetailsDto;
import com.dot.moneytransfer.dto.Transactiondto;
import com.dot.moneytransfer.entity.TransactionEntity;
import com.dot.moneytransfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;


    @Transactional
    public Transactiondto processTransfer(AccountDetailsDto transaction) {
        TransactionEntity transactionEntity = new TransactionEntity();

        transactionEntity.setTransactionReference(UUID.randomUUID().toString());

        // Check if source account has sufficient funds
        if (accountService.verifyAccountBalance(transaction.getAccountNumberFrom()).compareTo(transaction.getAmount()) < 0) {
            transactionEntity.setStatus(TransactionStatus.INSUFFICIENT_FUND);
            transactionEntity.setAmount(transaction.getAmount());
            transactionEntity.setStatusMessage("Insufficient funds");
            transactionEntity.setDateCreated(LocalDateTime.now());
            transactionEntity.setAccountNumberFrom(transaction.getAccountNumberFrom());
            transactionEntity.setAccountNumberTo(transaction.getAccountNumberTo());
            return modelMapper.map(transactionRepository.save(transactionEntity), Transactiondto.class);
        }

        // Update balance of the source account (deduct amount)
         accountService.updateBalance(transaction.getAccountNumberFrom(),
                 true, transaction.getAmount().doubleValue());

        // Update balance of the destination account (add amount)
        accountService.updateBalance(
                transaction.getAccountNumberTo(),
                false,
                transaction.getAmount().doubleValue()
        );
        // Calculate transaction fee and billed amount
        BigDecimal transactionFee = transaction.getAmount()
                .multiply(BigDecimal.valueOf(0.005))
                .min(BigDecimal.valueOf(100));

        transactionEntity.setTransactionFee(transactionFee);
        transactionEntity.setAmount(transaction.getAmount());
        transactionEntity.setBilledAmount(transaction.getAmount().add(transactionFee));
        transactionEntity.setDateCreated(LocalDateTime.now());
        transactionEntity.setAccountNumberFrom(transaction.getAccountNumberFrom());
        transactionEntity.setAccountNumberTo(transaction.getAccountNumberTo());

        // Mark the transaction as successful
        transactionEntity.setStatus(TransactionStatus.SUCCESSFUL);
        transactionEntity.setStatusMessage("Transaction successful");

        return modelMapper.map(transactionRepository.save(transactionEntity), Transactiondto.class);
    }


    @Transactional(readOnly = true)
    public List<Transactiondto> getTransactions(String status, String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        // Initialize with all transactions
        List<Transactiondto> transactions = transactionRepository.findAll().stream()
                                            .map(transactionEntity ->  modelMapper.map(transactionEntity, Transactiondto.class))
                                            .toList();

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
            transactions = transactions.stream()
                    .filter(t -> t.getStatus() == transactionStatus)
                    .toList();
        }

        // Filter by account number (either from or to) if provided
        if (accountNumber != null && !accountNumber.isEmpty()) {
            transactions = transactions.stream()
                    .filter(t -> t.getAccountNumberFrom().equals(accountNumber) || t.getAccountNumberTo().equals(accountNumber))
                    .toList();
        }

        // Filter by date range if both startDate and endDate are provided
        if (startDate != null && endDate != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getDateCreated().isAfter(startDate) && t.getDateCreated().isBefore(endDate))
                    .toList();
        }

        return transactions;
    }


    public void updateCommissionForSuccessfulTransactions() {
        List<TransactionEntity> successfulTransactions = transactionRepository.findByStatus(TransactionStatus.SUCCESSFUL);
        for (TransactionEntity transaction : successfulTransactions) {
            BigDecimal commission = transaction.getTransactionFee().multiply(BigDecimal.valueOf(0.2));
            transaction.setCommission(commission);
            transaction.setCommissionWorthy(true);
            transactionRepository.save(transaction);
        }
    }

    public List<Transactiondto> getTransactionSummary(LocalDateTime date) {
        // Define the start and end of the day
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        // Fetch transactions within this date range
        return transactionRepository.findByDateCreatedBetween(startOfDay, endOfDay).stream()
                .map(transactionEntity ->  modelMapper.map(transactionEntity, Transactiondto.class))
                .toList();
    }

}
