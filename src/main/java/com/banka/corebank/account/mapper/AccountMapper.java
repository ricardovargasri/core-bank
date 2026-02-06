package com.banka.corebank.account.mapper;

import com.banka.corebank.account.dto.response.AccountResponse;
import com.banka.corebank.account.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getType(),
                account.getBalance(),
                account.getCreatedAt());
    }
}
