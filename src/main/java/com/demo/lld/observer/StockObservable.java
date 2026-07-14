package com.demo.lld.observer;

public interface StockObservable {

    public void addObserver(UserObserver  userObserver);
    public void removeObserver(UserObserver  userObserver);
    public void notifyObservers();
    public void setData(int count);

}
