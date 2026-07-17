package com.demo.lld.parkingLotDesign.manager;

import java.util.*;

import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.strategy.NearestToEntranceAndElevatorParkingStrategy;
import com.demo.lld.parkingLotDesign.strategy.ParkingStrategy;

public class FourWheelerParkingSlotManager extends ParkingSlotManager {
    List<ParkingSlot> parkingSlots;
    int gateNumber;
    static ParkingStrategy parkingStrategy=new NearestToEntranceAndElevatorParkingStrategy();
    public FourWheelerParkingSlotManager(List<ParkingSlot> parkingSlots, int gateNumber) {
        super(parkingSlots,parkingStrategy,gateNumber);
        this.parkingSlots = parkingSlots;
        this.gateNumber = gateNumber;
    }

    


    

}
