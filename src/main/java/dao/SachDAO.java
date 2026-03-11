package dao;

import dto.SachDTO;
import enums.TrangThaiSach;
import utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SachDAO {

    public List<SachDTO> getAll() {
        List<SachDTO> list = new ArrayList<>();

        // NÂNG CẤP: Lấy thêm tl.TenLoai và nxb.TenNXB
        String sql = "SELECT s.MaSach, s.TenSach, s.MaLoai, s.MaNXB, s.HinhAnh, " +
                "s.GiaGoc, s.GiaBan, s.SoLuongTon, s.SoLuongLoi, s.TrangThai, s.NgayTao, s.UpdatedAt, " +
                "tl.TenLoai, nxb.TenNXB, " +
                "GROUP_CONCAT(tg.TenTacGia SEPARATOR ', ') AS DanhSachTacGia " +
                "FROM Sach s " +
                "LEFT JOIN TacGia_Sach tgs ON s.MaSach = tgs.MaSach " +
                "LEFT JOIN TacGia tg ON tgs.MaTacGia = tg.MaTacGia " +
                "LEFT JOIN TheLoai tl ON s.MaLoai = tl.MaLoai " +       // Kéo bảng Thể Loại
                "LEFT JOIN NhaXuatBan nxb ON s.MaNXB = nxb.MaNXB " +    // Kéo bảng NXB
                "GROUP BY s.MaSach";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SachDTO sachDTO = new SachDTO();
                sachDTO.setMaSach(rs.getInt("MaSach"));
                sachDTO.setTenSach(rs.getString("TenSach"));

                int maLoai = rs.getInt("MaLoai");
                sachDTO.setMaLoai(rs.wasNull() ? null : maLoai);

                int maNXB = rs.getInt("MaNXB");
                sachDTO.setMaNXB(rs.wasNull() ? null : maNXB);

                sachDTO.setHinhAnh(rs.getString("HinhAnh"));
                sachDTO.setGiaGoc(rs.getBigDecimal("GiaGoc"));
                sachDTO.setGiaBan(rs.getBigDecimal("GiaBan"));
                sachDTO.setSoLuongTon(rs.getInt("SoLuongTon"));
                sachDTO.setSoLuongLoi(rs.getInt("SoLuongLoi"));
                sachDTO.setTrangThai(enums.TrangThaiSach.valueOf(rs.getString("TrangThai")));
                sachDTO.setDanhSachTacGia(rs.getString("DanhSachTacGia"));

                // HỨNG DỮ LIỆU MỚI TỪ DB LÊN DTO
                sachDTO.setTenLoai(rs.getString("TenLoai"));
                sachDTO.setTenNXB(rs.getString("TenNXB"));

                if (rs.getTimestamp("NgayTao") != null) {
                    sachDTO.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
                }
                if (rs.getTimestamp("UpdatedAt") != null) {
                    sachDTO.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
                }

                list.add(sachDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(SachDTO sachDTO) {
        String sql = "INSERT INTO Sach (TenSach, MaLoai, MaNXB, HinhAnh, GiaGoc, GiaBan, SoLuongTon, SoLuongLoi, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sachDTO.getTenSach());
            ps.setInt(2, sachDTO.getMaLoai());
            ps.setInt(3, sachDTO.getMaNXB());
            ps.setString(4, sachDTO.getHinhAnh());
            ps.setBigDecimal(5, sachDTO.getGiaGoc());
            ps.setBigDecimal(6, sachDTO.getGiaBan());
            ps.setInt(7, sachDTO.getSoLuongTon());
            ps.setInt(8, sachDTO.getSoLuongLoi());
            ps.setString(9, sachDTO.getTrangThai().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về mã sách vừa tự tăng
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(SachDTO sachDTO) {
        String sql = "UPDATE Sach SET TenSach = ?, MaLoai = ?, MaNXB = ?, HinhAnh = ?, GiaGoc = ?, GiaBan = ?, SoLuongTon = ?, SoLuongLoi = ?, TrangThai = ? WHERE MaSach = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sachDTO.getTenSach());
            ps.setInt(2, sachDTO.getMaLoai());
            ps.setInt(3, sachDTO.getMaNXB());
            ps.setString(4, sachDTO.getHinhAnh());
            ps.setBigDecimal(5, sachDTO.getGiaGoc());
            ps.setBigDecimal(6, sachDTO.getGiaBan());
            ps.setInt(7, sachDTO.getSoLuongTon());
            ps.setInt(8, sachDTO.getSoLuongLoi());
            ps.setString(9, sachDTO.getTrangThai().name());

            ps.setInt(10, sachDTO.getMaSach());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maSach) {
        String sql = "UPDATE Sach SET TrangThai = ? WHERE MaSach = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiSach.NGUNG_BAN.name());
            ps.setInt(2, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean truTonKho(int maSach, int soLuongMua) {
        String sql = "UPDATE Sach SET SoLuongTon = SoLuongTon - ? WHERE MaSach = ? AND SoLuongTon >= ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soLuongMua);
            ps.setInt(2, maSach);
            ps.setInt(3, soLuongMua);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean congTonKho(int maSach, int soLuongTraLai) {
        String sql = "UPDATE Sach SET SoLuongTon = SoLuongTon + ? WHERE MaSach = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuongTraLai);
            ps.setInt(2, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public SachDTO getById(int maSach) {
        String sql = "SELECT MaSach, TenSach, MaLoai, MaNXB, HinhAnh, GiaGoc, GiaBan, SoLuongTon, SoLuongLoi, TrangThai FROM Sach WHERE MaSach = ?";

        try (Connection conn = utils.JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SachDTO s = new SachDTO();
                    s.setMaSach(rs.getInt("MaSach"));
                    s.setTenSach(rs.getString("TenSach"));

                    int maLoai = rs.getInt("MaLoai");
                    s.setMaLoai(rs.wasNull() ? null : maLoai);

                    int maNXB = rs.getInt("MaNXB");
                    s.setMaNXB(rs.wasNull() ? null : maNXB);

                    s.setHinhAnh(rs.getString("HinhAnh"));
                    s.setGiaGoc(rs.getBigDecimal("GiaGoc"));
                    s.setGiaBan(rs.getBigDecimal("GiaBan"));
                    s.setSoLuongTon(rs.getInt("SoLuongTon"));
                    s.setSoLuongLoi(rs.getInt("SoLuongLoi"));
                    s.setTrangThai(enums.TrangThaiSach.valueOf(rs.getString("TrangThai")));

                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getSoLuongSachSapHet(int gioiHan) {
        // Đếm sách đang bán mà tồn kho dưới mức giới hạn
        String sql = "SELECT COUNT(MaSach) AS SoLuong FROM Sach " +
                "WHERE TrangThai = 'DANG_BAN' AND SoLuongTon < ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gioiHan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("SoLuong");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Map<String, Integer> getTopSachBanChay(int limit) {
        Map<String, Integer> mapTopSach = new LinkedHashMap<>();
        String sql = "SELECT s.TenSach, SUM(ct.SoLuong) AS TongBan " +
                "FROM ChiTietHoaDon ct " +
                "JOIN HoaDon hd ON ct.MaHD = hd.MaHD " +
                "JOIN Sach s ON ct.MaSach = s.MaSach " +
                "WHERE hd.TrangThai = 'HOAN_THANH' " +
                "AND MONTH(hd.NgayTao) = MONTH(CURDATE()) " +
                "AND YEAR(hd.NgayTao) = YEAR(CURDATE()) " +
                "GROUP BY s.MaSach, s.TenSach " +
                "ORDER BY TongBan DESC " +
                "LIMIT ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mapTopSach.put(rs.getString("TenSach"), rs.getInt("TongBan"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return mapTopSach;
    }
}