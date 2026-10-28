package Lab2;

import java.sql.SQLException;
import java.util.List;
import Lab1.Account;

public interface AccountDAO1 {
    int create(int customerId, String accountType) throws SQLException;
    Account getById(int id) throws SQLException;
    boolean updateBalance(int accountId, double newBalance) throws SQLException;
    boolean delete(int accountId) throws SQLException;
    List<Account> getByCustomerId(int customerId) throws SQLException;
    Account getByIdForCustomer(int accountId, int customerId) throws SQLException;
    boolean deleteIfInactive(int accountId) throws SQLException;
}