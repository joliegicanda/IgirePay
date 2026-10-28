package Lab1;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private String referenceId;
    private double amount;
    private String type;
    private LocalDateTime timestamp;

    public Transaction(int transactionId, String referenceId, double amount, String type) {
        this.transactionId = transactionId;
        this.referenceId = referenceId;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public int getTransactionId() { return transactionId; }
    public String getReferenceId() { return referenceId; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Transaction{id=" + transactionId + ", ref=" + referenceId
                + ", amount=" + amount + ", type=" + type + ", time=" + timestamp + "}";
    }
}