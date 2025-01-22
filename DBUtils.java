package ;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBUtils {
    public static Connection connection;
    private static Statement statement;

    private static final String url = "jdbc:mysql://localhost/magazia";
    private static final String user = "root";
    private static final String password = "";

    public static void setConnection(){
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void disconnect(){
        try {
            if(statement != null){
                statement.close();
            }
            if(connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean exec(String sql){
        if (statement != null){
            try {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                return false;

            }
        }
        return true;
    }

    public static boolean addProduct(Product product){
        String sql = "INSERT INTO products (name, amount) VALUES ('" + product.getName() + "', " + product.getAmount() + ")";
        if(!exec(sql)){
            return false;
        }
        return true;
    }

    public static boolean deleteOrUpdateProductByNameAndAmount(String name, int deleteAmount) {
        String selectSql = "SELECT id, amount FROM products WHERE name = ? ORDER BY id DESC";
        String updateSql = "UPDATE products SET amount = ? WHERE id = ?";
        String deleteSql = "DELETE FROM products WHERE id = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateSql);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

            selectStmt.setString(1, name);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                int remainingToDelete = deleteAmount;

                while (resultSet.next() && remainingToDelete > 0) {
                    int id = resultSet.getInt("id");
                    int existingAmount = resultSet.getInt("amount");

                    if (existingAmount <= remainingToDelete) {
                        // Delete the entire row
                        deleteStmt.setInt(1, id);
                        deleteStmt.executeUpdate();
                        remainingToDelete -= existingAmount;
                    } else {
                        // Update the row with the remaining amount
                        updateStmt.setInt(1, existingAmount - remainingToDelete);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                        remainingToDelete = 0;
                    }
                }

                if (remainingToDelete > 0) {
                    System.err.println("Not enough stock to delete. Remaining to delete: " + remainingToDelete);
                    return false;
                }

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting or updating product: " + e.getMessage());
            return false;
        }
    }
}
