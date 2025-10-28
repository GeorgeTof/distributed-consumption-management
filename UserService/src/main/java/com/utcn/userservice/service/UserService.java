package com.utcn.userservice.service;

import com.utcn.userservice.dto.UserDTO;
import com.utcn.userservice.dto.builders.UserBuilder;
import com.utcn.userservice.model.User;
import com.utcn.userservice.repo.UserRepository;
import com.utcn.userservice.handlers.exceptions.model.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();

        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            LOGGER.error("User with username {} was not found in db", username);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + username);
        }

        return UserBuilder.toUserDTO(userOptional.get());
    }

    public UserDTO findUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        return UserBuilder.toUserDTO(userOptional.get());
    }

    // 3. CREATE (insert equivalent) TODO - probabil remove, e ok fara parola aici
    // NOTE: We need a DTO that includes the password for creation,
    // so this method should ideally receive a UserCreationDTO or UserDetailsDTO.
    // For now, we'll use UserDTO and handle the conversion.
    public Long insert(UserDTO userDTO) {
        // Map DTO to Entity
        User user = UserBuilder.toEntity(userDTO);

        // Persistence via Repository
        user = userRepository.save(user);

        LOGGER.debug("User with id {} was inserted in db", user.getId());
        return user.getId();
    }
}