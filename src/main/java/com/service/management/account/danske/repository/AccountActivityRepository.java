package com.service.management.account.danske.repository;

import com.service.management.account.danske.domain.AccountActivity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountActivityRepository extends CrudRepository<AccountActivity, Long> {

    /**
     * Method to return top 10 account activity in descending order based on transaction date
     *
     * @param accountId account id for which mini statement is requested
     * @return top 10 account activity in descending order based on transaction date
     */
    List<AccountActivity> findTop10ByAccountIdOrderByTransactionDateDesc(Long accountId);

}
