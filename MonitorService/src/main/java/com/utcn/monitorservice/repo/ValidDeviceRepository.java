package com.utcn.monitorservice.repo;

import com.utcn.monitorservice.model.ValidDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidDeviceRepository extends JpaRepository<ValidDevice, Long> {
}