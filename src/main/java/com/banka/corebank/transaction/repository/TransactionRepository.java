package com.banka.corebank.transaction.repository;

import com.banka.corebank.account.entity.Account;
import com.banka.corebank.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByAccountOrderByCreatedAtDesc(Account account);
}
