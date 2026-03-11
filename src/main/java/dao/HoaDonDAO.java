package dao;

import dto.HoaDonDTO;
import enums.TrangThaiGiaoDich;
import utils.JDBCUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HoaDonDAO {

    public List<HoaDonDTO> getAll() {
        List<HoaDonDTO> list = new ArrayList<>();
        String sql = "SELECT MaHD, MaNV, MaKH, MaKM, TongTien, TienGiam, ThanhTien, LoaiHoaDon, TrangThai, NgayTao FROM HoaDon";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDonDTO hoaDonDTO = new HoaDonDTO();
                hoaDonDTO.setMaHD(rs.getInt("MaHD"));

                int maNV = rs.getInt("MaNV");
                hoaDonDTO.setMaNV(rs.wasNull() ? null : maNV);

                int maKH = rs.getInt("MaKH");
                hoaDonDTO.setMaKH(rs.wasNull() ? null : maKH);

                int maKM = rs.getInt("MaKM");
                hoaDonDTO.setMaKM(rs.wasNull() ? null : maKM);

                hoaDonDTO.setTongTien(rs.getBigDecimal("TongTien"));
                hoaDonDTO.setTienGiam(rs.getBigDecimal("TienGiam"));
                hoaDonDTO.setThanhTien(rs.getBigDecimal("ThanhTien"));

                hoaDonDTO.setLoaiHoaDon(enums.LoaiHoaDon.valueOf(rs.getString("LoaiHoaDon")));
                hoaDonDTO.setTrangThai(enums.TrangThaiGiaoDich.valueOf(rs.getString("TrangThai")));

                java.sql.Timestamp tsNgayTao = rs.getTimestamp("NgayTao");
                if (tsNgayTao != null) {
                    hoaDonDTO.setNgayTao(tsNgayTao.toLocalDateTime());
                }

                list.add(hoaDonDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(HoaDonDTO hd) {
        String sql = "INSERT INTO HoaDon (MaNV, MaKH, MaKM, TongTien, TienGiam, LoaiHoaDon, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            if (hd.getMaNV() == null || hd.getMaNV() <= 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, hd.getMaNV());
            }

            if (hd.getMaKH() == null || hd.getMaKH() <= 0) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setInt(2, hd.getMaKH());

            if (hd.getMaKM() == null || hd.getMaKM() <= 0) ps.setNull(3, java.sql.Types.INTEGER);
            else ps.setInt(3, hd.getMaKM());

            ps.setBigDecimal(4, hd.getTongTien());
            ps.setBigDecimal(5, hd.getTienGiam());
            ps.setString(6, hd.getLoaiHoaDon().name());
            ps.setString(7, hd.getTrangThai().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(HoaDonDTO hoaDonDTO) {
        String sql = "UPDATE HoaDon SET MaNV = ?, MaKH = ?, MaKM = ?, TongTien = ?, TienGiam = ?, LoaiHoaDon = ?, TrangThai = ? WHERE MaHD = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hoaDonDTO.getMaNV() == null || hoaDonDTO.getMaNV() <= 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, hoaDonDTO.getMaNV());
            }

            if (hoaDonDTO.getMaKH() == null || hoaDonDTO.getMaKH() <= 0) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setInt(2, hoaDonDTO.getMaKH());

            if (hoaDonDTO.getMaKM() == null || hoaDonDTO.getMaKM() <= 0) ps.setNull(3, java.sql.Types.INTEGER);
            else ps.setInt(3, hoaDonDTO.getMaKM());

            ps.setBigDecimal(4, hoaDonDTO.getTongTien());
            ps.setBigDecimal(5, hoaDonDTO.getTienGiam());
            ps.setString(6, hoaDonDTO.getLoaiHoaDon().name());
            ps.setString(7, hoaDonDTO.getTrangThai().name());
            ps.setInt(8, hoaDonDTO.getMaHD());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maHD) {
        String sql = "UPDATE HoaDon SET TrangThai = ? WHERE MaHD = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enums.TrangThaiGiaoDich.DA_HUY.name());
            ps.setInt(2, maHD);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HoaDonDTO getById(int maHD) {
        String sql = "SELECT MaHD, MaNV, MaKH, MaKM, TongTien, TienGiam, ThanhTien, LoaiHoaDon, TrangThai, NgayTao FROM HoaDon WHERE MaHD = ?";
        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maHD);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.HoaDonDTO hd = new dto.HoaDonDTO();
                    hd.setMaHD(rs.getInt("MaHD"));

                    int maNV = rs.getInt("MaNV");
                    hd.setMaNV(rs.wasNull() ? null : maNV);

                    int maKH = rs.getInt("MaKH");
                    hd.setMaKH(rs.wasNull() ? null : maKH);

                    int maKM = rs.getInt("MaKM");
                    hd.setMaKM(rs.wasNull() ? null : maKM);

                    hd.setTongTien(rs.getBigDecimal("TongTien"));
                    hd.setTienGiam(rs.getBigDecimal("TienGiam"));
                    hd.setThanhTien(rs.getBigDecimal("ThanhTien"));
                    hd.setLoaiHoaDon(enums.LoaiHoaDon.valueOf(rs.getString("LoaiHoaDon")));
                    hd.setTrangThai(enums.TrangThaiGiaoDich.valueOf(rs.getString("TrangThai")));

                    java.sql.Timestamp tsNgayTao = rs.getTimestamp("NgayTao");
                    if (tsNgayTao != null) {
                        hd.setNgayTao(tsNgayTao.toLocalDateTime());
                    }
                    return hd;
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getDoanhThuHomNay() {
        String sql = "SELECT SUM(ThanhTien) AS DoanhThu FROM HoaDon WHERE TrangThai = 'HOAN_THANH' AND DATE(NgayTao) = CURDATE()";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                BigDecimal tongDoanhThu = rs.getBigDecimal("DoanhThu");
                return tongDoanhThu != null ? tongDoanhThu : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public int getSoDonHangMoi() {
        String sql = "SELECT COUNT(MaHD) AS SoDon FROM HoaDon WHERE TrangThai = 'CHO_XU_LY'";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("SoDon");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double[] getDoanhThuTheoTuanTrongThang() {
        double[] doanhThuTuan = new double[]{0, 0, 0, 0};
        // Lấy doanh thu từng ngày trong tháng hiện tại
        String sql = "SELECT DAY(NgayTao) AS Ngay, SUM(ThanhTien) AS TongTien FROM HoaDon " +
                "WHERE MONTH(NgayTao) = MONTH(CURDATE()) " +
                "AND YEAR(NgayTao) = YEAR(CURDATE()) " +
                "AND TrangThai = 'HOAN_THANH' " +
                "GROUP BY DAY(NgayTao)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int ngay = rs.getInt("Ngay");
                double tien = rs.getDouble("TongTien");
                if (ngay <= 7) doanhThuTuan[0] += tien;
                else if (ngay <= 14) doanhThuTuan[1] += tien;
                else if (ngay <= 21) doanhThuTuan[2] += tien;
                else doanhThuTuan[3] += tien;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return doanhThuTuan;
    }

    public BigDecimal getLoiNhuanHomNay() {
        BigDecimal loiNhuan = BigDecimal.ZERO;
        String sql = "SELECT SUM(hd.ThanhTien - COALESCE((" +
                "    SELECT SUM(ct.SoLuong * s.GiaGoc) " +
                "    FROM ChiTietHoaDon ct " +
                "    JOIN Sach s ON ct.MaSach = s.MaSach " +
                "    WHERE ct.MaHD = hd.MaHD" +
                "), 0)) AS TongLoiNhuan " +
                "FROM HoaDon hd " +
                "WHERE DATE(hd.NgayTao) = CURDATE() AND hd.TrangThai = 'HOAN_THANH'";

        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                loiNhuan = rs.getBigDecimal("TongLoiNhuan");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return loiNhuan != null ? loiNhuan : BigDecimal.ZERO;
    }

    public List<Object[]> getDonOnlinePending() {
        List<Object[]> list = new ArrayList<>();

        // 🔥 FIX LỖI Ở ĐÂY: Sửa điều kiện WHERE cho khớp chính xác với ENUM trong MySQL
        String sql = "SELECT hd.MaHD, kh.HoTen, hd.NgayTao, hd.TongTien, hd.TrangThai " +
                "FROM HoaDon hd " +
                "JOIN KhachHang kh ON hd.MaKH = kh.MaKH " +
                "WHERE hd.TrangThai = 'CHO_XU_LY'";

        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maHD = "HD" + String.format("%03d", rs.getInt("MaHD")); // Format cho đẹp: HD001, HD002
                String tenKH = rs.getString("HoTen");

                String ngayDat = "";
                if (rs.getTimestamp("NgayTao") != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    ngayDat = sdf.format(rs.getTimestamp("NgayTao"));
                }

                java.text.DecimalFormat df = new java.text.DecimalFormat("#,### đ");
                String tongTien = df.format(rs.getDouble("TongTien"));

                // Đổi chữ hiển thị trên UI cho thân thiện
                String trangThai = rs.getString("TrangThai").equals("CHO_XU_LY") ? "Chờ Xử Lý" : rs.getString("TrangThai");

                list.add(new Object[]{maHD, tenKH, ngayDat, tongTien, trangThai});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean duyetDonOnline(int maHD, String tenDangNhap) {
        java.sql.Connection conn = null;
        try {
            conn = utils.JDBCUtil.getConnection();
            conn.setAutoCommit(false);


            String sqlCTHD = "SELECT MaSach, SoLuong FROM ChiTietHoaDon WHERE MaHD = ?";
            try (java.sql.PreparedStatement psCTHD = conn.prepareStatement(sqlCTHD)) {
                psCTHD.setInt(1, maHD);
                try (java.sql.ResultSet rs = psCTHD.executeQuery()) {
                    while (rs.next()) {
                        int maSach = rs.getInt("MaSach");
                        int soLuongBan = rs.getInt("SoLuong");

                        String sqlUpdateKho = "UPDATE Sach SET SoLuongTon = SoLuongTon - ? WHERE MaSach = ? AND SoLuongTon >= ?";
                        try (java.sql.PreparedStatement psKho = conn.prepareStatement(sqlUpdateKho)) {
                            psKho.setInt(1, soLuongBan);
                            psKho.setInt(2, maSach);
                            psKho.setInt(3, soLuongBan);

                            int affectedRows = psKho.executeUpdate();
                            if (affectedRows == 0) {
                                throw new Exception("Không đủ số lượng tồn kho cho mã sách: " + maSach);
                            }
                        }
                    }
                }
            }
            String sqlHD = "UPDATE HoaDon SET TrangThai = 'HOAN_THANH', " +
                    "MaNV = (SELECT nv.MaNV FROM NhanVien nv JOIN TaiKhoan tk ON nv.MaTaiKhoan = tk.MaTaiKhoan WHERE tk.TenDangNhap = ?) " +
                    "WHERE MaHD = ?";
            try (java.sql.PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                psHD.setString(1, tenDangNhap);
                psHD.setInt(2, maHD);
                psHD.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean huyDonOnline(int maHD, String lyDo) {
        String sql = "UPDATE HoaDon SET TrangThai = 'DA_HUY', GhiChu = ? WHERE MaHD = ?";

        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lyDo);
            ps.setInt(2, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean insertDonHangOnline(int maTaiKhoan, Map<Integer, Integer> gioHang, BigDecimal tongTien) {
        Connection conn = null;
        PreparedStatement psFindKH = null;
        PreparedStatement psHoaDon = null;
        PreparedStatement psChiTiet = null;
        PreparedStatement psUpdateSach = null;
        ResultSet rs = null;

        // 1. CHỈNH LẠI CÂU SQL: Bỏ cột ThanhTien trong ChiTietHoaDon đi vì MySQL tự tính
        String sqlHoaDon = "INSERT INTO HoaDon (MaKH, NgayTao, TongTien, TrangThai) VALUES (?, CURRENT_TIMESTAMP, ?, 'CHO_XU_LY')";
        String sqlChiTiet = "INSERT INTO ChiTietHoaDon (MaHD, MaSach, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        String sqlUpdateSach = "UPDATE Sach SET SoLuongTon = SoLuongTon - ? WHERE MaSach = ?";
        String sqlLayGiaBan = "SELECT GiaBan FROM Sach WHERE MaSach = ?";

        try {
            conn = JDBCUtil.getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // ======================================================
            // BƯỚC 0: TÌM 'MaKH' TỪ 'MaTaiKhoan' (ĐỂ FIX LỖI FOREIGN KEY)
            // ======================================================
            int maKH_Chuan = -1;
            String sqlFindKH = "SELECT MaKH FROM KhachHang WHERE MaTaiKhoan = ?";
            psFindKH = conn.prepareStatement(sqlFindKH);
            psFindKH.setInt(1, maTaiKhoan);
            rs = psFindKH.executeQuery();
            if (rs.next()) {
                maKH_Chuan = rs.getInt("MaKH");
            } else {
                throw new SQLException("Tài khoản chưa có hồ sơ Khách Hàng. Hãy đăng ký nick mới để test!");
            }
            rs.close();
            psFindKH.close();

            // ======================================================
            // BƯỚC 1: TẠO HÓA ĐƠN MẸ
            // ======================================================
            psHoaDon = conn.prepareStatement(sqlHoaDon, Statement.RETURN_GENERATED_KEYS);
            // TRUYỀN MaKH CHUẨN VÀO ĐÂY
            psHoaDon.setInt(1, maKH_Chuan);
            psHoaDon.setBigDecimal(2, tongTien);

            int affectedRows = psHoaDon.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Lỗi tạo hóa đơn mẹ.");

            int maHDMoi = 0;
            rs = psHoaDon.getGeneratedKeys();
            if (rs.next()) maHDMoi = rs.getInt(1);
            rs.close();


            psChiTiet = conn.prepareStatement(sqlChiTiet);
            psUpdateSach = conn.prepareStatement(sqlUpdateSach);
            PreparedStatement psGia = conn.prepareStatement(sqlLayGiaBan);

            for (Map.Entry<Integer, Integer> entry : gioHang.entrySet()) {
                int maSach = entry.getKey();
                int soLuongMua = entry.getValue();

                psGia.setInt(1, maSach);
                ResultSet rsGia = psGia.executeQuery();
                BigDecimal donGia = BigDecimal.ZERO;
                if (rsGia.next()) donGia = rsGia.getBigDecimal("GiaBan");
                rsGia.close();

                // Chỉ truyền 4 tham số (Không có ThanhTien)
                psChiTiet.setInt(1, maHDMoi);
                psChiTiet.setInt(2, maSach);
                psChiTiet.setInt(3, soLuongMua);
                psChiTiet.setBigDecimal(4, donGia);
                psChiTiet.addBatch();

                psUpdateSach.setInt(1, soLuongMua);
                psUpdateSach.setInt(2, maSach);
                psUpdateSach.addBatch();
            }

            psChiTiet.executeBatch();
            psUpdateSach.executeBatch();
            psGia.close();

            conn.commit(); // THÀNH CÔNG!
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
                if (psUpdateSach != null) psUpdateSach.close();
                if (conn != null) { conn.setAutoCommit(true); conn.close(); }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

}