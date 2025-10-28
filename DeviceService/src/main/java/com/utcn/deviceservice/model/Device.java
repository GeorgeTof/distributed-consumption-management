package com.utcn.deviceservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "device_table")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "owner_username", nullable = false)
    private String ownerUsername;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "max_consumption", nullable = false)
    private Double maximumConsumption;

    @Column(name = "power_consumed", nullable = false)
    private Double powerConsumed;

    public Device() {
    }


    public Device(String name, String ownerUsername, String brand, Double maximumConsumption) {
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.brand = brand;
        this.maximumConsumption = maximumConsumption;
        this.powerConsumed = 0.0;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Double getMaximumConsumption() { return maximumConsumption; }
    public void setMaximumConsumption(Double maximumConsumption) { this.maximumConsumption = maximumConsumption; }

    public Double getPowerConsumed() { return powerConsumed; }
    public void setPowerConsumed(Double powerConsumed) { this.powerConsumed = powerConsumed; }
}