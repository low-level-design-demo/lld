package com.demo.lld.atmMachine;

public class AtmMachine {
    private static Atm atm = new Atm();
    private User user;
    private Card card;

    public static void main(String[] args) {
        AtmMachine atmMachine = new AtmMachine();
        atmMachine.initializeAtmMachine();
        atmMachine.atm.printCurrentATMStatus();
        atmMachine.atm.getAtmState().insertCard(atmMachine.atm, atmMachine.user.getCard());
        atmMachine.atm.getAtmState().authenticate(atmMachine.atm, atmMachine.user.getCard(), "1234");
        atmMachine.atm.getAtmState().selectOperation(atmMachine.atm,atmMachine.user.getCard(), TransactionType.WITHDRAW);
        atmMachine.atm.getAtmState().withdrawCash(atmMachine.atm,atmMachine.user.getCard(), 5000);
        atmMachine.atm.printCurrentATMStatus();
       
    }

    public AtmMachine() {
       
    }

    private void initializeAtmMachine() {

        atm = Atm.getAtm();
        atm.setAtmBalance(100000, 50, 100, 200);
        this.user = createUser();

    }

    private User createUser() {
        User user = new User();
        user.setCard(createCard());
        return user;
    }

    private Card createCard() {
        Card card = new Card();
        card.setCardNumber("1234567890");
        card.setBankAccount(createBankAccount());
        return card;
    }

    private BankAccount createBankAccount() {
        BankAccount balance = new BankAccount(10000);
        return balance;
    }

}
