package dao;

import db.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public Product findByNameOrBarcode(String keyword) {
        Product product = null;

        String sql = "SELECT * FROM product WHERE name = ? OR barcode = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, keyword);
            ps.setString(2, keyword);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setUnit(rs.getString("unit"));
                product.setSellingPrice(rs.getDouble("selling_price"));
                product.setBarcode(rs.getString("barcode"));
                product.setPrescriptionRequired(rs.getBoolean("prescription_required"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return product;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT * FROM product WHERE name LIKE ? OR barcode LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategory(rs.getString("category"));
                p.setUnit(rs.getString("unit"));
                p.setSellingPrice(rs.getDouble("selling_price"));
                p.setBarcode(rs.getString("barcode"));
                p.setPrescriptionRequired(rs.getBoolean("prescription_required"));

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertProduct(Product product) {
        String sql = "INSERT INTO product (name, category, unit, selling_price, barcode, prescription_required) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setString(3, product.getUnit());
            ps.setDouble(4, product.getSellingPrice());
            ps.setString(5, product.getBarcode());
            ps.setBoolean(6, product.isPrescriptionRequired());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product product) {
        String sql = "UPDATE product SET name = ?, category = ?, unit = ?, selling_price = ?, barcode = ?, prescription_required = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setString(3, product.getUnit());
            ps.setDouble(4, product.getSellingPrice());
            ps.setString(5, product.getBarcode());
            ps.setBoolean(6, product.isPrescriptionRequired());
            ps.setInt(7, product.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(int id) {
        String sql = "DELETE FROM product WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT * FROM product";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategory(rs.getString("category"));
                p.setUnit(rs.getString("unit"));
                p.setSellingPrice(rs.getDouble("selling_price"));
                p.setBarcode(rs.getString("barcode"));
                p.setPrescriptionRequired(rs.getBoolean("prescription_required"));

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}