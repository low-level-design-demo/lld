package com.demo.lld.atmMachine;

public class User {

    private String name;
    private String userId;
    private Card card;

    public User() {
    }

    public User(String name, String userId, Card card) {
        this.name = name;
        this.userId = userId;
        this.card = card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
    

}
