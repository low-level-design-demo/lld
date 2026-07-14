package com.demo.lld.observer;

public class ObserverDemo {
    public static void main(String[] args) {

        
        StockObservable stockObservable = new IphoneStockObservable();

        
        UserObserver emailObserver=new EmailObserver("rishi@gmail.com",stockObservable);
        UserObserver mobileObserver=new MobileObserver("98767878",stockObservable);
        
        stockObservable.addObserver(emailObserver);
        stockObservable.addObserver(mobileObserver);
        stockObservable.setData(10);
        
    }

}
