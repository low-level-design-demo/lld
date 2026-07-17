package com.demo.lld.parkingLotDesign.service;

import java.util.List;

import com.demo.lld.parkingLotDesign.factory.CostComputationFactory;
import com.demo.lld.parkingLotDesign.factory.ParkingSlotManagerFactory;
import com.demo.lld.parkingLotDesign.manager.ParkingSlotManager;
import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.model.Ticket;
import com.demo.lld.parkingLotDesign.model.VehicleType;

public class ExitGateService {

     
    private List<ParkingSlot> parkingSlots;
    public ExitGateService(List<ParkingSlot> parkingSlots) {
        this.parkingSlots = parkingSlots;
    }

    public double computeCost(Ticket ticket) {
        VehicleType vehicleType = ticket.getVehicle().getVehicleType();
        CostComputationService costComputationService = CostComputationFactory.getCostComputationService(vehicleType);
        return costComputationService.computeCost(ticket);
    }

    public void freeParkingSlot(Ticket ticket) {
        VehicleType vehicleType = ticket.getVehicle().getVehicleType();
       
        ParkingSlotManager parkingSlotManager = ParkingSlotManagerFactory.findVehicle(vehicleType, parkingSlots, ticket.getGateNumber());
        parkingSlotManager.removeVehicle(ticket.getParkingSlot());
    }

}
