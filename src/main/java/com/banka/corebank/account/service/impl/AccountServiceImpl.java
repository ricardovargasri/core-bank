package com.banka.corebank.account.service.impl;

import com.banka.corebank.account.dto.request.CreateAccountRequest;
import com.banka.corebank.account.dto.response.AccountResponse;
import com.banka.corebank.account.entity.Account;
import com.banka.corebank.account.enums.AccountType;
import com.banka.corebank.account.mapper.AccountMapper;
import com.banka.corebank.account.repository.AccountRepository;
import com.banka.corebank.account.service.AccountService;
import com.banka.corebank.customer.entity.Customer;
import com.banka.corebank.customer.repository.CustomerRepository;
import com.banka.corebank.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;
    private final Random random = new Random();

    public AccountServiceImpl(AccountRepository accountRepository,
            CustomerRepository customerRepository,
            AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional
    public AccountResponse createInitialAccount(Customer customer) {
        return createAccount(customer, AccountType.SAVINGS, BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public AccountResponse createNewAccount(UUID customerId, CreateAccountRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        validateCreationRestriction(customer, request.type());

        return createAccount(customer, request.type(), BigDecimal.ZERO);
    }

    private AccountResponse createAccount(Customer customer, AccountType type, BigDecimal initialBalance) {
        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setType(type);
        account.setBalance(initialBalance);
        account.setCustomer(customer);

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    private void validateCreationRestriction(Customer customer, AccountType type) {
        accountRepository.findFirstByCustomerOrderByCreatedAtDesc(customer).ifPresent(lastAccount -> {
            Instant now = Instant.now();
            Duration duration = Duration.between(lastAccount.getCreatedAt(), now);

            if (type == AccountType.SAVINGS && duration.toDays() < 5) {
                throw new RuntimeException("You must wait 5 days to create another Savings account");
            }

            if (type == AccountType.CHECKING && duration.toHours() < 24) {
                throw new RuntimeException("You must wait 24 hours to create another Checking account");
            }
        });
    }

    private String generateUniqueAccountNumber() {
        String number;
        do {
            number = String.format("%04d", random.nextInt(10000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
