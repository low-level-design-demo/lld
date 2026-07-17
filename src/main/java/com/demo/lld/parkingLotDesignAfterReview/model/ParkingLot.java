package com.demo.lld.parkingLotDesignAfterReview.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.demo.lld.parkingLotDesignAfterReview.exception.NoSlotAvailableException;
import com.demo.lld.parkingLotDesignAfterReview.strategy.ParkingSlotAssignmentStrategy;

public class ParkingLot {

    private final String id;
    private final Map<String, ParkingSlot> slotsById;
    private final ParkingSlotAssignmentStrategy assignmentStrategy;

    public ParkingLot(String id, List<ParkingSlot> slots, ParkingSlotAssignmentStrategy assignmentStrategy) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.assignmentStrategy = Objects.requireNonNull(assignmentStrategy, "assignmentStrategy cannot be null");

        Objects.requireNonNull(slots, "slots cannot be null");
        this.slotsById = new LinkedHashMap<>();
        for (ParkingSlot slot : slots) {
            this.slotsById.put(slot.getId(), slot);
        }
    }

    public String getId() {
        return id;
    }

    public ParkingSlot assignSlot(VehicleType vehicleType) {
        List<ParkingSlot> slotList = new ArrayList<>(slotsById.values());

        ParkingSlot slot = assignmentStrategy.findSlot(slotList, vehicleType);
        if (slot == null) {
            throw new NoSlotAvailableException("No free parking slot available for vehicle type: " + vehicleType);
        }

        slot.occupy();
        return slot;
    }

    public void releaseSlot(String slotId) {
        ParkingSlot slot = findSlot(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Parking slot not found: " + slotId));
        slot.release();
    }

    public Optional<ParkingSlot> findSlot(String slotId) {
        return Optional.ofNullable(slotsById.get(slotId));
    }
}