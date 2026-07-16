package com.demo.lld.decorator;

public class DecoratorDemo {
    public static void main(String[] args) {

        BasePizza basePizza=new VegPizza();
        Decorator mushroomDecorator=new MushRoomDecorator(basePizza);

        System.out.println("Cost of Veg Pizza with Mushroom Topping: "+mushroomDecorator.calculateCost());

        BasePizza basePizza1=new MargaritaPizza();
        Decorator mushroomDecorator2=new MushRoomDecorator(new SpicyDecorator(basePizza1));

        System.out.println("Cost of Margarita Pizza with Spicy Topping: "+mushroomDecorator2.calculateCost());



    }

}
