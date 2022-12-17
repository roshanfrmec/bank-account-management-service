package com.service.management.account.danske.repository;

import com.service.management.account.danske.domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

}
