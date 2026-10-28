package Lab1;

public class Account {
    private int accountId;
    private double balance;

    public Account(int accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;               // invalid amount
        }
        balance = balance + amount;
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance = balance - amount;
        return true;
    }

    public boolean processTransaction(Transaction t) {
        if (t.getType().equals("DEPOSIT")) {
            return deposit(t.getAmount());
        } else if (t.getType().equals("WITHDRAW")) {
            return withdraw(t.getAmount());
        }
        return false;                   // unknown type
    }

    @Override
    public String toString() {
        return "Account{id=" + accountId + ", balance=" + balance + "}";
    }
}