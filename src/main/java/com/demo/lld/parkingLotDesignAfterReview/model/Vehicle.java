package com.demo.lld.parkingLotDesignAfterReview.model;

import java.util.Objects;

public final class Vehicle {

    private final String licenseNumber;
    private final VehicleType vehicleType;

    public Vehicle(String licenseNumber, VehicleType vehicleType) {
        this.licenseNumber = Objects.requireNonNull(licenseNumber, "licenseNumber cannot be null");
        this.vehicleType = Objects.requireNonNull(vehicleType, "vehicleType cannot be null");
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}