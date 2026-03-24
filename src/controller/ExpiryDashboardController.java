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

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpiryDashboardController {

    @FXML
    private TableView<Batch> tableNearExpiry;

    @FXML
    private TableColumn<Batch, String> colNearProduct;

    @FXML
    private TableColumn<Batch, String> colNearBatch;

    @FXML
    private TableColumn<Batch, String> colNearExpiry;

    @FXML
    private TableColumn<Batch, Integer> colNearQty;

    @FXML
    private TableView<Batch> tableExpired;

    @FXML
    private TableColumn<Batch, String> colExpProduct;

    @FXML
    private TableColumn<Batch, String> colExpBatch;

    @FXML
    private TableColumn<Batch, String> colExpExpiry;

    @FXML
    private TableColumn<Batch, Integer> colExpQty;

    private ObservableList<Batch> nearList = FXCollections.observableArrayList();
    private ObservableList<Batch> expiredList = FXCollections.observableArrayList();

    private BatchDAO batchDAO = new BatchDAO();
    private ProductDAO productDAO = new ProductDAO();

    private Map<Integer, String> productMap = new HashMap<>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {

        loadProductMap();

        colNearProduct.setCellValueFactory(data ->
                new SimpleStringProperty(productMap.getOrDefault(data.getValue().getProductId(), ""))
        );
        colNearBatch.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatchNumber()));
        colNearExpiry.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getExpiryDate().format(formatter))
        );
        colNearQty.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject()
        );

        colExpProduct.setCellValueFactory(data ->
                new SimpleStringProperty(productMap.getOrDefault(data.getValue().getProductId(), ""))
        );
        colExpBatch.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatchNumber()));
        colExpExpiry.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getExpiryDate().format(formatter))
        );
        colExpQty.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject()
        );

        tableNearExpiry.setItems(nearList);
        tableExpired.setItems(expiredList);

        loadData();
    }

    @FXML
    private void onRefresh() {
        loadData();
    }

    private void loadData() {
        nearList.clear();
        expiredList.clear();

        List<Batch> near = batchDAO.getNearExpiryBatches(90);
        List<Batch> expired = batchDAO.getExpiredBatches();

        nearList.addAll(near);
        expiredList.addAll(expired);
    }

    private void loadProductMap() {
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            productMap.put(p.getId(), p.getName());
        }
    }
}