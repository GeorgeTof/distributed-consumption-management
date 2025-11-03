package com.utcn.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_table")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "town")
    private String town;

    @CreationTimestamp
    @Column(name = "register_date", nullable = false, updatable = false)
    private LocalDateTime registerDate;

    public User() {
    }

    public User(String username, String email, String role, Integer age, String town) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.age = age;
        this.town = town;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getTown() { return town; }
    public void setTown(String town) { this.town = town; }
    public LocalDateTime getRegisterDate() { return registerDate; }
    public void setRegisterDate(LocalDateTime registerDate) { this.registerDate = registerDate; }
}