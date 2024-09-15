package com.dot.moneytransfer.controller;

import com.dot.moneytransfer.dto.CreateAccountRequestDTO;
import com.dot.moneytransfer.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public CreateAccountRequestDTO createAccount(@RequestBody @Valid CreateAccountRequestDTO createAccountRequestDTO) {
        return accountService.createAccount(createAccountRequestDTO);
    }

    @GetMapping
    public List<CreateAccountRequestDTO> getAllBills() {
        return accountService.getAllAccounts();
    }

}
