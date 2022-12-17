package com.service.management.account.danske.controller;

import com.service.management.account.danske.model.*;
import com.service.management.account.danske.service.AccountManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@Validated
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountManagementController {

    private final AccountManagementService accountManagementService;


    @PostMapping(produces = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public AccountDetails createAccount(
            @Valid @RequestBody AccountDetails accountDetails) {
        return accountManagementService.createAccount(accountDetails);
    }

    @PutMapping(value = "/transaction", produces = {"application/json"})
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDetails performAccountActivity(
            @Valid @RequestBody AccountTransaction accountTransaction) {
        return accountManagementService.performAccountActivity(accountTransaction);
    }

    @GetMapping(value = "/{bank-account-number}/balance", produces = {"application/json"})
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDetails getAccountBalance(
            @Pattern(regexp = "[0-9, A-Z]{16}", message = "Invalid bank account number")
            @PathVariable(value = "bank-account-number") String bankAccountNumber) {

        log.info("Retrieve account balance details requested for accountNumber : {}", bankAccountNumber);
        return accountManagementService.findAccountBalance(bankAccountNumber);
    }


    @GetMapping(value = "/{bank-account-number}/mini-statements", produces = {"application/json"})
    @ResponseStatus(value = HttpStatus.OK)
    public List<AccountMiniStatement> getMiniStatement(
            @Pattern(regexp = "[0-9, A-Z]{16}", message = "Invalid bank account number")
            @PathVariable(value = "bank-account-number") String bankAccountNumber) {

        log.info("Retrieve mini statement requested for account number : {}", bankAccountNumber);
        return accountManagementService.findMiniStatement(bankAccountNumber);
    }


}
