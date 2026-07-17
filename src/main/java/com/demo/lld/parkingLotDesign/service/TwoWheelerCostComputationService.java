package com.demo.lld.parkingLotDesign.service;

import com.demo.lld.parkingLotDesign.model.Ticket;
import com.demo.lld.parkingLotDesign.strategy.MinutePricingStrategy;

public class TwoWheelerCostComputationService extends CostComputationService {

    public TwoWheelerCostComputationService() {
        super(new MinutePricingStrategy());
    }

    @Override
    public double computeCost(Ticket ticket) {
        return pricingStrategy.calculatePrice(ticket);
    }

}
