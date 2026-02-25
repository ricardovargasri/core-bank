package com.banka.corebank.account.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
