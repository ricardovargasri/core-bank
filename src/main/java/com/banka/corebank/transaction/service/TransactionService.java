package com.banka.corebank.transaction.service;

import com.banka.corebank.transaction.dto.request.DepositRequest;
import com.banka.corebank.transaction.dto.request.TransferRequest;
import com.banka.corebank.transaction.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    TransactionResponse deposit(DepositRequest request, String userEmail, String fullName);

    List<TransactionResponse> transfer(TransferRequest request, String userEmail, String fullName);

    List<TransactionResponse> getAccountHistory(String accountNumber, String userEmail);

    Page<TransactionResponse> getAccountHistory(String accountNumber, String userEmail, Pageable pageable);

    List<TransactionResponse> getAccountHistoryForAdmin(String accountNumber);

    Page<TransactionResponse> getAccountHistoryForAdmin(String accountNumber, Pageable pageable);
}
