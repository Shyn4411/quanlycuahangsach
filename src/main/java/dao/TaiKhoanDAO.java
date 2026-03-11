package dao;

import dto.TaiKhoanDTO;
import enums.TrangThaiTaiKhoan;
import utils.JDBCUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaiKhoanDAO {

    // =========================================================
    // 1. HÀM MAPPING (NGƯỜI PHIÊN DỊCH)
    // =========================================================
    private TaiKhoanDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        TaiKhoanDTO tk = new TaiKhoanDTO();
        tk.setMaTaiKhoan(rs.getInt("MaTaiKhoan"));
        tk.setMaQuyen(rs.getInt("MaQuyen"));
        tk.setTenDangNhap(rs.getString("TenDangNhap"));

        // Tránh lỗi nếu ResultSet không có cột MatKhau (trong hàm getAll)
        try {
            tk.setMatKhau(rs.getString("MatKhau"));
        } catch (SQLException e) {}

        tk.setTrangThai(TrangThaiTaiKhoan.valueOf(rs.getString("TrangThai")));

        if (rs.getTimestamp("NgayTao") != null) {
            tk.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
        }
        if (rs.getTimestamp("UpdatedAt") != null) {
            tk.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        }
        return tk;
    }

    // =========================================================
    // 2. CÁC HÀM TRUY VẤN (DÙNG MAPPING)
    // =========================================================

    public List<TaiKhoanDTO> getAll() {
        List<TaiKhoanDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public TaiKhoanDTO getByTenDangNhap(String username) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToDTO(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean checkTrungTenDangNhap(String username) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap = ? LIMIT 1";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // =========================================================
    // 3. CÁC HÀM THAY ĐỔI DỮ LIỆU (INSERT/UPDATE/DELETE)
    // =========================================================

    // Cần hàm này để lấy ID nhét vào bảng NhanVien
    public int insertAndGetId(TaiKhoanDTO tk) {
        String sql = "INSERT INTO TaiKhoan (MaQuyen, TenDangNhap, MatKhau, TrangThai) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tk.getMaQuyen());
            ps.setString(2, tk.getTenDangNhap());
            ps.setString(3, tk.getMatKhau());
            ps.setString(4, tk.getTrangThai().name());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public boolean updatePassword(int maTK, String newPass) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ?, UpdatedAt = CURRENT_TIMESTAMP WHERE MaTaiKhoan = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setInt(2, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateTrangThai(int maTK, TrangThaiTaiKhoan trangThai) {
        String sql = "UPDATE TaiKhoan SET TrangThai = ? WHERE MaTaiKhoan = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai.name());
            ps.setInt(2, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateQuyen(int maTK, int maQuyen) {
        String sql = "UPDATE TaiKhoan SET MaQuyen = ? WHERE MaTaiKhoan = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maQuyen);
            ps.setInt(2, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =========================================================
    // 4. HÀM ĐĂNG NHẬP ĐẶC BIỆT (CÓ JOIN)
    // =========================================================
    public TaiKhoanDTO login(String username, String password) {
        String sql = "SELECT tk.*, nv.MaNV, kh.MaKH " +
                "FROM TaiKhoan tk " +
                "LEFT JOIN NhanVien nv ON tk.MaTaiKhoan = nv.MaTaiKhoan " +
                "LEFT JOIN KhachHang kh ON tk.MaTaiKhoan = kh.MaTaiKhoan " +
                "WHERE tk.TenDangNhap = ? AND tk.MatKhau = ? AND tk.TrangThai = 'HOAT_DONG'";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Dùng mapping cơ bản
                    TaiKhoanDTO tk = mapResultSetToDTO(rs);

                    // Thêm phần ID liên kết đặc thù của login
                    int maNV = rs.getInt("MaNV");
                    if (!rs.wasNull()) tk.setMaNhanVien(maNV);

                    int maKH = rs.getInt("MaKH");
                    if (!rs.wasNull()) tk.setMaKhachHang(maKH);

                    return tk;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }


    public boolean checkUsernameExists(String username) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap = ?";
        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            java.sql.ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Thêm tài khoản mới vào Database
    public boolean insertDonHangOnline(int maTaiKhoan, Map<Integer, Integer> gioHang, BigDecimal tongTien) {
        Connection conn = null;
        PreparedStatement psFindKH = null;
        PreparedStatement psHoaDon = null;
        PreparedStatement psChiTiet = null;
        ResultSet rs = null;

        try {
            conn = utils.JDBCUtil.getConnection();
            conn.setAutoCommit(false); // 1. BẮT ĐẦU TRANSACTION

            // ======================================================
            // BƯỚC 0: TÌM 'MaKH' THỰC SỰ TỪ 'MaTaiKhoan'
            // ======================================================
            int maKH_Chuan = -1;
            String sqlFindKH = "SELECT MaKH FROM KhachHang WHERE MaTaiKhoan = ?";
            psFindKH = conn.prepareStatement(sqlFindKH);
            psFindKH.setInt(1, maTaiKhoan);
            rs = psFindKH.executeQuery();

            if (rs.next()) {
                maKH_Chuan = rs.getInt("MaKH");
            } else {
                // Nếu không tìm thấy khách, báo lỗi và Rollback ngay!
                throw new SQLException("Tài khoản này chưa có hồ sơ Khách Hàng. Không thể đặt đơn!");
            }
            rs.close();
            psFindKH.close();

            // ======================================================
            // BƯỚC 1: TẠO HÓA ĐƠN MẸ VỚI 'MaKH' VỪA TÌM ĐƯỢC
            // ======================================================
            String sqlHoaDon = "INSERT INTO HoaDon (MaKH, NgayTao, TongTien, TrangThai) VALUES (?, CURRENT_TIMESTAMP, ?, 'CHO_DUYET')";
            psHoaDon = conn.prepareStatement(sqlHoaDon, Statement.RETURN_GENERATED_KEYS);

            // 🔥 TRUYỀN CÁI MaKH CHUẨN VÀO ĐÂY 🔥
            psHoaDon.setInt(1, maKH_Chuan);
            psHoaDon.setBigDecimal(2, tongTien);

            int affectedRows = psHoaDon.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Lỗi tạo hóa đơn mẹ.");

            // Lấy MaHD vừa tạo để điền vào Chi Tiết Hóa Đơn
            int maHDMoi = 0;
            rs = psHoaDon.getGeneratedKeys();
            if (rs.next()) maHDMoi = rs.getInt(1);
            rs.close();

            // ======================================================
            // BƯỚC 2: TẠO CHI TIẾT HÓA ĐƠN (Giữ nguyên code cũ của ông)
            // ======================================================
            String sqlChiTiet = "INSERT INTO ChiTietHoaDon (MaHD, MaSach, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
            psChiTiet = conn.prepareStatement(sqlChiTiet);

            String sqlLayGiaBan = "SELECT GiaBan FROM Sach WHERE MaSach = ?";
            PreparedStatement psGia = conn.prepareStatement(sqlLayGiaBan);

            for (Map.Entry<Integer, Integer> entry : gioHang.entrySet()) {
                int maSach = entry.getKey();
                int soLuongMua = entry.getValue();

                psGia.setInt(1, maSach);
                ResultSet rsGia = psGia.executeQuery();
                BigDecimal donGia = BigDecimal.ZERO;
                if (rsGia.next()) donGia = rsGia.getBigDecimal("GiaBan");
                rsGia.close();

                psChiTiet.setInt(1, maHDMoi);
                psChiTiet.setInt(2, maSach);
                psChiTiet.setInt(3, soLuongMua);
                psChiTiet.setBigDecimal(4, donGia);
                // Bỏ cột ThanhTien đi vì Database của ông đã cài đặt nó là GENERATED ALWAYS AS (tự tính)
                psChiTiet.addBatch();
            }

            psChiTiet.executeBatch();
            psGia.close();

            conn.commit(); // Thành công!
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (psHoaDon != null) psHoaDon.close();
                if (psChiTiet != null) psChiTiet.close();
                if (conn != null) { conn.setAutoCommit(true); conn.close(); }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public boolean insert(TaiKhoanDTO tk) {
        Connection conn = null;
        try {
            conn = utils.JDBCUtil.getConnection();
            conn.setAutoCommit(false); // Bật chế độ Transaction

            // BƯỚC 1: TẠO TÀI KHOẢN
            String sqlTK = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, MaQuyen, TrangThai, NgayTao) VALUES (?, ?, ?, 'HOAT_DONG', CURRENT_TIMESTAMP)";
            PreparedStatement psTK = conn.prepareStatement(sqlTK, Statement.RETURN_GENERATED_KEYS);
            psTK.setString(1, tk.getTenDangNhap());
            psTK.setString(2, tk.getMatKhau()); // Nhớ là thực tế đi làm phải mã hóa pass nha
            psTK.setInt(3, tk.getMaQuyen());
            psTK.executeUpdate();

            // Lấy Mã Tài Khoản vừa sinh ra từ MySQL
            ResultSet rs = psTK.getGeneratedKeys();
            int maTaiKhoanMoi = 0;
            if (rs.next()) {
                maTaiKhoanMoi = rs.getInt(1);
            }

            // BƯỚC 2: TẠO PROFILE KHÁCH HÀNG KÈM THEO
            // Chỉ tạo nếu đăng ký là Khách Hàng (Giả sử Mã Quyền Khách Hàng = 4)
            if (tk.getMaQuyen() == 4) {
                // Điền tạm số điện thoại bằng thời gian hiện tại để không bị trùng khóa UNIQUE
                String fakePhone = String.valueOf(System.currentTimeMillis()).substring(3);

                String sqlKH = "INSERT INTO KhachHang (MaTaiKhoan, HoTen, SoDienThoai, TrangThai) VALUES (?, ?, ?, 'HOAT_DONG')";
                PreparedStatement psKH = conn.prepareStatement(sqlKH);
                psKH.setInt(1, maTaiKhoanMoi);
                psKH.setString(2, tk.getTenDangNhap()); // Tạm lấy tên nick làm họ tên
                psKH.setString(3, fakePhone);
                psKH.executeUpdate();
            }

            conn.commit(); // Hoàn tất 100%
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (Exception ex) {}
        }
        return false;
    }
}