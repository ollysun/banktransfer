package com.dot.moneytransfer.controller;


import com.dot.moneytransfer.dto.AccountDetailsDto;
import com.dot.moneytransfer.dto.Transactiondto;
import com.dot.moneytransfer.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/transfer")
    public Transactiondto transferMoney(@RequestBody @Valid AccountDetailsDto transaction) {
        return transactionService.processTransfer(transaction);
    }

    @GetMapping
    public List<Transactiondto> getTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        return transactionService.getTransactions(status, accountNumber, startDate, endDate);
    }

    @GetMapping("/summary")
    public List<Transactiondto> getTransactionSummary(@RequestParam LocalDateTime date) {
        return transactionService.getTransactionSummary(date);
    }
}
