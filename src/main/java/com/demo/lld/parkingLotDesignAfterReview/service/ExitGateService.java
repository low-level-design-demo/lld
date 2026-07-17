package com.demo.lld.parkingLotDesignAfterReview.service;

import java.time.LocalDateTime;
import java.util.Objects;

import com.demo.lld.parkingLotDesignAfterReview.model.ParkingLot;
import com.demo.lld.parkingLotDesignAfterReview.model.Ticket;

public class ExitGateService {

    private final BillingService billingService;
    private final ParkingLot parkingLot;

    public ExitGateService(BillingService billingService, ParkingLot parkingLot) {
        this.billingService = Objects.requireNonNull(billingService, "billingService cannot be null");
        this.parkingLot = Objects.requireNonNull(parkingLot, "parkingLot cannot be null");
    }

    public double processExit(Ticket ticket, LocalDateTime exitTime) {
        Objects.requireNonNull(ticket, "ticket cannot be null");
        Objects.requireNonNull(exitTime, "exitTime cannot be null");

        if (ticket.isClosed()) {
            throw new IllegalStateException("Ticket is already closed");
        }

        ticket.close(exitTime);
        double amount = billingService.calculateCost(ticket);
        parkingLot.releaseSlot(ticket.getParkingSlotId());

        return amount;
    }
}