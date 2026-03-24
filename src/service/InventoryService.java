package service;

import dao.BatchDAO;
import model.Batch;

import java.sql.Connection;
import java.sql.SQLException;

public class InventoryService {

    private BatchDAO batchDAO = new BatchDAO();

    public void reduceStock(Connection conn, int batchId, int quantity) throws SQLException {
        Batch batch = batchDAO.getBatchById(batchId);

        if (batch == null) {
            throw new SQLException("Batch not found");
        }

        int currentQty = batch.getQuantity();

        if (quantity > currentQty) {
            throw new SQLException("Insufficient stock");
        }

        int newQty = currentQty - quantity;

        batchDAO.updateBatchQuantity(conn, batchId, newQty);
    }
}