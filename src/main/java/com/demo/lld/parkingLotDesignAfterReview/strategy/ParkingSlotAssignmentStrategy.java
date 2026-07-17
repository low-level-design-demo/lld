package com.demo.lld.parkingLotDesignAfterReview.strategy;

import java.util.List;

import com.demo.lld.parkingLotDesignAfterReview.model.ParkingSlot;
import com.demo.lld.parkingLotDesignAfterReview.model.VehicleType;

public interface ParkingSlotAssignmentStrategy {
    ParkingSlot findSlot(List<ParkingSlot> slots, VehicleType vehicleType);
}