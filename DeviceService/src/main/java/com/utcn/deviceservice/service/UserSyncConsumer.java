package com.utcn.deviceservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.deviceservice.model.ValidUser;
import com.utcn.deviceservice.repo.DeviceRepository;
import com.utcn.deviceservice.repo.ValidUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserSyncConsumer {

    private final ValidUserRepository validUserRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserSyncConsumer(ValidUserRepository validUserRepository, DeviceRepository deviceRepository) {
        this.validUserRepository = validUserRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    @RabbitListener(queues = "device.user.sync.queue")
    public void receiveUserEvent(String jsonMessage) {
        try {
            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);
            String eventType = (String) message.get("eventType");
            String username = (String) message.get("username");

            if ("USER_CREATED".equals(eventType)) {
                if (!validUserRepository.existsById(username)) {
                    validUserRepository.save(new ValidUser(username));
                    System.out.println(">>> Synced: Added Valid User " + username);
                }
            }
            else if ("USER_DELETED".equals(eventType)) {
                if (validUserRepository.existsById(username)) {
                    validUserRepository.deleteById(username);
                    System.out.println(">>> Synced: Removed Valid User " + username);
                }

                deviceRepository.deleteByOwnerUsername(username);
                System.out.println(">>> Synced: Deleted all devices for User " + username);
            }

        } catch (Exception e) {
            System.err.println("Failed to sync user event: " + jsonMessage);
            e.printStackTrace();
        }
    }
}