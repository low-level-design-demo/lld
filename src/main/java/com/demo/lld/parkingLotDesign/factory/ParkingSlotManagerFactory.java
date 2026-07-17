package com.demo.lld.parkingLotDesign.factory;

import java.util.List;

import com.demo.lld.parkingLotDesign.manager.FourWheelerParkingSlotManager;
import com.demo.lld.parkingLotDesign.manager.ParkingSlotManager;
import com.demo.lld.parkingLotDesign.manager.TwoWheelerParkingSlotManager;
import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.model.VehicleType;

public class ParkingSlotManagerFactory {
    

    public static ParkingSlotManager findVehicle(VehicleType vehicleType,List<ParkingSlot> parkingSlots,int gateNumber) {
        if(vehicleType == VehicleType.FOUR_WHEELER){
            return new FourWheelerParkingSlotManager(parkingSlots,gateNumber);
        }else if(vehicleType == VehicleType.TWO_WHEELER){
            return new TwoWheelerParkingSlotManager(parkingSlots,gateNumber);
        }
        throw new IllegalArgumentException("Invalid vehicle type");
    }

}
