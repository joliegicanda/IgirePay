package Lab1;

import Lab1.Account;
import Lab1.Transaction;

public class WalletAccount extends Account {

    public WalletAccount(int accountId, double balance) {
        super(accountId, balance);
    }


    @Override
    public boolean deposit(double amount) {
        System.out.println("Wallet: instant deposit.");
        return super.deposit(amount);
    }

    @Override
    public boolean withdraw(double amount) {
        System.out.println("Wallet: instant withdrawal, no fee.");
        return super.withdraw(amount);
    }

    @Override
    public boolean processTransaction(Transaction t) {
        System.out.println("Wallet: processing " + t.getType() + " " + t.getReferenceId());
        return super.processTransaction(t);
    }

    @Override
    public String toString() {
        return "WalletAccount{id=" + getAccountId() + ", balance=" + getBalance() + "}";
    }
}