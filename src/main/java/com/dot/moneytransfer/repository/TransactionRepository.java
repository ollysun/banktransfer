package com.dot.moneytransfer.repository;

import com.dot.moneytransfer.entity.TransactionEntity;
import com.dot.moneytransfer.constant.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByStatus(TransactionStatus status);

    List<TransactionEntity> findByAccountNumberFromOrAccountNumberTo(String accountNumberFrom, String accountNumberTo);

    List<TransactionEntity> findByDateCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
}