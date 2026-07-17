package com.demo.lld.parkingLotDesignAfterReview.service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import com.demo.lld.parkingLotDesignAfterReview.model.Ticket;
import com.demo.lld.parkingLotDesignAfterReview.model.VehicleType;
import com.demo.lld.parkingLotDesignAfterReview.strategy.CostCalculationStrategy;

public class BillingService {

    private final Map<VehicleType, CostCalculationStrategy> strategies;

    public BillingService(Map<VehicleType, CostCalculationStrategy> strategies) {
        Objects.requireNonNull(strategies, "strategies cannot be null");

        this.strategies = new EnumMap<>(VehicleType.class);
        this.strategies.putAll(strategies);
    }

    public double calculateCost(Ticket ticket) {
        Objects.requireNonNull(ticket, "ticket cannot be null");

        CostCalculationStrategy strategy = strategies.get(ticket.getVehicle().getVehicleType());
        if (strategy == null) {
            throw new IllegalArgumentException("No billing strategy configured for vehicle type: " + ticket.getVehicle().getVehicleType());
        }

        return strategy.calculateCost(ticket);
    }
}