package com.service.management.account.danske.controller;

import com.service.management.account.danske.exception.DataNotFoundException;
import com.service.management.account.danske.model.*;
import com.service.management.account.danske.service.AccountManagementService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static com.service.management.account.danske.util.TestUtils.buildAccountDetails;
import static com.service.management.account.danske.util.TestUtils.buildAccountTransaction;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureWebClient
public class AccountManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountManagementService accountManagementService;

    @Test
    void shouldCreateBankAccountWhenValidInformationIsProvided() throws Exception {
        //given
        AccountDetails accountDetails = buildAccountDetails();

        given(accountManagementService.createAccount(accountDetails)).willReturn(AccountDetails.builder().bankAccountNumber("DANSKE0000000001").build());

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getAccountDetailsPayload()))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"bankAccountNumber\":\"DANSKE0000000001\"}"));
    }

    @Test
    void shouldDepositAmountIntoBankAccountWhenValidInformationIsProvided() throws Exception {
        //given
        AccountTransaction accountTransaction = buildAccountTransaction();

        given(accountManagementService.performAccountActivity(accountTransaction)).willReturn(AccountDetails.builder().bankAccountNumber("DANSKE0000000001").amount(2000.0).build());

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/account/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getAccountTransactionPayload()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"bankAccountNumber\":\"DANSKE0000000001\"}"));
    }


    @Test
    void shouldNotCreateBankAccountWhenInformationIsNotProper() throws Exception {

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getIncompleteAccountDetailsPayload()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldReturnAccountBalanceWhenRequestedWithValidAccountId() {
        //given

        given(accountManagementService.findAccountBalance("DANSKE0000000011")).willReturn(AccountDetails.builder()
                .amount(123.0)
                .currency(Currency.GBP)
                .bankAccountNumber("DANSKE0000000011")
                .build());

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/account/DANSKE0000000011/balance"))
                .andExpect(status().isOk())
                .andExpect(content().json(getExpectedBalanceResponse()));
    }

    @Test
    void shouldThrowNoDataFoundExceptionWhenRequestedForBalanceWithNonExistingBankAccountNumber() throws Exception {
        //given

        given(accountManagementService.findAccountBalance("DANSKE0000000011")).willThrow(DataNotFoundException.class);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/account/DANSKE0000000011/balance"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnMiniWhenRequestedWithValidAccountNumber() throws Exception {
        //given
        given(accountManagementService.findMiniStatement("DANSKE0000000011")).willReturn(List.of(AccountMiniStatement.builder()
                .amount(10000.0)
                .transactionDate(LocalDateTime.of(2022, 1, 1, 10, 10))
                .activity(Activity.DEPOSIT)
                .currency(Currency.GBP)
                .build()));

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/account/DANSKE0000000011/mini-statements"))
                .andExpect(status().isOk())
                .andExpect(content().json(getExpectedMiniStatementResponse()));
    }

    @Test
    void shouldThrowNoDataFoundExceptionWhenRequestedForMiniStatementWithNonExistingBankAccountNumber() throws Exception {
        //given

        given(accountManagementService.findMiniStatement("DANSKE0000000011")).willThrow(DataNotFoundException.class);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/account/DANSKE0000000011/mini-statements"))
                .andExpect(status().isNotFound());
    }

    private String getExpectedMiniStatementResponse() {
        return "[{\"amount\":10000.0,\"activity\":\"DEPOSIT\",\"currency\":\"GBP\",\"transaction-date\":\"2022-01-01-10:10\"}]";
    }

    private String getExpectedBalanceResponse() {
        return "{\"amount\":123.0,\"currency\":\"GBP\",\"bankAccountNumber\":\"DANSKE0000000011\"}";
    }


    private String getAccountDetailsPayload() {
        return "{\n" +
                "    \"name\":\"Roshan\",\n" +
                "    \"accountType\" : \"SAVINGS\",\n" +
                "    \"currency\" : \"GBP\",\n" +
                "    \"dateOfBirth\" : \"1989-03-15\",\n" +
                "    \"amount\" : 10000,\n" +
                "    \"address\" : \"CPH\"\n" +
                "}";
    }

    private String getIncompleteAccountDetailsPayload() {
        return "{\n" +
                "    \"accountType\" : \"SAVINGS\",\n" +
                "    \"currency\" : \"GBP\",\n" +
                "    \"dateOfBirth\" : \"1989-03-15\",\n" +
                "    \"amount\" : 10000,\n" +
                "    \"address\" : \"CPH\"\n" +
                "}";
    }

    private String getAccountTransactionPayload() {
        return "{\n" +
                "    \"bankAccountNumber\": \"DANSKE0000000003\",\n" +
                "    \"activity\": \"DEPOSIT\",\n" +
                "    \"currency\": \"GBP\",\n" +
                "    \"amount\": 10000\n" +
                "}";
    }




}
