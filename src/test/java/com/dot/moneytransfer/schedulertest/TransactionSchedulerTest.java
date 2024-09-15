package com.dot.moneytransfer.schedulertest;


import com.dot.moneytransfer.scheduler.TransactionScheduler;
import com.dot.moneytransfer.services.TransactionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class TransactionSchedulerTest {


    // Scheduled method calculateCommissions runs daily at midnight
    @Test
    void test_calculate_commissions_scheduled_at_midnight() {
        TransactionService transactionService = mock(TransactionService.class);
        TransactionScheduler transactionScheduler = new TransactionScheduler(transactionService);

        transactionScheduler.calculateCommissions();

        verify(transactionService, times(1)).updateCommissionForSuccessfulTransactions();
    }

    // TransactionService throws an exception during calculateCommissions
    @Test
    void test_calculate_commissions_throws_exception() {
        TransactionService transactionService = mock(TransactionService.class);
        doThrow(new RuntimeException("Exception during commission calculation"))
            .when(transactionService).updateCommissionForSuccessfulTransactions();
        TransactionScheduler transactionScheduler = new TransactionScheduler(transactionService);

        assertThrows(RuntimeException.class, transactionScheduler::calculateCommissions);
    }
}