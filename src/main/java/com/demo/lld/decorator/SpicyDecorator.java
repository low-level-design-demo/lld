package com.demo.lld.decorator;

public class SpicyDecorator extends Decorator {

    BasePizza basePizza;

    public SpicyDecorator(BasePizza basePizza) {
        super();
        this.basePizza = basePizza;
    }

    @Override
    public int calculateCost() {
        int spicyToppingCost=5;
        System.out.println("Cost of Spicy Topping: "+spicyToppingCost);
        int cost = basePizza.calculateCost() + spicyToppingCost;
        return cost;
    }

}
