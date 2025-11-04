package com.utcn.authservice.controller;

import com.utcn.authservice.model.User;
import com.utcn.authservice.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // SECRET - match with traefik config
    private static final String SECRET = "supersecret123456supersecret123456";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        role = role.toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("USER")) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid role: " + role));
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Password cannot be empty"));
        }

        if (password.length() < 6) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Password must be at least 6 characters long"));
        }

        User newUser = new User(
                username,
                passwordEncoder.encode(password),     // Always hash the password
                role.equals("USER") ? List.of("ROLE_USER") : List.of("ROLE_USER", "ROLE_ADMIN")
        );

        userRepository.save(newUser);

        Map<String, String> responseBody = Map.of(
                "user", newUser.getUsername(),
                "roles", String.join(",", newUser.getRoles())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
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

        return ResponseEntity.noContent().build();
    }
}