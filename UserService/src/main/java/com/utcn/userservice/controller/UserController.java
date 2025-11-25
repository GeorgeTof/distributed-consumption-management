package com.utcn.userservice.controller;

import com.utcn.userservice.dto.UpdateEmailRequestDTO;
import com.utcn.userservice.dto.UserDTO;
import com.utcn.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest req) {
        String username = req.getHeader("X-User");
        UserDTO user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * @deprecated This endpoint bypasses the Auth Service and RabbitMQ sync.
     * Please use POST /auth/register in the Auth Service instead.
     */
    @Deprecated
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody UserDTO userDTO) {

        Long id = userService.insert(userDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        // Return 201 Created with warning body
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", "success");
        response.put("warning", "DEPRECATED: This endpoint will be removed. Please use POST /auth/register via Auth Service for correct synchronization.");

        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateEmail(@PathVariable Long id, @Valid @RequestBody UpdateEmailRequestDTO emailDTO) {
        UserDTO updatedUser = userService.updateUserEmail(id, emailDTO.newEmail());
        return ResponseEntity.ok(updatedUser);
    }
}