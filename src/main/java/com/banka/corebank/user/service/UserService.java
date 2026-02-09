package com.banka.corebank.user.service;

import com.banka.corebank.user.dto.request.LoginRequest;
import com.banka.corebank.user.dto.request.RegisterRequest;
import com.banka.corebank.user.dto.response.AuthResponse;
import java.util.UUID;

public interface UserService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
