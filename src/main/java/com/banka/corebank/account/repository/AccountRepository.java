package com.banka.corebank.account.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.UUID;
import com.banka.corebank.account.entity.Account;

import com.banka.corebank.customer.entity.Customer;
import java.util.Optional;
import java.util.List;
import org.springframework.lang.NonNull;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Override
    @EntityGraph(attributePaths = { "customer" })
    @NonNull
    List<Account> findAll();

    Optional<Account> findFirstByCustomerOrderByCreatedAtDesc(Customer customer);

    @EntityGraph(attributePaths = { "customer" })
    List<Account> findAllByCustomer(Customer customer);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithLock(String accountNumber);

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
