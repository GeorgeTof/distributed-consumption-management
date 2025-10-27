package com.utcn.authservice.controller;

import com.utcn.authservice.model.User;
import com.utcn.authservice.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // This MUST be the same as in traefik
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
    public Map<String, String> signup(@RequestParam String username, @RequestParam String password) {

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username cannot be empty");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            // Alternative 409 Conflict for HTTP status response
            throw new RuntimeException("Username already taken");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password cannot be empty");
        }

        User newUser = new User(
                username,
                passwordEncoder.encode(password), // Always hash the password
                List.of("ROLE_USER")              // New users are regular users
        );

        userRepository.save(newUser);

        var key = Keys.hmacShaKeyFor(SECRET.getBytes());

        String token = Jwts.builder()
                .subject(newUser.getUsername())
                .claim("role", newUser.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
                .signWith(key)
                .compact();

        return Map.of(
                "token", token,
                "user", newUser.getUsername(),
                "roles", String.join(",", newUser.getRoles())
        );
    }
}