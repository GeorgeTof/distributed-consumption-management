package com.utcn.userservice.dto;
import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String username,
        Integer age,
        String town,
        LocalDateTime registerDate
) {

}
