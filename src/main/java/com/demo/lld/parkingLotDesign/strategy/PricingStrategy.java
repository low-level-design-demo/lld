package com.demo.lld.parkingLotDesign.strategy;

import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.model.Ticket;

public class PricingStrategy {

    public double calculatePrice(Ticket ticket){
        ParkingSlot parkingSlot = ticket.getParkingSlot();
        return parkingSlot.getPrice();
    }

}
