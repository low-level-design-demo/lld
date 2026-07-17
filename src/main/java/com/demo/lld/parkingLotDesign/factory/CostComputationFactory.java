package com.demo.lld.parkingLotDesign.factory;

import com.demo.lld.parkingLotDesign.model.VehicleType;
import com.demo.lld.parkingLotDesign.service.CostComputationService;
import com.demo.lld.parkingLotDesign.service.FourWheelerCostComputationService;
import com.demo.lld.parkingLotDesign.service.TwoWheelerCostComputationService;


public class CostComputationFactory {

    public static CostComputationService getCostComputationService(VehicleType vehicleType) {
        if(vehicleType == VehicleType.FOUR_WHEELER){
            return new FourWheelerCostComputationService();
        }
        else if(vehicleType == VehicleType.TWO_WHEELER){
            return new TwoWheelerCostComputationService();
        }
        else{
            throw new IllegalArgumentException("Invalid vehicle type");
        }
    }

}
