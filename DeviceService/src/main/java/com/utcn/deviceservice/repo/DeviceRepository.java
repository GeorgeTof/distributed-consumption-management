package com.utcn.deviceservice.repo;

import com.utcn.deviceservice.model.Device;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByOwnerUsername(String ownerUsername);

    @Transactional
    void deleteByOwnerUsername(String ownerUsername);
}