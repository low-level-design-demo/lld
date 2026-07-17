package com.demo.lld.parkingLotDesignAfterReview.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.demo.lld.parkingLotDesignAfterReview.model.ParkingLot;
import com.demo.lld.parkingLotDesignAfterReview.model.ParkingSlot;
import com.demo.lld.parkingLotDesignAfterReview.model.Ticket;
import com.demo.lld.parkingLotDesignAfterReview.model.Vehicle;

public class EntryGateService {

    private final ParkingLot parkingLot;

    public EntryGateService(ParkingLot parkingLot) {
        this.parkingLot = Objects.requireNonNull(parkingLot, "parkingLot cannot be null");
    }

    public Ticket issueTicket(Vehicle vehicle, int gateNumber) {
        Objects.requireNonNull(vehicle, "vehicle cannot be null");
        if (gateNumber <= 0) {
            throw new IllegalArgumentException("gateNumber must be positive");
        }

        ParkingSlot assignedSlot = parkingLot.assignSlot(vehicle.getVehicleType());

        return new Ticket(
                UUID.randomUUID().toString(),
                vehicle,
                assignedSlot.getId(),
                gateNumber,
                LocalDateTime.now()
        );
    }
}