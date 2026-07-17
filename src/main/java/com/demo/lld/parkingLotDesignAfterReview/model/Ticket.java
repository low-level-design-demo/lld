package com.demo.lld.parkingLotDesignAfterReview.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {

    public enum TicketStatus {
        ACTIVE,
        CLOSED
    }

    private final String id;
    private final Vehicle vehicle;
    private final String parkingSlotId;
    private final int gateNumber;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private TicketStatus status;

    public Ticket(String id, Vehicle vehicle, String parkingSlotId, int gateNumber, LocalDateTime entryTime) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle cannot be null");
        this.parkingSlotId = Objects.requireNonNull(parkingSlotId, "parkingSlotId cannot be null");
        this.gateNumber = gateNumber;
        this.entryTime = Objects.requireNonNull(entryTime, "entryTime cannot be null");
        this.status = TicketStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getParkingSlotId() {
        return parkingSlotId;
    }

    public int getGateNumber() {
        return gateNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public boolean isClosed() {
        return status == TicketStatus.CLOSED;
    }

    public void close(LocalDateTime exitTime) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Ticket is already closed");
        }
        this.exitTime = Objects.requireNonNull(exitTime, "exitTime cannot be null");
        this.status = TicketStatus.CLOSED;
    }
}