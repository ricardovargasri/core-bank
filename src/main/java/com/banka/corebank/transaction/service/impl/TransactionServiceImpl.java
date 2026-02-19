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
import com.banka.corebank.transaction.mapper.TransactionMapper;
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
        private final TransactionMapper transactionMapper;

        // Functional Interface for Validation
        @FunctionalInterface
        private interface ValidationRule<T> {
                void validate(T t);
        }

        @Override
        @Transactional
        public List<TransactionResponse> transfer(TransferRequest request, String userEmail) {
                // 1. Find Actors (Data Fetching)
                Account sourceAccount = accountRepository.findByAccountNumber(request.sourceAccountNumber())
                                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
                Account destinationAccount = accountRepository.findByAccountNumber(request.destinationAccountNumber())
                                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));
                User performer = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // 2. Functional Validations
                List<ValidationRule<Void>> validators = List.of(
                                // Basic Input Checks
                                v -> {
                                        if (request.amount().compareTo(BigDecimal.ZERO) <= 0)
                                                throw new BusinessException(
                                                                "Transfer amount must be greater than zero");
                                },
                                v -> {
                                        if (request.sourceAccountNumber().equals(request.destinationAccountNumber()))
                                                throw new BusinessException(
                                                                "Source and destination accounts must be different");
                                },

                                // Account Status Validations
                                v -> {
                                        if (!sourceAccount.isActive())
                                                throw new BusinessException(
                                                                "Source account is inactive. Transactions are blocked.");
                                },
                                v -> {
                                        if (!destinationAccount.isActive())
                                                throw new BusinessException(
                                                                "Destination account is inactive. Transactions are blocked.");
                                },

                                // Balance Check
                                v -> {
                                        if (sourceAccount.getBalance().compareTo(request.amount()) < 0)
                                                throw new BusinessException("Insufficient funds. Balance: "
                                                                + sourceAccount.getBalance());
                                });

                // Execute all validations
                validators.forEach(v -> v.validate(null));

                // 3. Execution (Side Effects)
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.amount()));
                destinationAccount.setBalance(destinationAccount.getBalance().add(request.amount()));

                accountRepository.save(sourceAccount);
                accountRepository.save(destinationAccount);

                // 4. Audit
                return createTransferAudit(sourceAccount, destinationAccount, request, performer);
        }

        private List<TransactionResponse> createTransferAudit(Account source, Account dest, TransferRequest req,
                        User performer) {
                String description = (req.description() == null || req.description().isBlank()) ? "sin descripcion"
                                : req.description();

                Transaction outTx = createTransactionRecord(source, req.amount(), TransactionType.TRANSFER_OUT,
                                description, performer);
                Transaction inTx = createTransactionRecord(dest, req.amount(), TransactionType.TRANSFER_IN, description,
                                performer);

                return List.of(transactionMapper.toResponse(outTx, source.getBalance()),
                                transactionMapper.toResponse(inTx, dest.getBalance()));
        }

        private Transaction createTransactionRecord(Account account, BigDecimal amount, TransactionType type,
                        String desc, User performer) {
                Transaction tx = new Transaction();
                tx.setAmount(amount);
                tx.setType(type);
                tx.setDescription(desc);
                tx.setAccount(account);
                tx.setPerformedBy(performer);
                return transactionRepository.save(tx);
        }

        @Override
        @Transactional
        public TransactionResponse deposit(DepositRequest request, String userEmail) {
                // 1. Find Actors
                Account account = accountRepository.findByAccountNumber(request.accountNumber())
                                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
                User performer = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("Performer not found"));

                // 2. Functional Validations
                List<ValidationRule<Void>> validators = List.of(
                                v -> {
                                        if (request.amount().compareTo(BigDecimal.ZERO) <= 0)
                                                throw new BusinessException("Deposit amount must be greater than zero");
                                },
                                v -> {
                                        if (!account.isActive())
                                                throw new BusinessException(
                                                                "Account is inactive. Deposits are blocked.");
                                },
                                v -> {
                                        if (performer.getRole() == com.banka.corebank.user.enums.UserRole.USER)
                                                throw new BusinessException(
                                                                "Self-deposits are not allowed. Please visit a Teller.");
                                });

                validators.forEach(v -> v.validate(null));

                // 3. Execution
                account.setBalance(account.getBalance().add(request.amount()));
                accountRepository.save(account);

                // 4. Audit
                String description = (request.description() == null || request.description().isBlank())
                                ? "sin descripcion"
                                : request.description();
                Transaction tx = createTransactionRecord(account, request.amount(), TransactionType.DEPOSIT,
                                description, performer);

                return transactionMapper.toResponse(tx, account.getBalance());
        }

        @Override
        @Transactional(readOnly = true)
        public List<TransactionResponse> getAccountHistory(String accountNumber, String userEmail) {
                Account account = accountRepository.findByAccountNumber(accountNumber)
                                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

                if (!account.getCustomer().getEmail().equals(userEmail)) {
                        throw new BusinessException("You are not authorized to view this account history");
                }

                return transactionRepository.findAllByAccountOrderByCreatedAtDesc(account)
                                .stream()
                                .map(tx -> transactionMapper.toResponse(tx, account.getBalance()))
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<TransactionResponse> getAccountHistoryForAdmin(String accountNumber) {
                Account account = accountRepository.findByAccountNumber(accountNumber)
                                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

                // Nota: Aquí NO validamos el email porque somos Admin
                return transactionRepository.findAllByAccountOrderByCreatedAtDesc(account)
                                .stream()
                                .map(tx -> transactionMapper.toResponse(tx, account.getBalance()))
                                .toList();
        }

}
