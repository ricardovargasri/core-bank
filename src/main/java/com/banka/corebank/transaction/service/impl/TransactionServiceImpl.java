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

                // NEW: Security Check - Account Status
                if (!sourceAccount.isActive()) {
                        throw new BusinessException("Source account is inactive. Transactions are blocked.");
                }
                if (!destinationAccount.isActive()) {
                        throw new BusinessException("Destination account is inactive. Transactions are blocked.");
                }

                // 3. Check Balance
                if (sourceAccount.getBalance().compareTo(request.amount()) < 0) {
                        throw new BusinessException(
                                        "Insufficient funds in source account. Current balance: "
                                                        + sourceAccount.getBalance());
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

                Transaction outTx = createTransactionRecord(sourceAccount, request.amount(),
                                TransactionType.TRANSFER_OUT,
                                description, performer);
                Transaction inTx = createTransactionRecord(destinationAccount, request.amount(),
                                TransactionType.TRANSFER_IN,
                                description, performer);

                return List.of(
                                transactionMapper.toResponse(outTx, sourceAccount.getBalance()),
                                transactionMapper.toResponse(inTx, destinationAccount.getBalance()));
        }

        private Transaction createTransactionRecord(Account account, BigDecimal amount, TransactionType type,
                        String desc,
                        User performer) {
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
                // 1. Validate Amount
                if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException("Deposit amount must be greater than zero");
                }

                // 2. Find Account
                Account account = accountRepository.findByAccountNumber(request.accountNumber())
                                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

                // NEW: Security Check - Account Status
                if (!account.isActive()) {
                        throw new BusinessException("Account is inactive. Deposits are blocked.");
                }

                // 3. Find Performer (Audit)
                User performer = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User performing the transaction not found"));

                // NEW: Business Rule - Only ADMIN or TELLER can perform deposits
                if (performer.getRole() == com.banka.corebank.user.enums.UserRole.USER) {
                        throw new BusinessException("Self-deposits are not allowed. Please visit a Teller.");
                }

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

                return transactionMapper.toResponse(savedTransaction, account.getBalance());
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
