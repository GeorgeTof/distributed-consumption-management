package com.utcn.authservice.dto;

public record RegisterRequest(
        String username,
        String password,
        String email,
        String role,
        Integer age,
        String town
) {}