package dao;

import utils.JDBCUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    // ========================================================
    // PHẦN 1: CÁC HÀM CHO 3 THẺ TỔNG KẾT (TỔNG VỐN, DOANH THU)
    // ========================================================

    // 1. LẤY TỔNG DOANH THU (Chỉ tính Hóa Đơn đã Hoàn Thành)
    public double getTongDoanhThu(String tuNgay, String denNgay) {
        double tong = 0;
        // Dùng TongTien cho thống nhất
        String sql = "SELECT SUM(TongTien) FROM HoaDon WHERE TrangThai = 'HoanThanh' AND NgayTao BETWEEN ? AND ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) tong = rs.getDouble(1);

        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    // 2. LẤY TỔNG VỐN NHẬP (Chỉ tính Phiếu Nhập đã Hoàn Thành)
    public double getTongVon(String tuNgay, String denNgay) {
        double tong = 0;
        String sql = "SELECT SUM(TongTien) FROM PhieuNhap WHERE TrangThai = 'HoanThanh' AND NgayTao BETWEEN ? AND ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) tong = rs.getDouble(1);

        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    // ========================================================
    // PHẦN 2: CÁC HÀM CHO BẢNG THỐNG KÊ CHI TIẾT
    // ========================================================

    // 1. LẤY TOP 10 SÁCH BÁN CHẠY
    public List<Object[]> getTopSachBanChay(String tuNgay, String denNgay) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.MaSach, s.TenSach, SUM(ct.SoLuong) AS TongBan, SUM(ct.ThanhTien) AS DoanhThu " +
                "FROM ChiTietHoaDon ct " +
                "JOIN Sach s ON ct.MaSach = s.MaSach " +
                "JOIN HoaDon hd ON ct.MaHD = hd.MaHD " +
                "WHERE hd.TrangThai = 'HoanThanh' AND hd.NgayTao BETWEEN ? AND ? " +
                "GROUP BY s.MaSach, s.TenSach " +
                "ORDER BY TongBan DESC LIMIT 10";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);

            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                list.add(new Object[]{
                        rank++,
                        "S" + String.format("%03d", rs.getInt("MaSach")), // Format mã sách S001
                        rs.getString("TenSach"),
                        rs.getInt("TongBan"),
                        rs.getDouble("DoanhThu")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 2. THỐNG KÊ DOANH THU THEO THÁNG (Gộp Hóa Đơn và Phiếu Nhập)
    public List<Object[]> getDoanhThuTheoThang(String tuNgay, String denNgay) {
        List<Object[]> list = new ArrayList<>();
        // Sử dụng UNION ALL để gộp dữ liệu từ 2 bảng, sau đó SUM lại theo Tháng/Năm
        String sql = "SELECT ThangNam, SUM(Von) AS TongVon, SUM(DoanhThu) AS TongDoanhThu, (SUM(DoanhThu) - SUM(Von)) AS LoiNhuan " +
                "FROM (" +
                "   SELECT DATE_FORMAT(NgayTao, '%m/%Y') AS ThangNam, 0 AS Von, TongTien AS DoanhThu " +
                "   FROM HoaDon WHERE TrangThai = 'HoanThanh' AND NgayTao BETWEEN ? AND ? " +
                "   UNION ALL " +
                "   SELECT DATE_FORMAT(NgayTao, '%m/%Y') AS ThangNam, TongTien AS Von, 0 AS DoanhThu " +
                "   FROM PhieuNhap WHERE TrangThai = 'HoanThanh' AND NgayTao BETWEEN ? AND ? " +
                ") AS ThongKeThang " +
                "GROUP BY ThangNam " +
                "ORDER BY STR_TO_DATE(ThangNam, '%m/%Y') ASC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            ps.setString(3, tuNgay);
            ps.setString(4, denNgay);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("ThangNam"),
                        rs.getDouble("TongVon"),
                        rs.getDouble("TongDoanhThu"),
                        rs.getDouble("LoiNhuan")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 3. LẤY SÁCH SẮP HẾT HÀNG (Tồn kho < 10)
    // 3. LẤY SÁCH SẮP HẾT HÀNG (Tồn kho < 10)
    public List<Object[]> getSachSapHet() {
        List<Object[]> list = new ArrayList<>();

        // Dùng LEFT JOIN để lỡ cuốn sách nào Tủn chưa kịp gán Thể loại hoặc NXB thì nó vẫn hiện ra được
        String sql = "SELECT s.MaSach, s.TenSach, tl.TenLoai, nxb.TenNXB, s.SoLuongTon " +
                "FROM Sach s " +
                "LEFT JOIN TheLoai tl ON s.MaLoai = tl.MaLoai " +
                "LEFT JOIN NhaXuatBan nxb ON s.MaNXB = nxb.MaNXB " +
                "WHERE s.SoLuongTon < 10 AND s.TrangThai = 'DangBan' " +
                "ORDER BY s.SoLuongTon ASC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        "S" + String.format("%03d", rs.getInt("MaSach")),
                        rs.getString("TenSach"),
                        // Thêm check null lỡ sách chưa gán Loại/NXB
                        rs.getString("TenLoai") != null ? rs.getString("TenLoai") : "Chưa phân loại",
                        rs.getString("TenNXB") != null ? rs.getString("TenNXB") : "Chưa có NXB",
                        rs.getInt("SoLuongTon")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}