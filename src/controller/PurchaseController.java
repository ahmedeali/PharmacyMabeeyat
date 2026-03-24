package controller;

import dao.ProductDAO;
import dao.BatchDAO;
import db.DBConnection;
import model.Batch;
import model.Product;
import model.Purchase;
import model.PurchaseItem;
import service.PurchaseService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseController {

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
        }
    }

    @FXML
    private void onAddItem() {
        if (selectedProduct == null) {
            showAlert("Select product first");
            return;
        }

        try {
            String batchNo = txtBatch.getText().trim();
            LocalDate expiry = dpExpiry.getValue();
            int qty = Integer.parseInt(txtQty.getText());
            int bonus = txtBonus.getText().isEmpty() ? 0 : Integer.parseInt(txtBonus.getText());
            double price = Double.parseDouble(txtPrice.getText());

            if (batchNo.isEmpty() || expiry == null) {
                showAlert("Batch & Expiry required");
                return;
            }

            PurchaseItem item = new PurchaseItem();
            item.setProductId(selectedProduct.getId());
            item.setProductName(selectedProduct.getName());
            item.setBatchNumber(batchNo);
            item.setExpiryDate(expiry);
            item.setQuantity(qty);
            item.setBonus(bonus);
            item.setPurchasePrice(price);

            items.add(item);

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

        try {
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Purchase purchase = new Purchase();
            purchase.setDate(new Date());
            purchase.setTotal(getTotal());

            List<Batch> batches = new ArrayList<>();

            for (PurchaseItem item : items) {
                Batch batch = new Batch();
                batch.setProductId(item.getProductId());
                batch.setBatchNumber(item.getBatchNumber());
                batch.setExpiryDate(item.getExpiryDate());
                batch.setQuantity(item.getQuantity() + item.getBonus());
                batch.setPurchasePrice(item.getPurchasePrice());

                batches.add(batch);
            }

            PurchaseService service = new PurchaseService();
            service.processPurchase(conn, purchase, batches);

            conn.commit();

            showAlert("Purchase saved successfully");

            items.clear();
            calculateTotal();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving purchase");
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (PurchaseItem item : items) {
            total += item.getQuantity() * item.getPurchasePrice();
        }
        lblTotal.setText(String.valueOf(total));
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