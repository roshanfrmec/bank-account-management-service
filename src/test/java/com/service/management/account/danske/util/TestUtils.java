package com.service.management.account.danske.util;

import com.service.management.account.danske.model.*;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class TestUtils {

    public static AccountDetails buildAccountDetails() {
        return AccountDetails.builder()
                .name("Roshan")
                .address("CPH")
                .accountType(AccountType.SAVINGS)
                .amount(10000.0)
                .currency(Currency.GBP)
                .dateOfBirth(LocalDate.of(1989, 3, 15))
                .build();
    }

    public static AccountTransaction buildAccountTransaction() {
        return AccountTransaction.builder()
                .activity(Activity.DEPOSIT)
                .amount(10000.0)
                .currency(Currency.GBP)
                .bankAccountNumber("DANSKE0000000003")
                .build();
    }
}
