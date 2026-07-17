package com.demo.lld.parkingLotDesignAfterReview.strategy;

import java.time.Duration;

import com.demo.lld.parkingLotDesignAfterReview.model.Ticket;

public class FourWheelerCostCalculationStrategy implements CostCalculationStrategy {

    private static final double RATE_PER_HOUR = 40.0;

    @Override
    public double calculateCost(Ticket ticket) {
        if (!ticket.isClosed()) {
            throw new IllegalStateException("Ticket is not closed yet");
        }

        long hours = calculateHours(ticket);
        return hours * RATE_PER_HOUR;
    }

    private long calculateHours(Ticket ticket) {
        long minutes = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes();
        return Math.max(1L, (minutes + 59) / 60);
    }
}