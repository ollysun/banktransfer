package com.dot.moneytransfer.services;

import com.dot.moneytransfer.constant.TransactionStatus;
import com.dot.moneytransfer.dto.AccountDetailsDto;
import com.dot.moneytransfer.dto.Transactiondto;
import com.dot.moneytransfer.entity.AccountEntity;
import com.dot.moneytransfer.entity.TransactionEntity;
import com.dot.moneytransfer.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private ModelMapper modelMapper;

    private AccountDetailsDto accountDetailsDto;
    private AccountEntity fromAccount;
    private AccountEntity toAccount;
    private TransactionEntity transactionEntity;
    private Transactiondto transactionDtoSuccessful;
    private Transactiondto transactionDtoFail;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Sample data for testing
        accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setAccountNumberFrom("1234567890");
        accountDetailsDto.setAccountNumberTo("0987654321");
        accountDetailsDto.setAmount(BigDecimal.valueOf(500));

        fromAccount = new AccountEntity();
        fromAccount.setAccountNumber("12345");
        fromAccount.setBalance(BigDecimal.valueOf(1000));

        toAccount = new AccountEntity();
        toAccount.setAccountNumber("67890");
        toAccount.setBalance(BigDecimal.valueOf(500));

        transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionReference(UUID.randomUUID().toString());
        transactionEntity.setAmount(BigDecimal.valueOf(500));
        transactionEntity.setTransactionFee(BigDecimal.valueOf(2.5));
        transactionEntity.setBilledAmount(BigDecimal.valueOf(502.5));
        transactionEntity.setDateCreated(LocalDateTime.now());
        transactionEntity.setStatus(TransactionStatus.SUCCESSFUL);

        transactionDtoSuccessful = new Transactiondto();
        transactionDtoSuccessful.setTransactionReference(transactionEntity.getTransactionReference());
        transactionDtoSuccessful.setStatus(TransactionStatus.SUCCESSFUL);

        transactionDtoFail = new Transactiondto();
        transactionDtoFail.setTransactionReference(transactionEntity.getTransactionReference());
        transactionDtoFail.setStatus(TransactionStatus.INSUFFICIENT_FUND);
    }


@Test
@Transactional
void testProcessTransfer_SuccessfulTransaction() {
    // Mock account balance check for sufficient funds
    AccountEntity fromAccountEntity = new AccountEntity();
    fromAccountEntity.setBalance(BigDecimal.valueOf(1000));
    when(accountService.verifyAccountBalance(accountDetailsDto.getAccountNumberFrom()))
            .thenReturn(BigDecimal.valueOf(1000));

    // Mock repository save and modelMapper behavior
    when(accountService.updateBalance(anyString(), anyBoolean(), anyDouble())).thenReturn(fromAccountEntity);
    when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
    when(modelMapper.map(any(TransactionEntity.class), eq(Transactiondto.class)))
            .thenReturn(new Transactiondto());

    // Call the method to test
    Transactiondto result = transactionService.processTransfer(accountDetailsDto);

    // Assertions
    assertNotNull(result);
    verify(accountService, times(1)).updateBalance(eq("1234567890"), eq(true), eq(500.0));
    verify(accountService, times(1)).updateBalance(eq("0987654321"), eq(false), eq(500.0));
    verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    assertEquals(TransactionStatus.SUCCESSFUL, transactionEntity.getStatus());
}

    @Test
    void testProcessTransfer_InsufficientFunds() {

        // Mock insufficient funds scenario
        when(accountService.verifyAccountBalance(anyString()))
                .thenReturn(BigDecimal.valueOf(100));

        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
        when(modelMapper.map(any(TransactionEntity.class), eq(Transactiondto.class)))
                .thenReturn(transactionDtoFail);

        Transactiondto result = transactionService.processTransfer(accountDetailsDto);
        System.out.println("result: " + result);
        // Assert that transaction is marked as insufficient funds
        assertNotNull(result);
        assertEquals(TransactionStatus.INSUFFICIENT_FUND, result.getStatus());
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    @Transactional(readOnly = true)
    void testGetTransactions_FilterByStatus() {
        // Sample transactions
        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setTransactionReference(UUID.randomUUID().toString());
        transaction1.setStatus(TransactionStatus.SUCCESSFUL);

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setTransactionReference(UUID.randomUUID().toString());
        transaction2.setStatus(TransactionStatus.INSUFFICIENT_FUND);

        // Map entities to dto based on status
        Transactiondto transactionDto1 = new Transactiondto();
        transactionDto1.setStatus(TransactionStatus.SUCCESSFUL);

        Transactiondto transactionDto2 = new Transactiondto();
        transactionDto2.setStatus(TransactionStatus.INSUFFICIENT_FUND);

        // Mock repository and modelMapper behavior
        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

        when(modelMapper.map(transaction1, Transactiondto.class)).thenReturn(transactionDto1);
        when(modelMapper.map(transaction2, Transactiondto.class)).thenReturn(transactionDto2);

        // Call the method to test
        List<Transactiondto> result = transactionService.getTransactions("SUCCESSFUL", null, null, null);

        // Assertions
        assertEquals(1, result.size(), "The filtered result should contain 1 transaction.");
        assertEquals(TransactionStatus.SUCCESSFUL, result.get(0).getStatus(), "The status of the returned transaction should be SUCCESSFUL.");
        verify(transactionRepository, times(1)).findAll();
    }


    @Test
    @Transactional
    void testUpdateCommissionForSuccessfulTransactions() {
        // Sample successful transactions
        transactionEntity.setTransactionFee(BigDecimal.valueOf(10));

        when(transactionRepository.findByStatus(TransactionStatus.SUCCESSFUL))
                .thenReturn(List.of(transactionEntity));

        // Call the method to test
        transactionService.updateCommissionForSuccessfulTransactions();

        // Assertions
        assertTrue(transactionEntity.isCommissionWorthy());
        assertEquals(BigDecimal.valueOf(2.0), transactionEntity.getCommission()); // 20% of 10 = 2
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    @Transactional(readOnly = true)
    void testGetTransactionSummary() {
        // Sample transactions for the day
        LocalDateTime date = LocalDateTime.now();
        when(transactionRepository.findByDateCreatedBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(transactionEntity));
        when(modelMapper.map(any(TransactionEntity.class), eq(Transactiondto.class)))
                .thenReturn(transactionDtoSuccessful);

        // Call the method to test
        List<Transactiondto> result = transactionService.getTransactionSummary(date);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByDateCreatedBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
