package com.demo.lld.observer;

public class MobileObserver implements UserObserver {
    String phoneNumber;
    StockObservable stockObservable;

    public MobileObserver(String phoneNumber, StockObservable stockObservable) {
        this.phoneNumber = phoneNumber;
        this.stockObservable = stockObservable;
    }

    @Override
    public void update() {
        System.out.println("Mobile number "+this.phoneNumber+" updated");
        
    }

}
