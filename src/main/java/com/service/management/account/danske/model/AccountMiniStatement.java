package com.service.management.account.danske.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountMiniStatement {

    @JsonProperty("account-id")
    private Long accountId;

    private Double amount;

    private Activity activity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'-'HH:mm")
    @JsonProperty("transaction-date")
    private LocalDateTime transactionDate;

    private Currency currency;
}
