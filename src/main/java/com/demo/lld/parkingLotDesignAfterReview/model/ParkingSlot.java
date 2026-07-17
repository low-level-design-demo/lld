package com.demo.lld.parkingLotDesignAfterReview.model;

import java.util.Objects;

public class ParkingSlot {

    private final String id;
    private final VehicleType supportedVehicleType;
    private boolean occupied;

    public ParkingSlot(String id, VehicleType supportedVehicleType) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.supportedVehicleType = Objects.requireNonNull(supportedVehicleType, "supportedVehicleType cannot be null");
    }

    public String getId() {
        return id;
    }

    public VehicleType getSupportedVehicleType() {
        return supportedVehicleType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public boolean isFree() {
        return !occupied;
    }

    public boolean supports(VehicleType vehicleType) {
        return supportedVehicleType == vehicleType;
    }

    public void occupy() {
        if (occupied) {
            throw new IllegalStateException("Parking slot is already occupied: " + id);
        }
        occupied = true;
    }

    public void release() {
        if (!occupied) {
            throw new IllegalStateException("Parking slot is already free: " + id);
        }
        occupied = false;
    }
}
