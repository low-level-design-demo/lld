package com.demo.lld.parkingLotDesign.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Indexed;

import com.demo.lld.parkingLotDesign.factory.ParkingSlotManagerFactory;
import com.demo.lld.parkingLotDesign.manager.ParkingSlotManager;
import com.demo.lld.parkingLotDesign.model.ParkingSlot;
import com.demo.lld.parkingLotDesign.model.Ticket;
import com.demo.lld.parkingLotDesign.model.Vehicle;
import com.demo.lld.parkingLotDesign.model.VehicleType;

public class EntranceGate {

    Vehicle vehicle;


    
    int gateNumber;
    
    ParkingSlotManagerFactory factoryManager= new ParkingSlotManagerFactory();
    ParkingSlotManager parkingSlotManager;
    List<ParkingSlot> slots;

    public EntranceGate(List<ParkingSlot> slots, int gateNumber) {
        this.slots = slots;
        this.gateNumber = gateNumber;
    }

    public ParkingSlot findParkingSpot(VehicleType vehicleType) {
        
        ParkingSlotManager parkingSlotManager = ParkingSlotManagerFactory.findVehicle(vehicleType, slots,gateNumber);
        return parkingSlotManager.findParkingSlot(vehicleType);
    }


    public Ticket issueTicket(Vehicle vehicle,ParkingSlot parkingSlot,int gateNumber) {

        LocalDateTime issueDateTime = LocalDateTime.now();
        Random random = new Random(); 
        Integer ticketNumber = random.nextInt();
        Ticket ticket= new Ticket(ticketNumber, vehicle, issueDateTime, parkingSlot, gateNumber);
        return ticket;
        
    }
   
    public void parkVehicle(Vehicle vehicle,ParkingSlot parkingSlot) {
        ParkingSlotManager parkingSlotManager = ParkingSlotManagerFactory.findVehicle(vehicle.getVehicleType(), slots,gateNumber);
        parkingSlotManager.parkVehicle(parkingSlot);
    }


}
