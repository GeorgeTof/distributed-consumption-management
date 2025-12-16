package com.utcn.devicedatasimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeviceDataSimulatorApplication {

    public static void main(String[] args) {
        int basePort = 8090;
        int deviceIndex = 0;

        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith("--device.index=")) {
                    try {
                        String value = arg.split("=")[1];
                        deviceIndex = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Could not parse device index, defaulting to 0.");
                    }
                }
            }
        }

        int dynamicPort = basePort + deviceIndex;

        System.setProperty("server.port", String.valueOf(dynamicPort));

        System.out.println("\n=================================================");
        System.out.println("   DYNAMIC CONFIGURATION");
        System.out.println("   Device Index: " + deviceIndex);
        System.out.println("   Server Port : " + dynamicPort);
        System.out.println("=================================================\n");

        SpringApplication.run(DeviceDataSimulatorApplication.class, args);
    }
}