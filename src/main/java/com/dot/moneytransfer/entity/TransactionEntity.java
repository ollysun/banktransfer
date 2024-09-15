package com.dot.moneytransfer.entity;

import com.dot.moneytransfer.constant.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Make transactionReference unique
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