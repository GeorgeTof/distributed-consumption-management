package com.utcn.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDTO(
        @Email(message = "Email format is not valid")
        @NotBlank(message = "Email must not be blank")
        String newEmail
) {
}