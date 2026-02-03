package com.banka.corebank.account.dto.request;

import com.banka.corebank.account.enums.AccountType;

public record CreateAccountRequest(
        AccountType type) {
}
