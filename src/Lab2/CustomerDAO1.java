package Lab2;

import java.sql.SQLException;
import Lab1.Customer;

public interface CustomerDAO1 {
    int create(Customer customer) throws SQLException;
    Customer getById(int id) throws SQLException;
    boolean update(Customer customer) throws SQLException;
    boolean delete(int id) throws SQLException;
    Customer findByEmail(String email) throws SQLException;
}