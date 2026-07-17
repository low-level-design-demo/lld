package com.demo.lld.parkingLotDesign.model;
import java.time.LocalDateTime;

public class Ticket {

    private Integer ticketId;
    private Vehicle vehicle;
    private LocalDateTime issueDateTime;
    private ParkingSlot parkingSlot;
    private Integer gateNumber;

    public Ticket(Integer ticketId, Vehicle vehicle, LocalDateTime issueDateTime, ParkingSlot parkingSlot, Integer gateNumber) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.issueDateTime = issueDateTime;
        this.parkingSlot = parkingSlot;
        this.gateNumber = gateNumber;
    }
    public Integer getTicketId() {
        return ticketId;
    }
    public Vehicle getVehicle() {
        return vehicle;
    }
    public LocalDateTime getIssueDateTime() {
        return issueDateTime;
    }
    public Integer getGateNumber() {
        return gateNumber;
    }
    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    public void setIssueDateTime(LocalDateTime issueDateTime) {
        this.issueDateTime = issueDateTime;
    }
    public ParkingSlot getParkingSlot() {
        return parkingSlot;
    }
    public void setParkingSlot(ParkingSlot parkingSlot) {
        this.parkingSlot = parkingSlot;
    }
    public void setGateNumber(Integer gateNumber) {
        this.gateNumber = gateNumber;
    }

}
