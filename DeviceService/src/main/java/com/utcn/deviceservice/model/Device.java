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

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "max_consumption", nullable = false)
    private Double maximumConsumption;

    public Device() {
    }

    public Device(String name, String brand, Double maximumConsumption) {
        this.name = name;
        this.brand = brand;
        this.maximumConsumption = maximumConsumption;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Double getMaximumConsumption() { return maximumConsumption; }
    public void setMaximumConsumption(Double maximumConsumption) { this.maximumConsumption = maximumConsumption; }
}