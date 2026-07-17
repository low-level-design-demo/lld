package com.demo.lld.parkingLotDesign.strategy;

import java.util.List;

import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.model.VehicleType;

public abstract class ParkingStrategy {

    public abstract ParkingSlot findParkingSlot(List<ParkingSlot> parkingSlots, VehicleType vehicleType,int gateNumber);
}
