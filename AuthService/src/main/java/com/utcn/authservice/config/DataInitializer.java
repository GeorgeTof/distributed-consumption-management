package com.utcn.authservice.config;

import com.utcn.authservice.model.User;
import com.utcn.authservice.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User(
                        "admin",
                        passwordEncoder.encode("adminpass"),
                        List.of("ROLE_ADMIN", "ROLE_USER")
                );
                userRepository.save(admin);
                System.out.println("Created ADMIN user");
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User(
                        "user",
                        passwordEncoder.encode("userpass"),
                        List.of("ROLE_USER")
                );
                userRepository.save(user);
                System.out.println("Created USER user");
            }
        };
    }
}