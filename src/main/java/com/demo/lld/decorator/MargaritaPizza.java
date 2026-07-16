package com.demo.lld.decorator;

public class MargaritaPizza extends BasePizza {

    @Override
    public int calculateCost() {
         int cost=15;
        System.out.println("Cost of MargaritaPizza: "+cost); 
        return cost; 
    }

}
