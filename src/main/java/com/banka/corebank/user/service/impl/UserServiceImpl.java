package com.banka.corebank.user.service.impl;

import com.banka.corebank.user.repository.UserRepository;
import com.banka.corebank.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
