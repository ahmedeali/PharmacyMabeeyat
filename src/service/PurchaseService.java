package service;

import dao.BatchDAO;
import model.Batch;
import model.Purchase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

public class PurchaseService {

    private BatchDAO batchDAO = new BatchDAO();

    public void processPurchase(Connection conn, Purchase purchase, List<Batch> batches) throws Exception {

        String purchaseSql = "INSERT INTO purchase (date, total) VALUES (?, ?)";
        int purchaseId;

        try (PreparedStatement ps = conn.prepareStatement(purchaseSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, new Date(purchase.getDate().getTime()));
            ps.setDouble(2, purchase.getTotal());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                purchaseId = rs.getInt(1);
            } else {
                throw new Exception("Failed to create purchase");
            }
        }

        String itemSql = "INSERT INTO purchase_item (purchase_id, product_id, batch_number, expiry_date, quantity, purchase_price) VALUES (?, ?, ?, ?, ?, ?)";

        for (Batch batch : batches) {

            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                ps.setInt(1, purchaseId);
                ps.setInt(2, batch.getProductId());
                ps.setString(3, batch.getBatchNumber());
                ps.setDate(4, Date.valueOf(batch.getExpiryDate()));
                ps.setInt(5, batch.getQuantity());
                ps.setDouble(6, batch.getPurchasePrice());
                ps.executeUpdate();
            }

            batchDAO.insertBatch(conn, batch);
        }
    }
}