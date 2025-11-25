package com.utcn.deviceservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "valid_users")
public class ValidUser {

    @Id
    private String username;

    public ValidUser() {}

    public ValidUser(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}