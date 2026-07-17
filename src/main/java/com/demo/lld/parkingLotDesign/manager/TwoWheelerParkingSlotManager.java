package com.demo.lld.parkingLotDesign.manager;

import java.util.*;

import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.strategy.NearestToEntranceParkingStrategy;
import com.demo.lld.parkingLotDesign.strategy.ParkingStrategy;

public class TwoWheelerParkingSlotManager extends ParkingSlotManager {
    
    static ParkingStrategy parkingStrategy = new NearestToEntranceParkingStrategy();

    public TwoWheelerParkingSlotManager(List<ParkingSlot> parkingSlots,int gateNumber) {
        super(parkingSlots, parkingStrategy,gateNumber);
      
    }

}
