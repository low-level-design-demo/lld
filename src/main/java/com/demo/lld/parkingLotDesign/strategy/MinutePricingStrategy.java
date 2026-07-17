package com.demo.lld.parkingLotDesign.strategy;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.model.Ticket;

public class MinutePricingStrategy extends PricingStrategy {

    @Override
    public double calculatePrice(Ticket ticket) {
        ParkingSlot parkingSlot = ticket.getParkingSlot();
        double price = parkingSlot.getPrice();
        LocalDateTime issueDateTime = ticket.getIssueDateTime();
        if (issueDateTime == null) {
            return 0;
        }

        long diffInMillis = System.currentTimeMillis()
                - issueDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long min = TimeUnit.MILLISECONDS.toMinutes(Math.max(0, diffInMillis));

        return price * min;
    }
}