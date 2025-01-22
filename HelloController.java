package ;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class HelloController {

    @FXML
    private Label welcomeText;
    @FXML
    private TextField productName;
    @FXML
    private TextField productAmount;
    @FXML
    private TextField deleteName;
    @FXML
    private TextField deleteAmount;
    @FXML
    private PieChart pieChart;



    @FXML
    public void initialize(){
        DBUtils.setConnection();
        loadPieChart();
    }

    @FXML
    protected void onAddItems(){
        Product product = new Product(productName.getText(), Integer.parseInt(productAmount.getText()));

        if(DBUtils.addProduct(product)){
            welcomeText.setText("damatebulia warmatebit!");
        } else{
            welcomeText.setText("ERROR!");
        }

        loadPieChart();
    }
    @FXML
    protected void onDeleteItems() {
        try {
            if (deleteName.getText().isEmpty() || deleteAmount.getText().isEmpty()) {
                welcomeText.setText("Please fill both name and amount fields for deletion.");
                return;
            }

            String name = deleteName.getText();
            int amount = Integer.parseInt(deleteAmount.getText());

            if (DBUtils.deleteOrUpdateProductByNameAndAmount(name, amount)) {
                welcomeText.setText("Operation successful for product '" + name + "'!");
            } else {
                welcomeText.setText("Operation failed for product '" + name + "'. Check logs for details.");
            }

            loadPieChart(); // Refresh the PieChart
        } catch (NumberFormatException e) {
            welcomeText.setText("Invalid amount format.");
        }
    }



    private void loadPieChart() {
        try (ResultSet resultSet = DBUtils.connection.createStatement().executeQuery("SELECT name, amount FROM products")) {

            // Transform the ResultSet into a Stream of Product objects
            Stream<Product> productStream = resultSetToProductStream(resultSet);

            // Group products by name and sum their amounts using Stream API
            Map<String, Integer> groupedProducts = productStream.collect(Collectors.groupingBy(
                    Product::getName,
                    Collectors.summingInt(Product::getAmount)
            ));

            // Convert the grouped data into a format suitable for PieChart
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            groupedProducts.forEach((name, count) ->
                    pieChartData.add(new PieChart.Data(name + " - " + count + " ცალი", count))
            );

            // Update the PieChart with the new data
            pieChart.setData(pieChartData);

        } catch (SQLException e) {
            System.err.println("Error fetching product data: " + e.getMessage());
        }
    }

    // Helper function to convert ResultSet to a Stream of Product
    private static Stream<Product> resultSetToProductStream(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (resultSet.next()) {
            // Use the constructor that takes ID for existing products
            Product product = new Product();
            product.setName(resultSet.getString("name"));
            product.setAmount(resultSet.getInt("amount"));
            products.add(product);
        }
        return products.stream();
    }







}
