package dao;

import dto.NhanVienDTO;
import enums.TrangThaiTaiKhoan;
import utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    // --- HÀM MAPPING DỮ LIỆU (ĐÃ TỐI ƯU) ---
    private NhanVienDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaNV(rs.getInt("MaNV"));
        nv.setMaTaiKhoan(rs.getInt("MaTaiKhoan"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setSoDienThoai(rs.getString("SoDienThoai"));

        // Chỉ mapping các trường Tài Khoản nếu chúng tồn tại trong ResultSet (dùng cho các câu JOIN)
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String colName = metaData.getColumnName(i);
                if (colName.equalsIgnoreCase("TenDangNhap")) nv.setTenDangNhap(rs.getString("TenDangNhap"));
                if (colName.equalsIgnoreCase("MaQuyen")) nv.setMaQuyen(rs.getInt("MaQuyen"));
                if (colName.equalsIgnoreCase("TrangThai")) {
                    String tt = rs.getString("TrangThai");
                    if (tt != null) nv.setTrangThai(TrangThaiTaiKhoan.valueOf(tt));
                }
            }
        } catch (SQLException e) {
            // Log nhẹ nếu cần, hoặc bỏ qua nếu cột không tồn tại
        }
        return nv;
    }

    // --- LẤY TẤT CẢ (JOIN TÀI KHOẢN) ---
    public List<NhanVienDTO> getAll() {
        List<NhanVienDTO> list = new ArrayList<>();
        String sql = "SELECT nv.*, tk.TenDangNhap, tk.MaQuyen, tk.TrangThai " +
                "FROM NhanVien nv " +
                "JOIN TaiKhoan tk ON nv.MaTaiKhoan = tk.MaTaiKhoan " +
                "ORDER BY nv.MaNV DESC";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- TÌM THEO ID (JOIN TÀI KHOẢN) ---
    public NhanVienDTO getByID(int maNV) {
        String sql = "SELECT nv.*, tk.TenDangNhap, tk.MaQuyen, tk.TrangThai " +
                "FROM NhanVien nv " +
                "JOIN TaiKhoan tk ON nv.MaTaiKhoan = tk.MaTaiKhoan " +
                "WHERE nv.MaNV = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToDTO(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- TÌM KIẾM TỔNG HỢP (SQL LEVEL) ---
    public List<NhanVienDTO> search(String keyword) {
        List<NhanVienDTO> list = new ArrayList<>();
        String sql = "SELECT nv.*, tk.TenDangNhap, tk.MaQuyen, tk.TrangThai " +
                "FROM NhanVien nv " +
                "JOIN TaiKhoan tk ON nv.MaTaiKhoan = tk.MaTaiKhoan " +
                "WHERE nv.HoTen LIKE ? OR nv.SoDienThoai LIKE ? OR tk.TenDangNhap LIKE ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- THÊM NHÂN VIÊN ---
    public boolean insert(NhanVienDTO nv) {
        String sql = "INSERT INTO NhanVien (MaTaiKhoan, HoTen, SoDienThoai) VALUES (?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nv.getMaTaiKhoan());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getSoDienThoai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- CẬP NHẬT ---
    public boolean update(NhanVienDTO nv) {
        String sql = "UPDATE NhanVien SET HoTen = ?, SoDienThoai = ? WHERE MaNV = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getSoDienThoai());
            ps.setInt(3, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- KHÓA TÀI KHOẢN (SOFT DELETE) ---
    public boolean delete(int maNV) {
        String sql = "UPDATE TaiKhoan tk " +
                "INNER JOIN NhanVien nv ON tk.MaTaiKhoan = nv.MaTaiKhoan " +
                "SET tk.TrangThai = ? " +
                "WHERE nv.MaNV = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, TrangThaiTaiKhoan.KHOA.name());
            ps.setInt(2, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- KIỂM TRA TRÙNG LẶP ---
    public boolean checkSoDienThoaiDaTonTai(String sdt) {
        String sql = "SELECT 1 FROM NhanVien WHERE SoDienThoai = ? LIMIT 1";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean checkSoDienThoaiBiTrungKhiSua(String sdt, int maNVHienTai) {
        String sql = "SELECT 1 FROM NhanVien WHERE SoDienThoai = ? AND MaNV != ? LIMIT 1";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ps.setInt(2, maNVHienTai);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- HÀM TIỆN ÍCH: LẤY MÃ TÀI KHOẢN ---
    public int getMaTKByMaNV(int maNV) {
        String sql = "SELECT MaTaiKhoan FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("MaTaiKhoan");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}