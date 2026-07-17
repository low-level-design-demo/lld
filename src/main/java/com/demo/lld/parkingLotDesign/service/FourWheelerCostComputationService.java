package com.demo.lld.parkingLotDesign.service;

import com.demo.lld.parkingLotDesign.model.Ticket;
import com.demo.lld.parkingLotDesign.strategy.HourlyPricingStrategy;

public class FourWheelerCostComputationService extends CostComputationService {

    public FourWheelerCostComputationService() {
        super(new HourlyPricingStrategy());
    }

    @Override
    public double computeCost(Ticket ticket) {
        return pricingStrategy.calculatePrice(ticket);
    }

}
