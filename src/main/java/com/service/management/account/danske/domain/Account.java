package com.service.management.account.danske.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.service.management.account.danske.model.AccountDetails;
import com.service.management.account.danske.model.Activity;
import com.service.management.account.danske.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long accountId;

    private String name;

    private LocalDate dateOfBirth;

    private String address;
    private Double amount;
    private String currency;

    private String accountType;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @JsonIgnore
    public AccountDetails buildAccountDetails() {
        return AccountDetails.builder()
                .bankAccountNumber(getBankAccountNumber())
                .amount(amount)
                .currency(Currency.valueOf(currency))
                .build();
    }

    public boolean hasSufficientFund(Double transactionAmount) {
        return amount.compareTo(transactionAmount) >= 0;
    }

    public AccountActivity buildAccountActivity(Double amount, Currency currency, Activity activity) {
        return AccountActivity.builder()
                .amount(amount)
                .currency(currency)
                .accountId(accountId)
                .activity(activity)
                .transactionDate(LocalDateTime.now())
                .build();
    }

    public String getBankAccountNumber() {
        return "DANSKE" + String.format("%010d", accountId);
    }
}
