package Lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Lab1.Account;
import Lab1.Transaction;

public class TransactionDAO {
    private Connection connection;
    private AccountDAO accountDAO;
    private ProcessedRequestDAO processedRequestDAO;

    public TransactionDAO(Connection connection) {
        this.connection = connection;
        this.accountDAO = new AccountDAO(connection);
        this.processedRequestDAO = new ProcessedRequestDAO(connection);
    }

    public int create(int accountId, Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, reference_id, transaction_type, amount) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            preparedStatement.setString(2, transaction.getReferenceId());
            preparedStatement.setString(3, transaction.getType());
            preparedStatement.setDouble(4, transaction.getAmount());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public Transaction getById(int id) throws SQLException {
        String sql = "SELECT id, reference_id, transaction_type, amount, created_at "
                + "FROM transactions WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildTransaction(resultSet);
            }
            return null;
        }
    }

    public boolean update(int id, double newAmount) throws SQLException {
        String sql = "UPDATE transactions SET amount = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, newAmount);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public List<Transaction> getByAccountId(int accountId) throws SQLException {
        String sql = "SELECT id, reference_id, transaction_type, amount, created_at "
                + "FROM transactions WHERE account_id = ? ORDER BY id";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transactions.add(buildTransaction(resultSet));
            }
        }
        return transactions;
    }

    public boolean deposit(int accountId, String referenceId, double amount) throws SQLException {
        if (!canProcess(accountId, referenceId, amount)) {
            return false;
        }
        Account account = accountDAO.getById(accountId);
        accountDAO.updateBalance(accountId, account.getBalance() + amount);
        create(accountId, new Transaction(0, referenceId, amount, "DEPOSIT"));
        processedRequestDAO.create(referenceId);       // remember this reference ID
        return true;
    }


    public boolean withdraw(int accountId, String referenceId, double amount) throws SQLException {
        if (!canProcess(accountId, referenceId, amount)) {
            return false;
        }
        Account account = accountDAO.getById(accountId);
        if (amount > account.getBalance()) {
            System.out.println("Insufficient balance. " + referenceId + " was not saved, so it can be retried.");
            return false;
        }
        accountDAO.updateBalance(accountId, account.getBalance() - amount);
        create(accountId, new Transaction(0, referenceId, amount, "WITHDRAW"));
        processedRequestDAO.create(referenceId);
        return true;
    }

    public boolean transfer(int fromAccountId, int toAccountId, String referenceId, double amount)
            throws SQLException {
        if (!canProcess(fromAccountId, referenceId, amount)) {
            return false;
        }
        Account fromAccount = accountDAO.getById(fromAccountId);
        Account toAccount = accountDAO.getById(toAccountId);
        if (toAccount == null) {
            System.out.println("Invalid account ID: " + toAccountId);
            return false;
        }
        if (amount > fromAccount.getBalance()) {
            System.out.println("Insufficient balance. " + referenceId + " was not saved, so it can be retried.");
            return false;
        }
        accountDAO.updateBalance(fromAccountId, fromAccount.getBalance() - amount);
        accountDAO.updateBalance(toAccountId, toAccount.getBalance() + amount);

        create(fromAccountId, new Transaction(0, referenceId + "-OUT", amount, "WITHDRAW"));
        create(toAccountId, new Transaction(0, referenceId + "-IN", amount, "DEPOSIT"));
        processedRequestDAO.create(referenceId);
        return true;
    }


    private boolean canProcess(int accountId, String referenceId, double amount) throws SQLException {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            System.out.println("Invalid reference ID: it cannot be empty.");
            return false;
        }
        if (processedRequestDAO.exists(referenceId)) {
            System.out.println("Duplicate request rejected: " + referenceId + " was already processed.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Invalid amount: " + amount);
            return false;
        }
        if (accountDAO.getById(accountId) == null) {
            System.out.println("Invalid account ID: " + accountId);
            return false;
        }
        return true;
    }


    private Transaction buildTransaction(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction(
                resultSet.getInt("id"),
                resultSet.getString("reference_id"),
                resultSet.getDouble("amount"),
                resultSet.getString("transaction_type"));
        transaction.setTimestamp(resultSet.getTimestamp("created_at").toLocalDateTime());
        return transaction;
    }
}