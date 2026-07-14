package com.demo.lld.observer;

import java.util.ArrayList;
import java.util.List;

public class IphoneStockObservable implements StockObservable {
    int count = 0;
    List<UserObserver> observers = new ArrayList<>();
    public IphoneStockObservable() {
        super();
    }

    @Override
    public void addObserver(UserObserver userObserver) {
       observers.add(userObserver);
    }

    @Override
    public void removeObserver(UserObserver userObserver) {
       observers.remove(userObserver);
    }

    @Override
    public void notifyObservers() {
       for(UserObserver observer:observers){
         observer.update();
       }
    }

    @Override
    public void setData(int count) {
      this.count = this.count + count;
      if (this.count > 0) {
         notifyObservers();
      }
    }
}
