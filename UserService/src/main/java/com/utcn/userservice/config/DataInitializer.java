package com.utcn.userservice.config;

import com.utcn.userservice.model.User;
import com.utcn.userservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User(
                        "admin",
                        "admin@example.com",
                        "ADMIN",
                        99,
                        "Server Room"
                );
                userRepository.save(admin);
                LOGGER.info("Created ADMIN user in user-service database");
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User(
                        "user",
                        "user@example.com",
                        "USER",
                        99,
                        "Cluj-Napoca"
                );
                userRepository.save(user);
                LOGGER.info("Created USER user in user-service database");
            }
        };
    }
}