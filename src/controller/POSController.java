package controller;

import dao.ProductDAO;
import dao.BatchDAO;
import db.DBConnection;
import model.Batch;
import model.CartItem;
import model.Product;
import model.Sale;
import model.SaleItem;
import service.SalesService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

public class POSController {

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<CartItem> tableCart;

    @FXML
    private TableColumn<CartItem, String> colProduct;

    @FXML
    private TableColumn<CartItem, Integer> colQty;

    @FXML
    private TableColumn<CartItem, Double> colPrice;

    @FXML
    private TableColumn<CartItem, Double> colTotal;

    @FXML
    private Label lblTotal;

    @FXML
    private ComboBox<String> cmbPayment;

    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();

    private ProductDAO productDAO = new ProductDAO();
    private BatchDAO batchDAO = new BatchDAO();

    @FXML
    public void initialize() {
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotal()).asObject());

        tableCart.setItems(cartList);

        cmbPayment.getItems().addAll("Cash", "Bank", "Mixed");
        cmbPayment.getSelectionModel().selectFirst();
    }

    @FXML
    private void onSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) return;

        Product product = productDAO.findByNameOrBarcode(keyword);
        if (product == null) {
            showAlert("Product not found");
            return;
        }

        addProductFEFO(product, 1);
        txtSearch.clear();
    }

    private void addProductFEFO(Product product, int requestedQty) {
        List<Batch> batches = batchDAO.getAvailableBatchesByProductFEFO(product.getId());

        int remaining = requestedQty;

        for (Batch batch : batches) {
            if (remaining <= 0) break;

            if (batch.getExpiryDate().isBefore(LocalDate.now())) continue;

            int takeQty = Math.min(batch.getQuantity(), remaining);

            addToCart(product, batch, takeQty);

            remaining -= takeQty;
        }

        if (remaining > 0) {
            showAlert("Not enough stock available");
        }

        calculateTotal();
    }

    private void addToCart(Product product, Batch batch, int qty) {
        for (CartItem item : cartList) {
            if (item.getProductId() == product.getId() &&
                item.getBatchId() == batch.getId()) {

                item.setQuantity(item.getQuantity() + qty);
                tableCart.refresh();
                return;
            }
        }

        CartItem item = new CartItem();
        item.setProductId(product.getId());
        item.setBatchId(batch.getId());
        item.setProductName(product.getName());
        item.setPrice(product.getSellingPrice());
        item.setQuantity(qty);

        cartList.add(item);
    }

    private void calculateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }

    @FXML
    private void onRemoveItem() {
        CartItem selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartList.remove(selected);
            calculateTotal();
        }
    }

    @FXML
    private void onCheckout() {
        if (cartList.isEmpty()) {
            showAlert("Cart is empty");
            return;
        }

        String paymentMethod = cmbPayment.getValue();

        try {
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Sale sale = new Sale();
            sale.setDate(new Date());
            sale.setPaymentMethod(paymentMethod);
            sale.setTotal(getTotal());

            List<SaleItem> saleItems = new ArrayList<>();

            for (CartItem cart : cartList) {
                SaleItem item = new SaleItem();
                item.setProductId(cart.getProductId());
                item.setBatchId(cart.getBatchId());
                item.setQuantity(cart.getQuantity());
                item.setPrice(cart.getPrice());
                item.setTotal(cart.getTotal());

                saleItems.add(item);
            }

            SalesService service = new SalesService();
            service.processSale(conn, sale, saleItems);

            conn.commit();

            showAlert("Sale completed successfully");
            cartList.clear();
            calculateTotal();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error processing sale");
        }
    }

    private double getTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getTotal();
        }
        return total;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}