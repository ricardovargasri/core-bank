package com.banka.corebank.transaction.service;

import com.banka.corebank.transaction.dto.request.DepositRequest;
import com.banka.corebank.transaction.dto.request.TransferRequest;
import com.banka.corebank.transaction.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse deposit(DepositRequest request, String userEmail);

    List<TransactionResponse> transfer(TransferRequest request, String userEmail);

    List<TransactionResponse> getAccountHistory(String accountNumber, String userEmail);

    List<TransactionResponse> getAccountHistoryForAdmin(String accountNumber);
}
