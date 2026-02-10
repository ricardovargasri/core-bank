package com.banka.corebank.transaction.service.impl;

import com.banka.corebank.account.entity.Account;
import com.banka.corebank.account.repository.AccountRepository;
import com.banka.corebank.exception.BusinessException;
import com.banka.corebank.exception.ResourceNotFoundException;
import com.banka.corebank.transaction.dto.request.DepositRequest;
import com.banka.corebank.transaction.dto.request.TransferRequest;
import com.banka.corebank.transaction.dto.response.TransactionResponse;
import com.banka.corebank.transaction.entity.Transaction;
import com.banka.corebank.transaction.enums.TransactionType;
import com.banka.corebank.transaction.repository.TransactionRepository;
import com.banka.corebank.transaction.service.TransactionService;
import com.banka.corebank.user.entity.User;
import com.banka.corebank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<TransactionResponse> transfer(TransferRequest request, String userEmail) {
        // 1. Validations
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transfer amount must be greater than zero");
        }

        if (request.sourceAccountNumber().equals(request.destinationAccountNumber())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        // 2. Find Accounts
        Account sourceAccount = accountRepository.findByAccountNumber(request.sourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountNumber(request.destinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        // 3. Check Balance
        if (sourceAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new BusinessException(
                    "Insufficient funds in source account. Current balance: " + sourceAccount.getBalance());
        }

        // 4. Find Performer
        User performer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 5. Update Balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.amount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.amount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // 6. Create Audit Records (Two sides of the same coin)
        String description = (request.description() == null || request.description().isBlank())
                ? "sin descripcion"
                : request.description();

        Transaction outTx = createTransactionRecord(sourceAccount, request.amount(), TransactionType.TRANSFER_OUT,
                description, performer);
        Transaction inTx = createTransactionRecord(destinationAccount, request.amount(), TransactionType.TRANSFER_IN,
                description, performer);

        return List.of(
                mapToResponse(outTx, sourceAccount.getBalance()),
                mapToResponse(inTx, destinationAccount.getBalance()));
    }

    private Transaction createTransactionRecord(Account account, BigDecimal amount, TransactionType type, String desc,
            User performer) {
        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setType(type);
        tx.setDescription(desc);
        tx.setAccount(account);
        tx.setPerformedBy(performer);
        return transactionRepository.save(tx);
    }

    private TransactionResponse mapToResponse(Transaction tx, BigDecimal newBalance) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getType(),
                tx.getDescription(),
                tx.getCreatedAt(),
                tx.getAccount().getAccountNumber(),
                newBalance);
    }

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request, String userEmail) {
        // 1. Validate Amount
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Deposit amount must be greater than zero");
        }

        // 2. Find Account
        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // 3. Find Performer (Audit)
        User performer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User performing the transaction not found"));

        // 4. Update Balance
        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        // 5. Create Transaction audit record
        String description = (request.description() == null || request.description().isBlank())
                ? "sin descripcion"
                : request.description();

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDescription(description);
        transaction.setAccount(account);
        transaction.setPerformedBy(performer);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getAmount(),
                savedTransaction.getType(),
                savedTransaction.getDescription(),
                savedTransaction.getCreatedAt(),
                account.getAccountNumber(),
                account.getBalance());
    }
}
