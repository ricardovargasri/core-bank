package com.banka.corebank.config;

import com.banka.corebank.user.entity.User;
import com.banka.corebank.user.enums.UserRole;
import com.banka.corebank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String adminEmail = "admin@corybank.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);

            userRepository.save(admin);
            log.info("Admin user seeded successfully: {}", adminEmail);
        } else {
            log.info("Admin user already exists, skipping seeding.");
        }
    }
}
