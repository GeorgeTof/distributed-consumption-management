package com.utcn.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.userservice.model.User;
import com.utcn.userservice.repo.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserSyncConsumer {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserSyncConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "${internal.queue.name}")
    public void receiveUserEvent(String jsonMessage) {
        try {
            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);
            String eventType = (String) message.get("eventType");

            if ("USER_CREATED".equals(eventType)) {
                syncUserCreation(message);
            }
            else if ("USER_DELETED".equals(eventType)) {
                syncUserDeletion(message);
            }

        } catch (Exception e) {
            System.err.println("Failed to sync user event: " + jsonMessage);
            e.printStackTrace();
        }
    }

    private void syncUserCreation(Map<String, Object> message) {
        String username = (String) message.get("username");

        if (userRepository.findByUsername(username).isPresent()) {
            System.out.println("SYNCHRONIZATION ISSUE\nUser " + username + " already exists. Skipping sync.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail((String) message.get("email"));
        user.setRole((String) message.get("role"));
        user.setTown((String) message.get("town"));

        Object ageObj = message.get("age");
        if (ageObj != null) {
            user.setAge(Integer.parseInt(ageObj.toString()));
        }

        user.setRegisterDate(LocalDateTime.now());

        userRepository.save(user);
        System.out.println(">>> Synced: Created User " + username + " in User DB.");
    }

    private void syncUserDeletion(Map<String, Object> message) {
        String username = (String) message.get("username");

        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            userRepository.delete(user);
            System.out.println(">>> Synced: Deleted User " + username + " from User DB.");
        }, () -> {
            System.out.println(">>> Synced: User " + username + " not found in User DB. Ignoring delete.");
        });
    }
}