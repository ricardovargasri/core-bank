package com.banka.corebank.transaction.mapper;

import com.banka.corebank.transaction.dto.response.TransactionResponse;
import com.banka.corebank.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction tx, BigDecimal newBalance) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getType(),
                tx.getDescription(),
                tx.getCreatedAt(),
                tx.getAccount().getAccountNumber(),
                newBalance);
    }
}
