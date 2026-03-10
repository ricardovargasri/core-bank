package com.banka.corebank.transaction.controller;

import com.banka.corebank.transaction.dto.request.DepositRequest;
import com.banka.corebank.transaction.dto.request.TransferRequest;
import com.banka.corebank.transaction.dto.response.TransactionResponse;
import com.banka.corebank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @RequestBody DepositRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.deposit(request, authentication.getName()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<TransactionResponse>> transfer(
            @RequestBody TransferRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.transfer(request, authentication.getName()));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<Page<TransactionResponse>> getAccountHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> history = transactionService.getAccountHistory(
                accountNumber, authentication.getName(), pageable);
        return ResponseEntity.ok(history);
    }
}
