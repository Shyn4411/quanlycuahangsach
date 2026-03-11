package dao;

import utils.JDBCUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public double getTongDoanhThu(String tu, String den) {
        double t = 0;
        // Fix chữ HOAN_THANH
        String sql = "SELECT SUM(ThanhTien) FROM HoaDon WHERE TrangThai = 'HOAN_THANH' AND NgayTao BETWEEN ? AND ?";
        try (Connection c = JDBCUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            // Fix lấy trọn vẹn ngày cuối cùng
            p.setString(1, tu + " 00:00:00");
            p.setString(2, den + " 23:59:59");
            ResultSet r = p.executeQuery(); if (r.next()) t = r.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return t;
    }

    public double getTongVon(String tu, String den) {
        double t = 0;
        // Fix chữ HOAN_THANH
        String sql = "SELECT SUM(TongTien) FROM PhieuNhap WHERE TrangThai = 'HOAN_THANH' AND NgayTao BETWEEN ? AND ?";
        try (Connection c = JDBCUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, tu + " 00:00:00");
            p.setString(2, den + " 23:59:59");
            ResultSet r = p.executeQuery(); if (r.next()) t = r.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return t;
    }

    public List<Object[]> getDoanhThuTheoThang(String tu, String den) {
        List<Object[]> list = new ArrayList<>();
        // Fix Enum và gộp bảng để tính Lợi Nhuận
        String sql = "SELECT ThangNam, SUM(Von) AS V, SUM(DoanhThu) AS D, (SUM(DoanhThu) - SUM(Von)) AS L " +
                "FROM (SELECT DATE_FORMAT(NgayTao, '%m/%Y') AS ThangNam, 0 AS Von, ThanhTien AS DoanhThu FROM HoaDon " +
                "WHERE TrangThai = 'HOAN_THANH' AND NgayTao BETWEEN ? AND ? " +
                "UNION ALL SELECT DATE_FORMAT(NgayTao, '%m/%Y') AS ThangNam, TongTien AS Von, 0 AS DoanhThu FROM PhieuNhap " +
                "WHERE TrangThai = 'HOAN_THANH' AND NgayTao BETWEEN ? AND ?) AS T GROUP BY ThangNam ORDER BY STR_TO_DATE(ThangNam, '%m/%Y')";
        try (Connection c = JDBCUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            String start = tu + " 00:00:00";
            String end = den + " 23:59:59";
            p.setString(1, start); p.setString(2, end); p.setString(3, start); p.setString(4, end);
            ResultSet r = p.executeQuery();
            while (r.next()) list.add(new Object[]{r.getString(1), r.getDouble(2), r.getDouble(3), r.getDouble(4)});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Object[]> getSachSapHet() {
        List<Object[]> list = new ArrayList<>();
        // Fix chữ DANG_BAN
        String sql = "SELECT s.MaSach, s.TenSach, tl.TenLoai, nxb.TenNXB, s.SoLuongTon FROM Sach s " +
                "LEFT JOIN TheLoai tl ON s.MaLoai = tl.MaLoai LEFT JOIN NhaXuatBan nxb ON s.MaNXB = nxb.MaNXB " +
                "WHERE s.SoLuongTon < 10 AND s.TrangThai = 'DANG_BAN' ORDER BY s.SoLuongTon ASC";
        try (Connection c = JDBCUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(new Object[]{"S" + String.format("%03d", r.getInt(1)), r.getString(2), r.getString(3), r.getString(4), r.getInt(5)});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}