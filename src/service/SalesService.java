package service;

import dao.BatchDAO;
import model.Batch;
import model.Sale;
import model.SaleItem;

import java.sql.*;
import java.util.List;

public class SalesService {

    private BatchDAO batchDAO = new BatchDAO();

    public void processSale(Connection conn, Sale sale, List<SaleItem> items) throws Exception {

        String saleSql = "INSERT INTO sale (date, total, payment_method) VALUES (?, ?, ?)";
        int saleId;

        try (PreparedStatement ps = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, new Date(sale.getDate().getTime()));
            ps.setDouble(2, sale.getTotal());
            ps.setString(3, sale.getPaymentMethod());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                saleId = rs.getInt(1);
            } else {
                throw new Exception("Failed to create sale");
            }
        }

        String itemSql = "INSERT INTO sale_item (sale_id, product_id, batch_id, quantity, price, total) VALUES (?, ?, ?, ?, ?, ?)";

        for (SaleItem item : items) {

            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                ps.setInt(1, saleId);
                ps.setInt(2, item.getProductId());
                ps.setInt(3, item.getBatchId());
                ps.setInt(4, item.getQuantity());
                ps.setDouble(5, item.getPrice());
                ps.setDouble(6, item.getTotal());
                ps.executeUpdate();
            }

            Batch batch = batchDAO.getBatchById(item.getBatchId());
            int newQty = batch.getQuantity() - item.getQuantity();

            if (newQty < 0) {
                throw new Exception("Stock error");
            }

            batchDAO.updateBatchQuantity(conn, item.getBatchId(), newQty);
        }
    }
}