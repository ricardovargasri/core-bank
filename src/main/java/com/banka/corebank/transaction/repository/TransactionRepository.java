package com.banka.corebank.transaction.repository;

import com.banka.corebank.account.entity.Account;
import com.banka.corebank.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @EntityGraph(attributePaths = { "account" })
    List<Transaction> findAllByAccountOrderByCreatedAtDesc(Account account);

    @EntityGraph(attributePaths = { "account" })
    Page<Transaction> findAllByAccountOrderByCreatedAtDesc(Account account, Pageable pageable);
}
