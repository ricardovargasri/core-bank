package com.banka.corebank.account.controller;

import com.banka.corebank.account.dto.request.CreateAccountRequest;
import com.banka.corebank.account.dto.response.AccountResponse;
import com.banka.corebank.account.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(Authentication authentication) {
        String fullName = getFullName(authentication);
        return ResponseEntity.ok(accountService.getMyAccounts(authentication.getName(), fullName));
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<AccountResponse> createNewAccount(
            @PathVariable UUID customerId,
            @RequestBody CreateAccountRequest request) {
        return new ResponseEntity<>(accountService.createNewAccount(customerId, request), HttpStatus.CREATED);
    }

    @PostMapping("/me")
    public ResponseEntity<AccountResponse> createNewAccountForMe(
            Authentication authentication,
            @RequestBody CreateAccountRequest request) {
        String fullName = getFullName(authentication);
        return new ResponseEntity<>(accountService.createNewAccountForUser(authentication.getName(), fullName, request),
                HttpStatus.CREATED);
    }

    private String getFullName(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
            return (String) jwt.getTokenAttributes().get("name");
        }
        return null;
    }
}
