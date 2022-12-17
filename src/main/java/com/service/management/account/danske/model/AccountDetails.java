package com.service.management.account.danske.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.service.management.account.danske.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {

    @NotEmpty(message = "Name can not be empty")
    private String name;

    @NotNull(message = "Date of birth can not be null")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @PastOrPresent
    private LocalDate dateOfBirth;
    @NotEmpty(message = "Address can not be empty")
    private String address;
    @NotNull(message = "Amount can not be null")
    @Min(value = 1000, message = "Opening balance can not be less than 1000")
    private Double amount;

    @NotNull(message = "Currency type can not be empty")
    private Currency currency;
    @NotNull(message = "Account type can not be empty")
    private AccountType accountType;

    // Identity document can also be added as an attribute in real life implementation

    private String bankAccountNumber;

    public Account mapToAccount() {
        return Account.builder()
                .name(name)
                .address(address)
                .accountType(String.valueOf(accountType))
                .amount(amount)
                .currency(String.valueOf(currency))
                .createdTime(LocalDateTime.now())
                .build();
    }
}
