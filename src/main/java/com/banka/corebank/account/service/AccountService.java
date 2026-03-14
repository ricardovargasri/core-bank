package com.banka.corebank.account.service;

import com.banka.corebank.account.dto.request.CreateAccountRequest;
import com.banka.corebank.account.dto.response.AccountResponse;
import com.banka.corebank.account.dto.response.AdminAccountResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<AccountResponse> getMyAccounts(String email, String fullName);

    List<AdminAccountResponse> getAllAccounts();

    AccountResponse createInitialAccount(com.banka.corebank.customer.entity.Customer customer);

    AccountResponse createNewAccount(UUID customerId, CreateAccountRequest request);

    AccountResponse createNewAccountForUser(String email, String fullName, CreateAccountRequest request);

    void deactivateAccount(UUID accountId);

    void activateAccount(UUID accountId);
}
