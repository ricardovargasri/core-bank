package com.banka.corebank.account.controller;

import com.banka.corebank.account.dto.request.CreateAccountRequest;
import com.banka.corebank.account.dto.response.AccountResponse;
import com.banka.corebank.account.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<AccountResponse> createNewAccount(
            @PathVariable UUID customerId,
            @RequestBody CreateAccountRequest request) {
        return new ResponseEntity<>(accountService.createNewAccount(customerId, request), HttpStatus.CREATED);
    }
}
