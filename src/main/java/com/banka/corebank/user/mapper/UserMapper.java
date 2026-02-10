package com.banka.corebank.user.mapper;

import com.banka.corebank.user.dto.request.RegisterRequest;
import com.banka.corebank.user.dto.response.AuthResponse;
import com.banka.corebank.user.entity.User;
import com.banka.corebank.user.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Maps RegisterRequest to User entity.
     * Note: password encryption is handled in the service layer, not here.
     */
    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setRole(UserRole.USER); // Default role for new registrations
        return user;
    }

    /**
     * Maps User entity to AuthResponse.
     * Token is usually passed separately or injected by the service.
     */
    public AuthResponse toResponse(User user, String token) {
        String firstName = "Usuario";
        if (user.getCustomer() != null && user.getCustomer().getName() != null) {
            firstName = user.getCustomer().getName().split(" ")[0];
        }

        return new AuthResponse(
                token,
                "fake-refresh-token", // Placeholder
                user.getEmail(),
                firstName,
                user.getRole());
    }
}
