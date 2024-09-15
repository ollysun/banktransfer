package com.dot.moneytransfer.services;


import com.dot.moneytransfer.dto.AccountDetailsDto;
import com.dot.moneytransfer.dto.CreateAccountRequestDTO;
import com.dot.moneytransfer.dto.Transactiondto;
import com.dot.moneytransfer.entity.AccountEntity;
import com.dot.moneytransfer.exceptionhandling.ResourceException;
import com.dot.moneytransfer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public AccountEntity updateBalance(String accountNumber, boolean debit, double amount) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber);

        if (debit) {
            account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(amount)));
        } else {
            account.setBalance(account.getBalance().add(BigDecimal.valueOf(amount)));
        }

        return accountRepository.save(account);
    }

    @Transactional
    public CreateAccountRequestDTO createAccount(CreateAccountRequestDTO createAccountRequestDTO) {
        if (createAccountRequestDTO == null) {
            throw new ResourceException("createAccountRequestDTO cannot be null");
        }

        if(accountRepository.existsByAccountNumber(createAccountRequestDTO.getAccountNumber())) {
            throw new ResourceException("Account number already exists");
        }

        AccountEntity accountEntity = modelMapper.map(createAccountRequestDTO, AccountEntity.class);
        return modelMapper.map(accountRepository.save(accountEntity), CreateAccountRequestDTO.class);
    }

    public BigDecimal verifyAccountBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).getBalance();
    }

    @Transactional(readOnly = true)
    public List<CreateAccountRequestDTO> getAllAccounts() {

        return accountRepository.findAll().stream()
                .map(accountEntity  -> modelMapper.map(accountEntity, CreateAccountRequestDTO.class))
                .toList();
    }

}
