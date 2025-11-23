package com.utcn.monitorservice.repo;

import com.utcn.monitorservice.model.SensorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SensorRecordRepository extends JpaRepository<SensorRecord, Long> {
    List<SensorRecord> findByDeviceId(Long deviceId);
}