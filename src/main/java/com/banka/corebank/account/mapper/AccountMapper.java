package com.banka.corebank.account.mapper;

import com.banka.corebank.account.dto.request.CreateAccountRequest;
import com.banka.corebank.account.dto.response.AccountResponse;
import com.banka.corebank.account.dto.response.AdminAccountResponse;
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
                account.getCreatedAt(),
                account.isActive());
    }

    public AdminAccountResponse toAdminResponse(Account account) {
        String ownerName = account.getCustomer() != null ? account.getCustomer().getName() : "Unknown";
        String ownerEmail = account.getCustomer() != null ? account.getCustomer().getEmail() : "Unknown";

        return new AdminAccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getType(),
                account.getBalance(),
                account.getCreatedAt(),
                ownerName,
                ownerEmail,
                account.isActive());
    }

    public Account toEntity(CreateAccountRequest request) {
        Account account = new Account();
        account.setType(request.type());
        return account;
    }
}
