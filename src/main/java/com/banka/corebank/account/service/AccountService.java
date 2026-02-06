package com.banka.corebank.account.service;

import com.banka.corebank.account.dto.request.CreateAccountRequest;
import com.banka.corebank.account.dto.response.AccountResponse;
import com.banka.corebank.customer.entity.Customer;

import java.util.UUID;

public interface AccountService {

    AccountResponse createInitialAccount(Customer customer);

    AccountResponse createNewAccount(UUID customerId, CreateAccountRequest request);
}
