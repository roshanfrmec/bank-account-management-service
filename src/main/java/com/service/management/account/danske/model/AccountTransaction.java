package com.service.management.account.danske.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransaction {

    @NotEmpty(message = "Name can not be empty")
    private String bankAccountNumber;

    @NotNull(message = "activity type can not be empty")
    private Activity activity;

    @Min(value = 1)
    private Double amount;

    @NotNull(message = "currency can not be empty")
    private Currency currency;




}
