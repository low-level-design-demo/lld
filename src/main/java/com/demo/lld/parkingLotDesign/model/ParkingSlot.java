package com.demo.lld.parkingLotDesign.model;

public class ParkingSlot {

    private int slotNumber;
    private boolean isAvailable;
    private VehicleType vehicleType;
    private int price=10; //base price for parking slot

    public ParkingSlot(int slotNumber,VehicleType vehicleType) {
        this.slotNumber = slotNumber;
        this.vehicleType = vehicleType;
        this.isAvailable = true;
    }





    public void parkVehicle(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        this.isAvailable = false;
    }
    public void removeVehicle() {
        this.vehicleType = null;
        this.isAvailable = true;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getSlotNumber() {
        return slotNumber;
    }
   
    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

     public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

}
