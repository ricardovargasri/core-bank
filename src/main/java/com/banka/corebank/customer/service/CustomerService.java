package com.banka.corebank.customer.service;

import com.banka.corebank.customer.dto.request.CreateCustomerRequest;
import com.banka.corebank.customer.dto.response.CustomerResponse;

import java.util.UUID;

public interface CustomerService {

    CustomerResponse create(CreateCustomerRequest request);

    CustomerResponse findById(UUID id);
}
