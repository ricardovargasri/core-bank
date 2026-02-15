package com.banka.corebank.user.service.impl;

import com.banka.corebank.config.JwtService;
import com.banka.corebank.customer.dto.request.CreateCustomerRequest;
import com.banka.corebank.customer.dto.response.CustomerResponse;
import com.banka.corebank.customer.entity.Customer;
import com.banka.corebank.customer.repository.CustomerRepository;
import com.banka.corebank.customer.service.CustomerService;
import com.banka.corebank.exception.ResourceNotFoundException;
import com.banka.corebank.user.dto.request.LoginRequest;
import com.banka.corebank.user.dto.request.RegisterRequest;
import com.banka.corebank.user.dto.response.AuthResponse;
import com.banka.corebank.user.entity.User;
import com.banka.corebank.user.mapper.UserMapper;
import com.banka.corebank.user.repository.UserRepository;
import com.banka.corebank.user.service.UserService;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final CustomerRepository customerRepository;
        private final CustomerService customerService;
        private final UserMapper userMapper;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager; // Added

        public UserServiceImpl(UserRepository userRepository,
                        CustomerRepository customerRepository,
                        CustomerService customerService,
                        UserMapper userMapper,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        AuthenticationManager authenticationManager) { // Added
                this.userRepository = userRepository;
                this.customerRepository = customerRepository;
                this.customerService = customerService;
                this.userMapper = userMapper;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.authenticationManager = authenticationManager;
        }

        @Override
        public AuthResponse login(LoginRequest request) {
                // This will authenticate the user or throw an exception if credentials are
                // wrong
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.email(),
                                                request.password()));

                // If we are here, authentication was successful
                User user = userRepository.findByEmail(request.email())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                String accessToken = jwtService.generateToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);
                return userMapper.toResponse(user, accessToken, refreshToken);
        }

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                // 0. Manual validation for early failure
                if (request.email() == null || request.email().isBlank() ||
                                request.password() == null || request.password().isBlank() ||
                                request.firstName() == null || request.firstName().isBlank() ||
                                request.lastName() == null || request.lastName().isBlank() ||
                                request.documentId() == null || request.documentId().isBlank()) {
                        throw new com.banka.corebank.exception.BusinessException(
                                        "All registration fields are required and cannot be empty.");
                }

                // 1. Map DTO to User entity
                User user = userMapper.toEntity(request);

                // 2. Real Encryption using BCrypt
                user.setPassword(passwordEncoder.encode(request.password()));

                // 3. Create Customer (Business Data)
                String fullName = request.firstName() + " " + request.lastName();

                CreateCustomerRequest customerRequest = new CreateCustomerRequest(
                                fullName,
                                request.documentId(),
                                request.email());

                CustomerResponse customerResponse = customerService.create(customerRequest);

                // 4. Link User to Customer
                // We fetch the saved customer entity using the ID from the response
                Customer customer = customerRepository.findById(customerResponse.id())
                                .orElseThrow(() -> new ResourceNotFoundException("Customer not found after creation"));

                user.setCustomer(customer);

                User savedUser = userRepository.save(user);

                // Generation of real JWT tokens
                String accessToken = jwtService.generateToken(savedUser);
                String refreshToken = jwtService.generateRefreshToken(savedUser);

                return userMapper.toResponse(savedUser, accessToken, refreshToken);
        }

        @Override
        @Transactional
        public void deactivateUser(UUID userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                user.setActive(false);
                userRepository.save(user);
        }
}