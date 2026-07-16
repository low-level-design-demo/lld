package com.demo.lld.decorator;

public class VegPizza extends BasePizza {

    @Override
    public int calculateCost() {
        int cost=12;
        System.out.println("Cost of VezPizza: "+cost); 
        return cost; 
    }

}
