package dao;

import db.DBConnection;
import model.Batch;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BatchDAO {

    public List<Batch> getAvailableBatchesByProductFEFO(int productId) {
        List<Batch> list = new ArrayList<>();

        String sql = "SELECT * FROM batch WHERE product_id = ? AND quantity > 0 ORDER BY expiry_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Batch b = new Batch();
                b.setId(rs.getInt("id"));
                b.setProductId(rs.getInt("product_id"));
                b.setBatchNumber(rs.getString("batch_number"));
                b.setQuantity(rs.getInt("quantity"));
                b.setPurchasePrice(rs.getDouble("purchase_price"));

                Date exp = rs.getDate("expiry_date");
                if (exp != null) {
                    b.setExpiryDate(exp.toLocalDate());
                }

                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertBatch(Connection conn, Batch batch) throws SQLException {
        String sql = "INSERT INTO batch (product_id, batch_number, expiry_date, quantity, purchase_price) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, batch.getProductId());
            ps.setString(2, batch.getBatchNumber());
            ps.setDate(3, Date.valueOf(batch.getExpiryDate()));
            ps.setInt(4, batch.getQuantity());
            ps.setDouble(5, batch.getPurchasePrice());
            ps.executeUpdate();
        }
    }

    public void updateBatchQuantity(Connection conn, int batchId, int newQty) throws SQLException {
        String sql = "UPDATE batch SET quantity = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, batchId);
            ps.executeUpdate();
        }
    }

    public Batch getBatchById(int id) {
        String sql = "SELECT * FROM batch WHERE id = ?";
        Batch b = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                b = new Batch();
                b.setId(rs.getInt("id"));
                b.setProductId(rs.getInt("product_id"));
                b.setBatchNumber(rs.getString("batch_number"));
                b.setQuantity(rs.getInt("quantity"));
                b.setPurchasePrice(rs.getDouble("purchase_price"));

                Date exp = rs.getDate("expiry_date");
                if (exp != null) {
                    b.setExpiryDate(exp.toLocalDate());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    public List<Batch> getNearExpiryBatches(int days) {
        List<Batch> list = new ArrayList<>();

        String sql = "SELECT * FROM batch WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Batch b = new Batch();
                b.setId(rs.getInt("id"));
                b.setProductId(rs.getInt("product_id"));
                b.setBatchNumber(rs.getString("batch_number"));
                b.setQuantity(rs.getInt("quantity"));
                b.setPurchasePrice(rs.getDouble("purchase_price"));

                Date exp = rs.getDate("expiry_date");
                if (exp != null) {
                    b.setExpiryDate(exp.toLocalDate());
                }

                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Batch> getExpiredBatches() {
        List<Batch> list = new ArrayList<>();

        String sql = "SELECT * FROM batch WHERE expiry_date < CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Batch b = new Batch();
                b.setId(rs.getInt("id"));
                b.setProductId(rs.getInt("product_id"));
                b.setBatchNumber(rs.getString("batch_number"));
                b.setQuantity(rs.getInt("quantity"));
                b.setPurchasePrice(rs.getDouble("purchase_price"));

                Date exp = rs.getDate("expiry_date");
                if (exp != null) {
                    b.setExpiryDate(exp.toLocalDate());
                }

                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}