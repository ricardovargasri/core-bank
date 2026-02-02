package com.banka.corebank.customer.service.impl;

import com.banka.corebank.customer.entity.Customer;
import com.banka.corebank.customer.repository.CustomerRepository;
import com.banka.corebank.customer.service.CustomerService;
import com.banka.corebank.exception.ResourceNotFoundException;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
