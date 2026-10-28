package Lab2;

import java.sql.SQLException;
import java.util.List;

public interface ProcessedRequestDAO1 {
    int create(String referenceId) throws SQLException;
    List<String> getAll() throws SQLException;
    boolean update(int id, String newReferenceId) throws SQLException;
    boolean delete(int id) throws SQLException;
    boolean exists(String referenceId) throws SQLException;
}