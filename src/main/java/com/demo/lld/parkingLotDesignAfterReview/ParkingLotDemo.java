package com.demo.lld.parkingLotDesignAfterReview;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import com.demo.lld.parkingLotDesignAfterReview.model.ParkingLot;
import com.demo.lld.parkingLotDesignAfterReview.model.ParkingSlot;
import com.demo.lld.parkingLotDesignAfterReview.model.Ticket;
import com.demo.lld.parkingLotDesignAfterReview.model.Vehicle;
import com.demo.lld.parkingLotDesignAfterReview.model.VehicleType;
import com.demo.lld.parkingLotDesignAfterReview.service.BillingService;
import com.demo.lld.parkingLotDesignAfterReview.service.EntryGateService;
import com.demo.lld.parkingLotDesignAfterReview.service.ExitGateService;
import com.demo.lld.parkingLotDesignAfterReview.strategy.CostCalculationStrategy;
import com.demo.lld.parkingLotDesignAfterReview.strategy.FirstAvailableParkingSlotAssignmentStrategy;
import com.demo.lld.parkingLotDesignAfterReview.strategy.FourWheelerCostCalculationStrategy;
import com.demo.lld.parkingLotDesignAfterReview.strategy.TwoWheelerCostCalculationStrategy;

public class ParkingLotDemo {

    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(
                "PL-001",
                Arrays.asList(
                        new ParkingSlot("A1", VehicleType.FOUR_WHEELER),
                        new ParkingSlot("A2", VehicleType.FOUR_WHEELER),
                        new ParkingSlot("B1", VehicleType.TWO_WHEELER),
                        new ParkingSlot("B2", VehicleType.TWO_WHEELER)
                ),
                new FirstAvailableParkingSlotAssignmentStrategy()
        );

        Map<VehicleType, CostCalculationStrategy> strategies = new EnumMap<>(VehicleType.class);
        strategies.put(VehicleType.TWO_WHEELER, new TwoWheelerCostCalculationStrategy());
        strategies.put(VehicleType.FOUR_WHEELER, new FourWheelerCostCalculationStrategy());

        BillingService billingService = new BillingService(strategies);
        EntryGateService entryGateService = new EntryGateService(parkingLot);
        ExitGateService exitGateService = new ExitGateService(billingService, parkingLot);

        Vehicle vehicle = new Vehicle("KA01AB1234", VehicleType.FOUR_WHEELER);
        Ticket ticket = entryGateService.issueTicket(vehicle, 1);

        double amount = exitGateService.processExit(ticket, LocalDateTime.now().plusHours(3));
        System.out.println("Amount to pay: " + amount);
    }
}