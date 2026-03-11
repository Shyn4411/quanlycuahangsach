package dao;

import utils.JDBCUtil;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CauHinhDAO {
    public Map<String, String> getAll() {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT MaThamSo, GiaTri FROM CauHinh";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("MaThamSo"), rs.getString("GiaTri"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public boolean update(String key, String value) {
        String sql = "UPDATE CauHinh SET GiaTri = ? WHERE MaThamSo = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, key);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}