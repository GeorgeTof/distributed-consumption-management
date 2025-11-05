package com.utcn.deviceservice.controller;

import com.utcn.deviceservice.dto.DeviceDTO;
import com.utcn.deviceservice.service.DeviceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        List<DeviceDTO> devices = deviceService.findDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable Long id) {
        DeviceDTO device = deviceService.findDeviceById(id);
        return ResponseEntity.ok(device);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUser(@PathVariable String username) {
        List<DeviceDTO> devices = deviceService.findDeviceByUsername(username);
        return ResponseEntity.ok(devices);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<DeviceDTO>> getOwnDevices(HttpServletRequest req) {
        String username = req.getHeader("X-User");
        List<DeviceDTO> devices = deviceService.findDeviceByUsername(username);
        return ResponseEntity.ok(devices);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody DeviceDTO deviceDTO) {
        Long id = deviceService.insert(deviceDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/id/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}/consumption")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDTO> updatePowerConsumed(
            @PathVariable Long id,
            @RequestParam Double powerConsumed) {

        DeviceDTO updatedDevice = deviceService.updateDeviceConsumption(id, powerConsumed);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDevicesByUser(@PathVariable String username) {
        deviceService.deleteDevicesByUsername(username);
        return ResponseEntity.noContent().build();
    }
}