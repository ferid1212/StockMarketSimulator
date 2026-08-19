package com.example.stock.config;

import com.example.stock.entity.Basket;
import com.example.stock.entity.User;
import com.example.stock.enums.Role;
import com.example.stock.repository.UserRepository;
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
    public void run(String... args) throws Exception {
        String adminEmail = "admin@gmail.com";
        String adminRawPassword = "admin123";

        if (!userRepository.existsByEmail(adminEmail) && !userRepository.existsByRole(Role.ROLE_ADMIN)) {
            User admin = User.builder()
                    .name("Admin")
                    .surname("System")
                    .age(30)
                    .phone("+994500000000")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminRawPassword))
                    .role(Role.ROLE_ADMIN)
                    .balance(5000.0)
                    .build();

            Basket basket = new Basket();
            basket.setUser(admin);
            admin.setBasket(basket);

            userRepository.save(admin);

            log.info("\n=================================================================\n" +
                     "  SYSTEM ADMIN INITIALIZED SUCCESSFULLY UPON APPLICATION RUN!\n" +
                     "  Admin Gmail / Email: {}\n" +
                     "  Admin Code / Password: {}\n" +
                     "  Role: ROLE_ADMIN\n" +
                     "=================================================================",
                    adminEmail, adminRawPassword);
        } else {
            log.info("\n=================================================================\n" +
                     "  ADMIN USER ALREADY EXISTS IN DATABASE:\n" +
                     "  Admin Gmail / Email: {}\n" +
                     "  Admin Code / Password: {}\n" +
                     "=================================================================",
                    adminEmail, adminRawPassword);
        }
    }
}
