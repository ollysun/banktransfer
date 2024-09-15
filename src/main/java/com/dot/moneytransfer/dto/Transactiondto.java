package com.dot.moneytransfer.dto;

import com.dot.moneytransfer.constant.TransactionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transactiondto {

    private Long id;
    private String transactionReference;
    private BigDecimal amount;
    private BigDecimal transactionFee;
    private BigDecimal billedAmount;
    private String description;
    private LocalDateTime dateCreated;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String statusMessage;
    private boolean commissionWorthy;
    private BigDecimal commission;
    private String accountNumberFrom;
    private String accountNumberTo;

}
