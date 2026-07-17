package com.demo.lld.parkingLotDesign.model;

public class FourWheelerParkingSlot extends ParkingSlot {

    public FourWheelerParkingSlot(int slotId,VehicleType vehicleType) {
        super(slotId, VehicleType.FOUR_WHEELER);
    }

      public void setPrice(int price) {
        super.setPrice(price);
    }

    

}
