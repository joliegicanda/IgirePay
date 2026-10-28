package Lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessedRequestDAO {
    private Connection connection;

    public ProcessedRequestDAO(Connection connection) {
        this.connection = connection;
    }

    public int create(String referenceId) throws SQLException {
        String sql = "INSERT INTO processed_requests (reference_id) VALUES (?) RETURNING id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, referenceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public List<String> getAll() throws SQLException {
        String sql = "SELECT reference_id FROM processed_requests ORDER BY id";
        List<String> referenceIds = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                referenceIds.add(resultSet.getString("reference_id"));
            }
        }
        return referenceIds;
    }

    public boolean update(int id, String newReferenceId) throws SQLException {
        String sql = "UPDATE processed_requests SET reference_id = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newReferenceId);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM processed_requests WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean exists(String referenceId) throws SQLException {
        String sql = "SELECT 1 FROM processed_requests WHERE reference_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, referenceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }
}