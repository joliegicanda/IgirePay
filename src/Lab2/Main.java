package Lab2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import Lab1.Account;
import Lab1.Customer;
import Lab1.Transaction;

public class Main {
    public static void main(String[] args) {

        String createCustomers = "CREATE TABLE IF NOT EXISTS customers ("
                + "id SERIAL PRIMARY KEY, "
                + "full_name VARCHAR(100) NOT NULL, "
                + "email VARCHAR(100) UNIQUE, "
                + "phone_number VARCHAR(20))";

        String createAccounts = "CREATE TABLE IF NOT EXISTS accounts ("
                + "id SERIAL PRIMARY KEY, "
                + "customer_id INT NOT NULL REFERENCES customers(id), "
                + "account_type VARCHAR(20) NOT NULL, "
                + "balance NUMERIC(15,2) DEFAULT 0, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String createTransactions = "CREATE TABLE IF NOT EXISTS transactions ("
                + "id SERIAL PRIMARY KEY, "
                + "account_id INT NOT NULL REFERENCES accounts(id), "
                + "reference_id VARCHAR(50) UNIQUE NOT NULL, "
                + "transaction_type VARCHAR(20) NOT NULL, "
                + "amount NUMERIC(15,2) NOT NULL, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String createProcessedRequests = "CREATE TABLE IF NOT EXISTS processed_requests ("
                + "id SERIAL PRIMARY KEY, "
                + "reference_id VARCHAR(50) UNIQUE NOT NULL, "
                + "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try {
            Connection connection = DBConnection.getConnection();
            System.out.println("Connected to the database.");

            Statement statement = connection.createStatement();
            statement.execute(createCustomers);
            statement.execute(createAccounts);
            statement.execute(createTransactions);
            statement.execute(createProcessedRequests);
            statement.close();
            System.out.println("Tables created successfully.");


            CustomerDAO customerDAO = new CustomerDAO(connection);
            AccountDAO accountDAO = new AccountDAO(connection);
            TransactionDAO transactionDAO = new TransactionDAO(connection);
            ProcessedRequestDAO processedRequestDAO = new ProcessedRequestDAO(connection);

            System.out.println("\n--- Customers ---");
            int customerId = customerDAO.create(new Customer(0, "Alice Uwase", "alice@mail.com", "0788000000"));
            System.out.println("Added:   " + customerDAO.getById(customerId));
            customerDAO.update(new Customer(customerId, "Alice Uwase", "alice.uwase@mail.com", "0788111111"));
            System.out.println("Updated: " + customerDAO.findByEmail("alice.uwase@mail.com"));

            System.out.println("Attack:  " + customerDAO.findByEmail("' OR '1'='1"));

            System.out.println("\n--- Accounts ---");
            int walletId = accountDAO.create(customerId, "WALLET");
            int savingsId = accountDAO.create(customerId, "SAVINGS");
            int unusedId = accountDAO.create(customerId, "SAVINGS");
            for (Account account : accountDAO.getByCustomerId(customerId)) {
                System.out.println(account);
            }

            System.out.println("\n--- Transactions ---");
            System.out.println("Deposit REF-001:  " + transactionDAO.deposit(walletId, "REF-001", 50000));
            System.out.println("Withdraw REF-002: " + transactionDAO.withdraw(walletId, "REF-002", 5000));
            System.out.println("Transfer REF-003: " + transactionDAO.transfer(walletId, savingsId, "REF-003", 10000));

            System.out.println("\n--- Duplicate and invalid requests ---");
            System.out.println("Retry REF-002:    " + transactionDAO.withdraw(walletId, "REF-002", 5000));
            System.out.println("Empty reference:  " + transactionDAO.deposit(walletId, "", 100));
            System.out.println("Too much REF-004: " + transactionDAO.withdraw(walletId, "REF-004", 50000000));

            System.out.println("\nWallet:  " + accountDAO.getById(walletId));
            System.out.println("Savings: " + accountDAO.getById(savingsId));

            System.out.println("\nWallet history:");
            for (Transaction transaction : transactionDAO.getByAccountId(walletId)) {
                System.out.println(transaction);
            }

            System.out.println("\n--- Secure account query ---");
            System.out.println("Owner:        " + accountDAO.getByIdForCustomer(walletId, customerId));
            System.out.println("Someone else: " + accountDAO.getByIdForCustomer(walletId, customerId + 1));

            System.out.println("\n--- Delete inactive accounts ---");
            System.out.println("Unused account deleted? " + accountDAO.deleteIfInactive(unusedId));
            System.out.println("Wallet deleted?         " + accountDAO.deleteIfInactive(walletId));

            System.out.println("\nProcessed reference IDs: " + processedRequestDAO.getAll());

            connection.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}