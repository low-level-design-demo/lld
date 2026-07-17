package com.demo.lld.parkingLotDesignAfterReview.strategy;

import java.util.List;

import com.demo.lld.parkingLotDesignAfterReview.model.ParkingSlot;
import com.demo.lld.parkingLotDesignAfterReview.model.VehicleType;

public class FirstAvailableParkingSlotAssignmentStrategy implements ParkingSlotAssignmentStrategy {

    @Override
    public ParkingSlot findSlot(List<ParkingSlot> slots, VehicleType vehicleType) {
        for (ParkingSlot slot : slots) {
            if (slot.isFree() && slot.supports(vehicleType)) {
                return slot;
            }
        }
        return null;
    }
}