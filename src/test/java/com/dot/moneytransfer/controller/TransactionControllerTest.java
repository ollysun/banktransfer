package com.dot.moneytransfer.controller;

import com.dot.moneytransfer.constant.TransactionStatus;
import com.dot.moneytransfer.dto.AccountDetailsDto;
import com.dot.moneytransfer.dto.Transactiondto;
import com.dot.moneytransfer.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountDetailsDto accountDetailsDto;
    private Transactiondto transactiondto;

    @BeforeEach
    void setUp() {
        accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setAccountNumberFrom("1234589089");
        accountDetailsDto.setAccountNumberTo("6789078990");
        accountDetailsDto.setAmount(BigDecimal.valueOf(500));

        transactiondto = new Transactiondto();
        transactiondto.setAccountNumberFrom("1234589089");
        transactiondto.setAccountNumberTo("6789078990");
        transactiondto.setAmount(BigDecimal.valueOf(500));
        transactiondto.setStatus(TransactionStatus.valueOf("SUCCESSFUL"));
    }

    @Test
    void testTransferMoney_Success() throws Exception {
        // Mock the service response
        when(transactionService.processTransfer(any(AccountDetailsDto.class)))
                .thenReturn(transactiondto);

        // Perform the POST request and validate the response
        mockMvc.perform(post("/api/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDetailsDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumberFrom").value("1234589089"))
                .andExpect(jsonPath("$.accountNumberTo").value("6789078990"))
                .andExpect(jsonPath("$.status").value("SUCCESSFUL"));
    }

    @Test
    void testGetTransactions_Success() throws Exception {
        // Mock the service response
        when(transactionService.getTransactions(any(), any(), any(), any()))
                .thenReturn(List.of(transactiondto));

        // Perform the GET request and validate the response
        mockMvc.perform(get("/api/transactions")
                .param("status", "SUCCESSFUL")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountNumberFrom").value("1234589089"))
                .andExpect(jsonPath("$[0].accountNumberTo").value("6789078990"))
                .andExpect(jsonPath("$[0].status").value("SUCCESSFUL"));
    }

    @Test
    void testGetTransactionSummary_Success() throws Exception {
        // Mock the service response
        when(transactionService.getTransactionSummary(any(LocalDateTime.class)))
                .thenReturn(List.of(transactiondto));

        // Perform the GET request and validate the response
        mockMvc.perform(get("/api/transactions/summary")
                .param("date", LocalDateTime.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountNumberFrom").value("1234589089"))
                .andExpect(jsonPath("$[0].accountNumberTo").value("6789078990"))
                .andExpect(jsonPath("$[0].status").value("SUCCESSFUL"));
    }

    @Test
    void testGetTransactions_NoTransactions() throws Exception {
        // Mock empty response
        when(transactionService.getTransactions(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Perform the GET request and validate the response
        mockMvc.perform(get("/api/transactions")
                .param("status", "SUCCESSFUL")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testTransferMoney_InvalidRequest() throws Exception {
        // Modify DTO to create an invalid request (missing amount)
        accountDetailsDto.setAmount(null);

        // Perform the POST request and expect a validation failure
        mockMvc.perform(post("/api/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDetailsDto)))
                .andExpect(status().isBadRequest());
    }
}
