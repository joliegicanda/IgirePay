package Lab2;

import java.sql.SQLException;
import java.util.List;
import Lab1.Transaction;

public interface TransactionDAO1 {
    int create(int accountId, Transaction transaction) throws SQLException;
    Transaction getById(int id) throws SQLException;
    boolean update(int id, double newAmount) throws SQLException;
    boolean delete(int id) throws SQLException;
    List<Transaction> getByAccountId(int accountId) throws SQLException;
    boolean deposit(int accountId, String referenceId, double amount) throws SQLException;
    boolean withdraw(int accountId, String referenceId, double amount) throws SQLException;
    boolean transfer(int fromAccountId, int toAccountId, String referenceId, double amount) throws SQLException;
}