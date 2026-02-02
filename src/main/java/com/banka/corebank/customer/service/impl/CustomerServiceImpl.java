package com.banka.corebank.customer.service.impl;

import com.banka.corebank.customer.dto.request.CreateCustomerRequest;
import com.banka.corebank.customer.dto.response.CustomerResponse;
import com.banka.corebank.customer.entity.Customer;
import com.banka.corebank.customer.mapper.CustomerMapper;
import com.banka.corebank.customer.repository.CustomerRepository;
import com.banka.corebank.customer.service.CustomerService;
import com.banka.corebank.exception.ResourceNotFoundException;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        Customer customerToSave = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customerToSave);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }
}
