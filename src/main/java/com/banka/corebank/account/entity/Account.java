package com.banka.corebank.account.entity;

import com.banka.corebank.account.enums.AccountType;
import com.banka.corebank.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean active = true;

    @UpdateTimestamp
    private Instant updatedAt;
}
