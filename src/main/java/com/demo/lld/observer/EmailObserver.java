package com.demo.lld.observer;

public class EmailObserver implements UserObserver{
    String email;
    StockObservable stockObservable;

    public EmailObserver(String email, StockObservable stockObservable) {
        this.email = email;
        this.stockObservable = stockObservable;
    }

    @Override
    public void update() {
        System.out.println("Email sent to "+ this.email);
    }
    
}


