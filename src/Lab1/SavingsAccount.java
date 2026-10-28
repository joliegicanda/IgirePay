package Lab1;

import Lab1.Account;

public class SavingsAccount extends Account {
    private double withdrawalFee = 50;
    private double withdrawalLimit = 200000;
    private double minimumDeposit = 100;

    public SavingsAccount(int accountId, double balance) {
        super(accountId, balance);
    }

    public double getWithdrawalFee() { return withdrawalFee; }
    public void setWithdrawalFee(double withdrawalFee) { this.withdrawalFee = withdrawalFee; }
    public double getWithdrawalLimit() { return withdrawalLimit; }
    public void setWithdrawalLimit(double withdrawalLimit) { this.withdrawalLimit = withdrawalLimit; }
    public double getMinimumDeposit() { return minimumDeposit; }
    public void setMinimumDeposit(double minimumDeposit) { this.minimumDeposit = minimumDeposit; }

    @Override
    public boolean deposit(double amount) {
        if (amount < minimumDeposit) {
            System.out.println("Savings: deposit is below the minimum of " + minimumDeposit);
            return false;
        }
        return super.deposit(amount);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > withdrawalLimit) {
            System.out.println("Savings: amount is above the withdrawal limit.");
            return false;
        }
        double total = amount + withdrawalFee;   // customer pays amount + fee
        if (amount <= 0 || total > getBalance()) {
            return false;
        }
        setBalance(getBalance() - total);
        return true;
    }

    // Print a message, then use the parent's logic.
    @Override
    public boolean processTransaction(Transaction t) {
        System.out.println("Savings: processing " + t.getType() + " " + t.getReferenceId());
        return super.processTransaction(t);
    }

    @Override
    public String toString() {
        return "SavingsAccount{id=" + getAccountId() + ", balance=" + getBalance()
                + ", fee=" + withdrawalFee + ", limit=" + withdrawalLimit
                + ", minDeposit=" + minimumDeposit + "}";
    }
}