package com.service.management.account.danske.service;

import com.service.management.account.danske.domain.Account;
import com.service.management.account.danske.domain.AccountActivity;
import com.service.management.account.danske.exception.BadRequestException;
import com.service.management.account.danske.exception.DataNotFoundException;
import com.service.management.account.danske.model.*;
import com.service.management.account.danske.repository.AccountActivityRepository;
import com.service.management.account.danske.repository.AccountRepository;
import com.service.management.account.danske.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.service.management.account.danske.util.Constants.NO_SUCH_ACCOUNT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class AccountManagementService {
    private final AccountRepository accountRepository;
    private final AccountActivityRepository accountActivityRepository;

    public AccountDetails createAccount(AccountDetails accountDetails) {
        Account account = accountRepository.save(accountDetails.mapToAccount());
        accountActivityRepository.save(account.buildAccountActivity(account.getAmount(), Currency.valueOf(account.getCurrency()), Activity.DEPOSIT));
        log.info("Bank account has been created and the account number is {}", account.getBankAccountNumber());
        return account.buildAccountDetails();
    }

    public AccountDetails performAccountActivity(AccountTransaction accountTransaction) {
        Long accountId = getAccountId(accountTransaction.getBankAccountNumber());
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("No Account found for given bank account number"));
        if (accountTransaction.getActivity().equals(Activity.WITHDRAWAL)) {
            if (!account.hasSufficientFund(accountTransaction.getAmount())) {
                throw new BadRequestException(Constants.INSUFFICIENT_FUND_AVAILABLE);
            }
            account.setAmount(account.getAmount() - accountTransaction.getAmount());
        } else {
            account.setAmount(account.getAmount() + accountTransaction.getAmount());
        }
        account.setUpdatedTime(LocalDateTime.now());
        Account updatedAccount = accountRepository.save(account);
        accountActivityRepository.save(account.buildAccountActivity(accountTransaction.getAmount(), accountTransaction.getCurrency(), accountTransaction.getActivity()));
        return updatedAccount.buildAccountDetails();
    }

    public AccountDetails findAccountBalance(String bankAccountNumber) {
        Long accountId = getAccountId(bankAccountNumber);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException(NO_SUCH_ACCOUNT_FOUND));
        return account.buildAccountDetails();
    }

    public List<AccountMiniStatement> findMiniStatement(String bankAccountNumber) {
        Long accountId = getAccountId(bankAccountNumber);
        List<AccountMiniStatement> accountMiniStatements = accountActivityRepository.findTop10ByAccountIdOrderByTransactionDateDesc(accountId)
                .stream()
                .map(AccountActivity::buildAccountActivity)
                .collect(Collectors.toList());
        if (accountMiniStatements.isEmpty()) {
            throw new DataNotFoundException("No Account found for given bank account number");
        }
        return accountMiniStatements;
    }

    private Long getAccountId(String bankAccountNumber) {
        String accountId = bankAccountNumber.substring(6, 16); // to take the numeric part of bank account number
        return Long.parseLong(accountId);
    }
}
