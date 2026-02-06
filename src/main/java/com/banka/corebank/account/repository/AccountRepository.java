package com.banka.corebank.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.banka.corebank.account.entity.Account;

import com.banka.corebank.customer.entity.Customer;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findFirstByCustomerOrderByCreatedAtDesc(Customer customer);

    boolean existsByAccountNumber(String accountNumber);
}
