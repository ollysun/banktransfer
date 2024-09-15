package com.dot.moneytransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDetailsDto {
    @NotBlank(message = "please enter the account from number")
    @Positive(message = "Please enter positive number")
    @Size(min = 10, max = 10, message
            = "account number must be 10 number long")
    private String accountNumberFrom;
    @NotBlank(message = "please enter the account to number")
    @Positive(message = "Please enter positive number")
    @Size(min = 10, max = 10, message
            = "account number must be 10 number long")
    private String accountNumberTo;
    @NotNull(message = "please enter the amount")
    private BigDecimal amount;

}
