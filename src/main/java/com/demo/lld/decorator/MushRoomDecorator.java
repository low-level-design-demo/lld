package com.demo.lld.decorator;

public class MushRoomDecorator extends Decorator {

    BasePizza basePizza;

    public MushRoomDecorator(BasePizza basePizza) {
        super();
        this.basePizza = basePizza;
    }

    @Override
    public int calculateCost() {
        int mushRoomToppingCost = 5;
        System.out.println("Cost of Mushroom topping: " + mushRoomToppingCost);// Cost of MushRoom Topping
        int totalCost = basePizza.calculateCost() + mushRoomToppingCost;

        return totalCost;
    }

}
