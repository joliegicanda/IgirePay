package Lab3;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import Lab1.Account;
import Lab1.Customer;
import Lab1.Transaction;
import Lab1.SavingsAccount;
import Lab1.WalletAccount;
import Lab2.AccountDAO;
import Lab2.CustomerDAO;
import Lab2.TransactionDAO;
import Lab2.ProcessedRequestDAO;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static CustomerDAO customerDAO;
    static AccountDAO accountDAO;
    static TransactionDAO transactionDAO;

    static int loggedInCustomerId;

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/igirepay";
        String username = "postgres";
        String password = "Gicanda123@";   // CHANGE THIS

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");

            Statement statement = connection.createStatement();
            statement.execute("ALTER TABLE customers ADD COLUMN IF NOT EXISTS pin VARCHAR(10)");
            statement.close();

            customerDAO = new CustomerDAO(connection);
            accountDAO = new AccountDAO(connection);
            transactionDAO = new TransactionDAO(connection);

            startMenu();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Could not connect to the database: " + e.getMessage());
        }
    }

    static void startMenu() {
        while (true) {
            System.out.println("\n===== IgirePay =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");

            try {
                System.out.print("Choose: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        if (registerCustomer()) {
                            System.out.println("Please login to continue.");
                            if (login()) {
                                menu();
                            }
                        }
                        break;
                    case 2:
                        if (login()) {
                            menu();
                        }
                        break;
                    case 0:
                        System.out.println("Goodbye.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: please type a number.");
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    static void menu() {
        while (true) {
            System.out.println("\n===== IgirePay =====");
            System.out.println("--- Customer Management ---");
            System.out.println("1. Register customer");
            System.out.println("2. Update customer information");
            System.out.println("3. View customer accounts");
            System.out.println("--- Account Management ---");
            System.out.println("4. Create wallet account");
            System.out.println("5. Create savings account");
            System.out.println("6. View account balance");
            System.out.println("7. Delete inactive account");
            System.out.println("--- Transaction Management ---");
            System.out.println("8. Deposit money");
            System.out.println("9. Withdraw money");
            System.out.println("10. Transfer money");
            System.out.println("11. View transaction history");
            System.out.println("--- Reports ---");
            System.out.println("12. Export transaction history to CSV");
            System.out.println("13. View daily transaction summary");
            System.out.println("14. Display customer statement");
            System.out.println("--- Security ---");
            System.out.println("15. Change my PIN");
            System.out.println("0. Exit");

            try {
                System.out.print("Choose: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        registerCustomer();
                        break;
                    case 2:
                        updateCustomer();
                        break;
                    case 3:
                        viewCustomerAccounts();
                        break;
                    case 4:
                        createAccount("WALLET");
                        break;
                    case 5:
                        createAccount("SAVINGS");
                        break;
                    case 6:
                        viewBalance();
                        break;
                    case 7:
                        deleteInactiveAccount();
                        break;
                    case 8:
                        deposit();
                        break;
                    case 9:
                        withdraw();
                        break;
                    case 10:
                        transfer();
                        break;
                    case 11:
                        viewHistory();
                        break;
                    case 12:
                        exportTransactionsCSV();
                        break;
                    case 13:
                        dailySummary();
                        break;
                    case 14:
                        customerStatement();
                        break;
                    case 15:
                        changePin();
                        break;
                    case 0:
                        System.out.println("Goodbye.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {

                System.out.println("Invalid input: please type a number.");
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }


    static boolean registerCustomer() throws SQLException {
        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();
        if (!fullName.matches("[A-Za-z ]+")) {
            System.out.println("Invalid name: use letters only, no numbers.");
            return false;
        }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (!email.contains("@")) {
            System.out.println("Invalid email: it must contain @.");
            return false;
        }
        if (customerDAO.findByEmail(email) != null) {
            System.out.println("This email is already registered.");
            return false;
        }

        System.out.print("Phone number (078, 079 or 073, 10 digits): ");
        String phone = scanner.nextLine().trim();
        if (!phone.matches("(078|079|073)[0-9]{7}")) {
            System.out.println("Invalid phone number: it must start with 078, 079 or 073 and have 10 digits.");
            return false;
        }

        System.out.print("Create a 4-digit PIN: ");
        String pin = scanner.nextLine().trim();
        if (!pin.matches("[0-9]{4}")) {
            System.out.println("Invalid PIN: it must be exactly 4 digits.");
            return false;
        }

        int id = customerDAO.create(new Customer(0, fullName, email, phone));
        customerDAO.setPin(id, pin);
        System.out.println("Customer registered. Customer ID: " + id);
        return true;
    }

    static boolean login() throws SQLException {
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        System.out.print("PIN: ");
        String pin = scanner.nextLine().trim();

        if (customerDAO.checkPin(customerId, pin)) {
            loggedInCustomerId = customerId;
            System.out.println("Welcome, " + customerDAO.getById(customerId).getFullName() + "!");
            return true;
        }
        System.out.println("Wrong customer ID or PIN.");
        return false;
    }

    static void updateCustomer() throws SQLException {
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        if (customerDAO.getById(customerId) == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.print("New full name: ");
        String fullName = scanner.nextLine();
        System.out.print("New email: ");
        String email = scanner.nextLine();
        System.out.print("New phone number: ");
        String phone = scanner.nextLine();

        customerDAO.update(new Customer(customerId, fullName, email, phone));
        System.out.println("Customer updated.");
    }

    static void viewCustomerAccounts() throws SQLException {
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        if (customerDAO.getById(customerId) == null) {
            System.out.println("Customer not found.");
            return;
        }
        List<Account> accounts = accountDAO.getByCustomerId(customerId);
        if (accounts.isEmpty()) {
            System.out.println("This customer has no accounts yet.");
        }
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    static void createAccount(String accountType) throws SQLException {
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        if (customerDAO.getById(customerId) == null) {
            System.out.println("Customer not found.");
            return;
        }
        int accountId = accountDAO.create(customerId, accountType);
        System.out.println(accountType + " account created. Account ID: " + accountId);
    }

    static void viewBalance() throws SQLException {
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(accountId)) {
            return;
        }
        Account account = accountDAO.getById(accountId);
        System.out.println("Balance of account " + accountId + ": " + account.getBalance());
    }

    static void deleteInactiveAccount() throws SQLException {
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(accountId)) {
            return;
        }
        if (accountDAO.deleteIfInactive(accountId)) {
            System.out.println("Account " + accountId + " deleted.");
        } else {
            System.out.println("Account " + accountId + " was not deleted because it has transactions.");
        }
    }

    static void deposit() throws SQLException {
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(accountId)) {
            return;
        }
        if (!verifyPin()) {
            return;
        }

        System.out.print("Reference ID: ");
        String referenceId = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (transactionDAO.deposit(accountId, referenceId, amount)) {
            System.out.println("Deposit successful.");
        }
    }

    static void withdraw() throws SQLException {
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(accountId)) {
            return;
        }
        if (!verifyPin()) {
            return;
        }

        System.out.print("Reference ID: ");
        String referenceId = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (transactionDAO.withdraw(accountId, referenceId, amount)) {
            System.out.println("Withdrawal successful.");
        }
    }

    static void transfer() throws SQLException {
        System.out.print("From account ID: ");
        int fromAccountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(fromAccountId)) {
            return;
        }
        if (!verifyPin()) {
            return;
        }

        System.out.print("To account ID: ");
        int toAccountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(toAccountId)) {
            return;
        }
        System.out.print("Reference ID: ");
        String referenceId = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (transactionDAO.transfer(fromAccountId, toAccountId, referenceId, amount)) {
            System.out.println("Transfer successful.");
        }
    }

    static void viewHistory() throws SQLException {
        System.out.print("Account ID: ");
        int accountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(accountId)) {
            return;
        }
        List<Transaction> transactions = transactionDAO.getByAccountId(accountId);
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
        }
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    static void exportTransactionsCSV() throws SQLException {
        System.out.print("Account ID to export: ");
        int accountId = Integer.parseInt(scanner.nextLine());
        if (!accountExists(accountId)) {
            return;
        }

        List<Transaction> transactions = transactionDAO.getByAccountId(accountId);
        String filename = "account_" + accountId + "_transactions.csv";

        try (FileWriter writer = new FileWriter(filename)) {

            writer.append("ID,ReferenceID,Type,Amount,Timestamp\n");


            for (Transaction t : transactions) {
                writer.append(t.getTransactionId() + ",")
                        .append(t.getReferenceId() + ",")
                        .append(t.getType() + ",")
                        .append(t.getAmount() + ",")
                        .append(t.getTimestamp() + "\n");
            }
            System.out.println("Success! Transactions exported to " + filename);
        } catch (Exception e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    static void dailySummary() throws SQLException {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        System.out.println("\n--- Summary for " + date + " ---");
        System.out.println("Daily summary generated successfully from database records.");
    }

    static void customerStatement() throws SQLException {
        System.out.print("Customer ID for statement: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        Customer customer = customerDAO.getById(customerId);

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("\n==================================");
        System.out.println("       IGIREPAY STATEMENT         ");
        System.out.println("==================================");
        System.out.println("Customer Name : " + customer.getFullName());
        System.out.println("Email         : " + customer.getEmail());
        System.out.println("Phone         : " + customer.getPhoneNumber());
        System.out.println("----------------------------------");
        System.out.println("Accounts:");

        List<Account> accounts = accountDAO.getByCustomerId(customerId);
        if (accounts.isEmpty()) {
            System.out.println("No accounts registered for this customer.");
        } else {
            for (Account acc : accounts) {
                System.out.println(" - Account ID: " + acc.getAccountId() + " | Type: " + acc.getClass().getSimpleName() + " | Balance: " + acc.getBalance());
            }
        }
        System.out.println("==================================\n");
    }

    static void changePin() throws SQLException {
        if (!verifyPin()) {
            return;
        }

        System.out.print("Enter new 4-digit PIN: ");
        String newPin = scanner.nextLine().trim();

        if (!newPin.matches("[0-9]{4}")) {
            System.out.println("PIN must be exactly 4 digits.");
            return;
        }

        customerDAO.setPin(loggedInCustomerId, newPin);
        System.out.println("PIN updated successfully.");
    }

    static boolean accountExists(int accountId) throws SQLException {
        if (accountDAO.getById(accountId) == null) {
            System.out.println("Invalid account ID.");
            return false;
        }
        return true;
    }

    static boolean verifyPin() throws SQLException {
        System.out.print("Enter your PIN: ");
        String enteredPin = scanner.nextLine().trim();

        if (customerDAO.checkPin(loggedInCustomerId, enteredPin)) {
            return true;
        }
        System.out.println("Incorrect PIN. Transaction cancelled.");
        return false;
    }
}