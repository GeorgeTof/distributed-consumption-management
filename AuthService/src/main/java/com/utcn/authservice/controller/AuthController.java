package com.utcn.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.authservice.config.RabbitConfig;
import com.utcn.authservice.dto.RegisterRequest;
import com.utcn.authservice.model.User;
import com.utcn.authservice.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // SECRET - match with traefik config
    private static final String SECRET = "supersecret123456supersecret123456";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${internal.exchange.name}")
    private String internalExchange;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        var key = Keys.hmacShaKeyFor(SECRET.getBytes());
        List<String> roles = user.getRoles();

        String token = Jwts.builder()
                .subject(user.getUsername())
                .claim("role", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
                .signWith(key)
                .compact();

        return Map.of(
                "token", token,
                "user", user.getUsername(),
                "roles", String.join(",", roles)
        );
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signup(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        return ResponseEntity.badRequest().body("Deprecated. Please use /auth/register");
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.password() == null || request.password().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters long"));
        }

        if (request.age() == null || request.age() < 18) {
            return ResponseEntity.badRequest().body(Map.of("error", "User must be at least 18 years old."));
        }

        String role = request.role().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("USER")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username '" + request.username() + "' is already taken."));
        }

        User newUser = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                role.equals("USER") ? List.of("ROLE_USER") : List.of("ROLE_USER", "ROLE_ADMIN")
        );
        userRepository.save(newUser);

        try {
            Map<String, Object> eventMessage = new HashMap<>();
            eventMessage.put("eventType", "USER_CREATED");
            eventMessage.put("username", request.username());
            eventMessage.put("email", request.email());
            eventMessage.put("role", role);
            eventMessage.put("age", request.age());
            eventMessage.put("town", request.town());
            eventMessage.put("registerDate", new Date().toString());

            String jsonPayload = objectMapper.writeValueAsString(eventMessage);

            rabbitTemplate.convertAndSend(internalExchange, "user.created", jsonPayload);

            System.out.println(">>> Published User Created Event: " + request.username());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "User saved but failed to sync profile."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
    }

    @DeleteMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found: " + username));
        }

        userRepository.delete(userOptional.get());

        try {
            Map<String, Object> eventMessage = new HashMap<>();
            eventMessage.put("eventType", "USER_DELETED");
            eventMessage.put("username", username);

            String jsonPayload = objectMapper.writeValueAsString(eventMessage);

            rabbitTemplate.convertAndSend(internalExchange, "user.deleted", jsonPayload);

            System.out.println(">>> Published User Deleted Event: " + username);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.noContent().build();
    }
}