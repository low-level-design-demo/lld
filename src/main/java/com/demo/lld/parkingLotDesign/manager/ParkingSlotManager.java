package com.demo.lld.parkingLotDesign.manager;

import java.util.List;

import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.model.VehicleType;
import com.demo.lld.parkingLotDesign.strategy.ParkingStrategy;

public class ParkingSlotManager {

    List<ParkingSlot> parkingSlots;
    ParkingStrategy parkingStrategy;
    int gateNumber;

    public ParkingSlotManager(List<ParkingSlot> parkingSlots, ParkingStrategy parkingStrategy,int gateNumber) {
        this.parkingSlots = parkingSlots;
        this.parkingStrategy = parkingStrategy;
        this.gateNumber = gateNumber;
    }

    public ParkingSlot findParkingSlot(VehicleType vehicleType) {
        ParkingSlot slot = parkingStrategy.findParkingSlot(parkingSlots,vehicleType,gateNumber);
        
        return slot;
    }

    public void addParkingSlot(ParkingSlot slot) {
        parkingSlots.add(slot);
    }

    public void removeParkingSlot(ParkingSlot slot) {
        parkingSlots.remove(slot);
    }

    public void parkVehicle(ParkingSlot slot) {
        slot.setAvailable(false);
    }

    public void removeVehicle(ParkingSlot slot) {
        slot.setAvailable(true);
    }

}
