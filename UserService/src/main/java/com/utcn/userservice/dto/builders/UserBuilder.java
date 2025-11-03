package com.utcn.userservice.dto.builders;

import com.utcn.userservice.dto.UserDTO;
import com.utcn.userservice.model.User;

public class UserBuilder {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getAge(),
                user.getTown(),
                user.getRegisterDate()
        );
    }

    public static User toEntity(UserDTO userDTO) {
        return new User(
                userDTO.username(),
                userDTO.email(),
                userDTO.role(),
                userDTO.age(),
                userDTO.town()
        );
    }
}