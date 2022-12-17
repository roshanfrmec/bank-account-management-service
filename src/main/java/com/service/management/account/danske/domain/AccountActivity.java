package com.service.management.account.danske.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.service.management.account.danske.model.AccountMiniStatement;
import com.service.management.account.danske.model.Activity;
import com.service.management.account.danske.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ACCOUNT_ACTIVITY_ID")
    private Long id;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    private Double amount;

    private Activity activity;

    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    private Currency currency;

    private String remarks;

    @JsonIgnore
    public AccountMiniStatement buildAccountActivity() {
        return AccountMiniStatement.builder()
                .amount(amount)
                .currency(currency)
                .transactionDate(transactionDate)
                .activity(activity)
                .build();
    }
}
