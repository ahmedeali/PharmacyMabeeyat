package controller;

import dao.BatchDAO;
import dao.ProductDAO;
import model.Batch;
import model.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InventoryController {

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Batch> tableInventory;

    @FXML
    private TableColumn<Batch, String> colProduct;

    @FXML
    private TableColumn<Batch, String> colBatch;

    @FXML
    private TableColumn<Batch, String> colExpiry;

    @FXML
    private TableColumn<Batch, Integer> colQty;

    @FXML
    private TableColumn<Batch, Double> colPrice;

    private ObservableList<Batch> list = FXCollections.observableArrayList();

    private BatchDAO batchDAO = new BatchDAO();
    private ProductDAO productDAO = new ProductDAO();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        colProduct.setCellValueFactory(data -> {
            Product p = productDAO.getAllProducts()
                    .stream()
                    .filter(prod -> prod.getId() == data.getValue().getProductId())
                    .findFirst()
                    .orElse(null);

            return new SimpleStringProperty(p != null ? p.getName() : "");
        });

        colBatch.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatchNumber()));

        colExpiry.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getExpiryDate() != null ?
                        data.getValue().getExpiryDate().format(formatter) : ""
        ));

        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPurchasePrice()).asObject());

        tableInventory.setItems(list);

        loadAll();
    }

    @FXML
    private void onSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadAll();
            return;
        }

        List<Product> products = productDAO.searchProducts(keyword);
        list.clear();

        for (Product p : products) {
            List<Batch> batches = batchDAO.getAvailableBatchesByProductFEFO(p.getId());
            list.addAll(batches);
        }
    }

    @FXML
    private void onRefresh() {
        txtSearch.clear();
        loadAll();
    }

    private void loadAll() {
        list.clear();

        List<Product> products = productDAO.getAllProducts();

        for (Product p : products) {
            List<Batch> batches = batchDAO.getAvailableBatchesByProductFEFO(p.getId());
            list.addAll(batches);
        }
    }
}