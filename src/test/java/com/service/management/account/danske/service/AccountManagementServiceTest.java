package com.service.management.account.danske.service;

import com.service.management.account.danske.domain.Account;
import com.service.management.account.danske.domain.AccountActivity;
import com.service.management.account.danske.exception.BadRequestException;
import com.service.management.account.danske.exception.DataNotFoundException;
import com.service.management.account.danske.model.*;
import com.service.management.account.danske.repository.AccountActivityRepository;
import com.service.management.account.danske.repository.AccountRepository;
import com.service.management.account.danske.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.service.management.account.danske.util.TestUtils.buildAccountTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class AccountManagementServiceTest {

    private AccountManagementService accountManagementService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private AccountActivityRepository accountActivityRepository;

    @BeforeEach
    void setUp() {
        this.accountManagementService = new AccountManagementService(accountRepository, accountActivityRepository);
    }

    @AfterEach
    void tearDown() {
        this.accountManagementService = null;
    }

    @Test
    void shouldCreateAccount() {
        //given
        AccountDetails accountDetails = TestUtils.buildAccountDetails();

        Account createdAccount = accountDetails.mapToAccount();
        createdAccount.setAccountId(1L);

        given(accountRepository.save(any(Account.class))).willReturn(createdAccount); // force to use any as localDateTime is being used
        given(accountActivityRepository.save(any(AccountActivity.class))).willReturn(AccountActivity.builder().build());

        //when
        AccountDetails accountInfo = accountManagementService.createAccount(accountDetails);

        //then
        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo.getBankAccountNumber()).isEqualTo("DANSKE0000000001");
    }

    @Test
    void shouldDepositAmount() {
        //given
        AccountTransaction accountTransaction = buildAccountTransaction();
        Account account = TestUtils.buildAccountDetails().mapToAccount();
        account.setAccountId(1L);
        given(accountRepository.findById(3L)).willReturn(Optional.ofNullable(account));

        given(accountRepository.save(any(Account.class))).willReturn(account);
        given(accountActivityRepository.save(any(AccountActivity.class))).willReturn(AccountActivity.builder().build());

        //when
        AccountDetails accountInfo = accountManagementService.performAccountActivity(accountTransaction);

        //then
        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo.getBankAccountNumber()).isEqualTo("DANSKE0000000001");
        assertThat(accountInfo.getAmount()).isEqualTo(20000.0);

    }

    @Test
    void shouldAllowWithdrawalOfAmount() {
        //given
        AccountTransaction accountTransaction = buildAccountTransaction();
        accountTransaction.setActivity(Activity.WITHDRAWAL);
        Account account = TestUtils.buildAccountDetails().mapToAccount();
        account.setAccountId(1L);
        account.setAmount(400000.0);
        given(accountRepository.findById(3L)).willReturn(Optional.ofNullable(account));

        given(accountRepository.save(any(Account.class))).willReturn(account);
        given(accountActivityRepository.save(any(AccountActivity.class))).willReturn(AccountActivity.builder().build());

        //when
        AccountDetails accountInfo = accountManagementService.performAccountActivity(accountTransaction);

        //then
        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo.getBankAccountNumber()).isEqualTo("DANSKE0000000001");
        assertThat(accountInfo.getAmount()).isEqualTo(390000.0);

    }

    @Test
    void shouldNotAllowWithdrawalIfAccountIsNotHavingSufficientFund() {
        //given
        AccountTransaction accountTransaction = buildAccountTransaction();
        accountTransaction.setActivity(Activity.WITHDRAWAL);
        accountTransaction.setAmount(30000.0);

        Account account = TestUtils.buildAccountDetails().mapToAccount();
        account.setAccountId(1L);
        given(accountRepository.findById(3L)).willReturn(Optional.ofNullable(account));

        given(accountRepository.save(any(Account.class))).willReturn(account);
        given(accountActivityRepository.save(any(AccountActivity.class))).willReturn(AccountActivity.builder().build());

        //when

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> accountManagementService.performAccountActivity(accountTransaction));

        //then
        assertThat(exception.getMessage()).isEqualTo("Insufficient fund available");

    }

    @Test
    void shouldReturnAccountBalance() {

        //given
        given(accountRepository.findById(3L)).willReturn(Optional.of(buildAccount(3L, 12.0)));

        //when
        AccountDetails accountDetails = accountManagementService.findAccountBalance("DANSKE0000000003");

        //then
        assertThat(accountDetails).isNotNull();
        assertThat(accountDetails.getAmount()).isEqualTo(12.0);
        assertThat(accountDetails.getCurrency()).isEqualTo(Currency.GBP);
    }

    @Test
    void shouldReturnNoDataFoundWhenAccountNumberDoesNotExist() {

        //given
        given(accountRepository.findById(3L)).willReturn(Optional.empty());

        //when

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> accountManagementService.findAccountBalance("DANSKE0000000003"));

        //then
        assertThat(exception.getMessage()).isEqualTo("No account found for requested account id");

    }

    @Test
    void shouldReturnMiniStatement() {

        //given
        given(accountRepository.findById(3L)).willReturn(Optional.of(buildAccount(3L, 12.0)));
        given(accountActivityRepository.findTop10ByAccountIdOrderByTransactionDateDesc(3L))
                .willReturn(buildAccountActivity());

        //when
        List<AccountMiniStatement> accountMiniStatements = accountManagementService.findMiniStatement("DANSKE0000000003");

        //then
        assertThat(accountMiniStatements).isNotNull();
        assertThat(accountMiniStatements.size()).isEqualTo(1);
        assertThat(accountMiniStatements.get(0).getAmount()).isEqualTo(100.0);
        assertThat(accountMiniStatements.get(0).getActivity()).isEqualTo(Activity.DEPOSIT);
        assertThat(accountMiniStatements.get(0).getTransactionDate()).isEqualTo(LocalDateTime.of(2022, 1, 1, 11, 10));
        assertThat(accountMiniStatements.get(0).getCurrency()).isEqualTo(Currency.GBP);

    }

    @Test
    void shouldReturnNoDataFoundForMiniStatementWhenAccountIdDoesNotExist() {

        //given
        given(accountActivityRepository.findById(3L)).willReturn(Optional.empty());

        //when

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> accountManagementService.findMiniStatement("DANSKE0000000003"));

        //then
        assertThat(exception.getMessage()).isEqualTo("No Account found for given bank account number");

    }

    private Account buildAccount(Long accountId, double amount) {
        return Account.builder()
                .accountId(accountId)
                .amount(amount)
                .currency(String.valueOf(Currency.GBP))
                .build();
    }

    private List<AccountActivity> buildAccountActivity() {
        return List.of(AccountActivity.builder()
                .accountId(112L)
                .amount(100.0)
                .currency(Currency.GBP)
                .activity(Activity.DEPOSIT)
                .transactionDate(LocalDateTime.of(2022, 1, 1, 11, 10))
                .build());
    }


}
