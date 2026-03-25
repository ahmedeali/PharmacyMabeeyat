package controller;

import db.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.UserSession;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Enter username and password");
            return;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UserSession.setUsername(username);
                try (PreparedStatement log = conn.prepareStatement("INSERT INTO activity_log (username, action_desc) VALUES (?, ?)") ) {
                    log.setString(1, username);
                    log.setString(2, "User logged in");
                    log.executeUpdate();
                }
                openDashboard();
                closeCurrentWindow();
            } else {
                showAlert("Invalid credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database error");
        }
    }

    private void openDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ui/Dashboard.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

src/controller/POSController.java
src/controller/POSController.java
+147
-15

src/controller/PurchaseController.java
src/controller/PurchaseController.java
+42
-8

package controller;

import dao.ProductDAO;
import dao.BatchDAO;
import dao.SupplierDAO;
import db.DBConnection;
import model.Batch;
import model.Product;
import model.Purchase;
import model.PurchaseItem;
import model.UserSession;
import service.PurchaseService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseController {

    @FXML
    private TextField txtSupplier;

    @FXML
    private TextField txtProduct;

    @FXML
    private TextField txtBatch;

    @FXML
    private DatePicker dpExpiry;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtBonus;

    @FXML
    private TextField txtPrice;

    @FXML
    private TableView<PurchaseItem> table;

    @FXML
    private TableColumn<PurchaseItem, String> colProduct;

    @FXML
    private TableColumn<PurchaseItem, String> colBatch;

    @FXML
    private TableColumn<PurchaseItem, Integer> colQty;

    @FXML
    private TableColumn<PurchaseItem, Integer> colBonus;

    @FXML
    private TableColumn<PurchaseItem, Double> colPrice;

    @FXML
    private Label lblTotal;

    private ObservableList<PurchaseItem> items = FXCollections.observableArrayList();

    private ProductDAO productDAO = new ProductDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();

    private Product selectedProduct;

    @FXML
    public void initialize() {
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colBatch.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatchNumber()));
        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colBonus.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBonus()).asObject());
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPurchasePrice()).asObject());

        table.setItems(items);
    }

    @FXML
    private void onSearchProduct() {
        String name = txtProduct.getText().trim();
        if (name.isEmpty()) return;

        selectedProduct = productDAO.findByNameOrBarcode(name);

        if (selectedProduct == null) {
            showAlert("Product not found");
        } else {
            txtProduct.setText(selectedProduct.getName());
@@ -127,92 +137,116 @@ public class PurchaseController {

            clearInputs();
            calculateTotal();

        } catch (Exception e) {
            showAlert("Invalid input");
        }
    }

    @FXML
    private void onRemoveItem() {
        PurchaseItem selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            items.remove(selected);
            calculateTotal();
        }
    }

    @FXML
    private void onConfirmPurchase() {
        if (items.isEmpty()) {
            showAlert("No items added");
            return;
        }

        Connection conn = null;
        try {
            Connection conn = DBConnection.getConnection();
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Integer supplierId = null;
            if (!txtSupplier.getText().trim().isEmpty()) {
                supplierId = supplierDAO.getOrCreateSupplierId(conn, txtSupplier.getText().trim());
            }

            Purchase purchase = new Purchase();
            purchase.setDate(new Date());
            purchase.setTotal(getTotal());
            purchase.setSupplierId(supplierId);
            purchase.setCreatedBy(UserSession.getUsername());

            List<Batch> batches = new ArrayList<>();

            for (PurchaseItem item : items) {
                Batch batch = new Batch();
                batch.setProductId(item.getProductId());
                batch.setBatchNumber(item.getBatchNumber());
                batch.setExpiryDate(item.getExpiryDate());
                batch.setQuantity(item.getQuantity() + item.getBonus());
                batch.setPurchasePrice(item.getPurchasePrice());
                batch.setSupplierId(supplierId);

                batches.add(batch);
            }

            PurchaseService service = new PurchaseService();
            service.processPurchase(conn, purchase, batches);

            conn.commit();

            showAlert("Purchase saved successfully");

            items.clear();
            calculateTotal();

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Error saving purchase");
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignore) {
            }
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (PurchaseItem item : items) {
            total += item.getQuantity() * item.getPurchasePrice();
        }
        lblTotal.setText(String.valueOf(total));
        lblTotal.setText(String.format("%.2f SDG", total));
    }

    private double getTotal() {
        double total = 0;
        for (PurchaseItem item : items) {
            total += item.getQuantity() * item.getPurchasePrice();
        }
        return total;
    }

    private void clearInputs() {
        txtBatch.clear();
        txtQty.clear();
        txtBonus.clear();
        txtPrice.clear();
        dpExpiry.setValue(null);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
}
