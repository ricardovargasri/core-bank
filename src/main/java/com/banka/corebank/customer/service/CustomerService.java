package com.banka.corebank.customer.service;

import com.banka.corebank.customer.entity.Customer;

import java.util.UUID;

public interface CustomerService {

    Customer create(Customer customer);

    Customer findById(UUID id);
}

