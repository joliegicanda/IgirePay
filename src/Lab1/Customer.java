package Lab1;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String pin;                                   // NEW: the customer's PIN
    private List<Account> accounts = new ArrayList<>();   // the customer's accounts

    public Customer(int id, String fullName, String email, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPin() { return pin; }                       // NEW
    public void setPin(String pin) { this.pin = pin; }           // NEW
    public List<Account> getAccounts() { return accounts; }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name=" + fullName + ", email=" + email
                + ", phone=" + phoneNumber + ", accounts=" + accounts.size() + "}";
    }
}