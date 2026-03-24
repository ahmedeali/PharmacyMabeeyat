package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportsController {

    @FXML
    private TableView<ObservableList<String>> tableReports;

    @FXML
    private TableColumn<ObservableList<String>, String> col1;

    @FXML
    private TableColumn<ObservableList<String>, String> col2;

    @FXML
    private TableColumn<ObservableList<String>, String> col3;

    @FXML
    private TableColumn<ObservableList<String>, String> col4;

    @FXML
    private TableColumn<ObservableList<String>, String> col5;

    @FXML
    public void initialize() {
        setupColumns();
    }

    private void setupColumns() {
        col1.setCellValueFactory(data -> new SimpleStringProperty(getValue(data.getValue(), 0)));
        col2.setCellValueFactory(data -> new SimpleStringProperty(getValue(data.getValue(), 1)));
        col3.setCellValueFactory(data -> new SimpleStringProperty(getValue(data.getValue(), 2)));
        col4.setCellValueFactory(data -> new SimpleStringProperty(getValue(data.getValue(), 3)));
        col5.setCellValueFactory(data -> new SimpleStringProperty(getValue(data.getValue(), 4)));
    }

    private String getValue(ObservableList<String> row, int index) {
        return index < row.size() ? row.get(index) : "";
    }

    @FXML
    private void onRefresh() {
        tableReports.getItems().clear();
    }

    @FXML
    private void loadDailySales() {
        String sql = "SELECT id, date, total, payment_method FROM sale ORDER BY date DESC";
        loadData(sql);
    }

    @FXML
    private void loadProfitReport() {
        String sql = "SELECT s.id, p.name, si.quantity, si.price, b.purchase_price, (si.price - b.purchase_price) * si.quantity AS profit " +
                "FROM sale_item si " +
                "JOIN sale s ON si.sale_id = s.id " +
                "JOIN product p ON si.product_id = p.id " +
                "JOIN batch b ON si.batch_id = b.id";
        loadData(sql);
    }

    @FXML
    private void loadStockReport() {
        String sql = "SELECT p.name, b.batch_number, b.expiry_date, b.quantity, b.purchase_price " +
                "FROM batch b JOIN product p ON b.product_id = p.id";
        loadData(sql);
    }

    @FXML
    private void loadExpiryReport() {
        String sql = "SELECT p.name, b.batch_number, b.expiry_date, b.quantity " +
                "FROM batch b JOIN product p ON b.product_id = p.id " +
                "WHERE b.expiry_date < CURDATE() OR b.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 90 DAY)";
        loadData(sql);
    }

    private void loadData(String sql) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int columnCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }

            tableReports.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}