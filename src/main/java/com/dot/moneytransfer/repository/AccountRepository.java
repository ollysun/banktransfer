package com.dot.moneytransfer.repository;

import com.dot.moneytransfer.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    AccountEntity findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

}