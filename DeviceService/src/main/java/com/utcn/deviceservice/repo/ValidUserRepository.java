package com.utcn.deviceservice.repo;

import com.utcn.deviceservice.model.ValidUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidUserRepository extends JpaRepository<ValidUser, String> {
}