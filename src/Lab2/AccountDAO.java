package Lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Lab1.Account;
import Lab1.SavingsAccount;
import Lab1.WalletAccount;

public class AccountDAO {
    private Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public int create(int customerId, String accountType) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, account_type) VALUES (?, ?) RETURNING id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, accountType);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public Account getById(int id) throws SQLException {
        String sql = "SELECT id, account_type, balance FROM accounts WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildAccount(resultSet);
            }
            return null;
        }
    }

    public boolean updateBalance(int accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setInt(2, accountId);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean delete(int accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public List<Account> getByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT id, account_type, balance FROM accounts WHERE customer_id = ? ORDER BY id";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                accounts.add(buildAccount(resultSet));
            }
        }
        return accounts;
    }

    public Account getByIdForCustomer(int accountId, int customerId) throws SQLException {
        String sql = "SELECT id, account_type, balance FROM accounts WHERE id = ? AND customer_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            preparedStatement.setInt(2, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildAccount(resultSet);
            }
            return null;
        }
    }

    public boolean deleteIfInactive(int accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ? "
                + "AND NOT EXISTS (SELECT 1 FROM transactions WHERE account_id = accounts.id)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Account buildAccount(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        double balance = resultSet.getDouble("balance");
        String type = resultSet.getString("account_type");

        if (type.equals("SAVINGS")) {
            return new SavingsAccount(id, balance);
        }
        return new WalletAccount(id, balance);
    }
}