package com.utcn.monitorservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sensor_records")
public class SensorRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;
    private Double measurement;

    private int year;
    private int month;
    private int day;
    private int hour;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Double getMeasurement() { return measurement; }
    public void setMeasurement(Double measurement) { this.measurement = measurement; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }
}