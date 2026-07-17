package com.demo.lld.parkingLotDesign.model;

public class TwoWheelerParkingSlot extends ParkingSlot {

    public TwoWheelerParkingSlot(int slotId,VehicleType vehicleType) {
        super(slotId, VehicleType.TWO_WHEELER);

    }
    public void setPrice(int price) {
        super.setPrice(price);
    }



}
