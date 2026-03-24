package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private void openPOS() {
        loadScreen("/ui/POS.fxml", "POS");
    }

    @FXML
    private void openPurchase() {
        loadScreen("/ui/Purchase.fxml", "Purchasing");
    }

    @FXML
    private void openInventory() {
        loadScreen("/ui/Inventory.fxml", "Inventory");
    }

    @FXML
    private void openExpiry() {
        loadScreen("/ui/ExpiryDashboard.fxml", "Expiry Dashboard");
    }

    @FXML
    private void openReports() {
        loadScreen("/ui/Reports.fxml", "Reports");
    }

    private void loadScreen(String path, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}