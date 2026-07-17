package com.demo.lld.parkingLotDesignAfterReview.strategy;

import com.demo.lld.parkingLotDesignAfterReview.model.Ticket;

public interface CostCalculationStrategy {
    double calculateCost(Ticket ticket);
}