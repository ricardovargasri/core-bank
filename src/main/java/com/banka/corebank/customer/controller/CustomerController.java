package com.banka.corebank.customer.controller;

import com.banka.corebank.customer.dto.request.CreateCustomerRequest;
import com.banka.corebank.customer.dto.response.CustomerResponse;
import com.banka.corebank.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@RequestBody CreateCustomerRequest request) {
        return new ResponseEntity<>(customerService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }
}
