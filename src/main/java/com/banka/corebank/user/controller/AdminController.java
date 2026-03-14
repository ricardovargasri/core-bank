package com.banka.corebank.user.controller;

import com.banka.corebank.account.dto.response.AdminAccountResponse;
import com.banka.corebank.account.service.AccountService;
import com.banka.corebank.transaction.service.TransactionService;
import com.banka.corebank.user.entity.User;
import com.banka.corebank.user.repository.UserRepository;
import com.banka.corebank.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;

    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUserEmails() {
        List<String> emails = userRepository.findAll()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AdminAccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/accounts/{accountId}/deactivate")
    public ResponseEntity<Void> deactivateAccount(@PathVariable UUID accountId) {
        accountService.deactivateAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/accounts/{accountId}/activate")
    public ResponseEntity<Void> activateAccount(@PathVariable UUID accountId) {
        accountService.activateAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
    public ResponseEntity<List<com.banka.corebank.transaction.dto.response.TransactionResponse>> getAccountHistory(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getAccountHistoryForAdmin(accountNumber));
    }

    @org.springframework.web.bind.annotation.PostMapping("/deposit")
    public ResponseEntity<com.banka.corebank.transaction.dto.response.TransactionResponse> deposit(
            @org.springframework.web.bind.annotation.RequestBody com.banka.corebank.transaction.dto.request.DepositRequest request) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        String adminEmail = auth.getName();
        String fullName = getFullName(auth);

        return ResponseEntity.ok(transactionService.deposit(request, adminEmail, fullName));
    }

    private String getFullName(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
            return (String) jwt.getTokenAttributes().get("name");
        }
        return null;
    }
}
