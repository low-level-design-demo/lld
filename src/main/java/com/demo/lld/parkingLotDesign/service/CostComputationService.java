package com.demo.lld.parkingLotDesign.service;

import com.demo.lld.parkingLotDesign.model.Ticket;
import com.demo.lld.parkingLotDesign.strategy.PricingStrategy;

public abstract class CostComputationService {
    protected PricingStrategy pricingStrategy;

    public CostComputationService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public abstract double computeCost(Ticket ticket);

}
