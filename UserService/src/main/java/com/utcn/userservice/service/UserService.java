package com.utcn.userservice.service;

import com.utcn.userservice.dto.UserDTO;
import com.utcn.userservice.dto.builders.UserBuilder;
import com.utcn.userservice.handlers.exceptions.model.BadRequestException;
import com.utcn.userservice.handlers.exceptions.model.ConflictException;
import com.utcn.userservice.handlers.exceptions.model.ResourceNotFoundException;
import com.utcn.userservice.model.User;
import com.utcn.userservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "USER");

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

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        userRepository.deleteById(id);
        LOGGER.debug("User with id {} was deleted from db", id);
    }

    public Long insert(UserDTO userDTO) {
        validateUserForCreation(userDTO);

        User user = UserBuilder.toEntity(userDTO);

        user = userRepository.save(user);

        LOGGER.debug("User with id {} was inserted in db", user.getId());
        return user.getId();
    }

    public UserDTO updateUserEmail(Long id, String newEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} was not found in db", id);
                    return new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
                });

        Optional<User> userWithNewEmail = userRepository.findByEmail(newEmail);
        if (userWithNewEmail.isPresent() && !userWithNewEmail.get().getId().equals(user.getId())) {

            String details = "Email " + newEmail + " is already in use by another account.";
            LOGGER.warn(details);
            throw new ConflictException(details);
        }

        user.setEmail(newEmail);
        User updatedUser = userRepository.save(user);
        LOGGER.debug("User with id {} email was updated by an admin", updatedUser.getId());

        return UserBuilder.toUserDTO(updatedUser);
    }

    private void validateUserForCreation(UserDTO userDTO) {
        userRepository.findByUsername(userDTO.username())
                .ifPresent(u -> {
                    throw new ConflictException("Username '" + userDTO.username() + "' is already taken.");
                });

        userRepository.findByEmail(userDTO.email())
                .ifPresent(u -> {
                    throw new ConflictException("Email '" + userDTO.email() + "' is already in use.");
                });

        if (userDTO.role() == null || !VALID_ROLES.contains(userDTO.role().toUpperCase())) {
            throw new BadRequestException("Role must be one of: " + VALID_ROLES);
        }

        if (userDTO.age() == null || userDTO.age() < 18) {
            throw new BadRequestException("User must be at least 18 years old.");
        }
    }
}