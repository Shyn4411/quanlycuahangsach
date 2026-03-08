package dao;

import dto.SachDTO;
import utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SachDAO {

    public List<SachDTO> getAll() {
        List<SachDTO> list = new ArrayList<>();
        String sql = "SELECT s.MaSach, s.TenSach, s.MaLoai, s.MaNXB, s.HinhAnh, " +
                "s.GiaGoc, s.GiaBan, s.SoLuongTon, s.SoLuongLoi, s.TrangThai, s.NgayTao, s.UpdatedAt, " +
                "GROUP_CONCAT(tg.TenTacGia SEPARATOR ', ') AS DanhSachTacGia " +
                "FROM Sach s " +
                "LEFT JOIN TacGia_Sach tgs ON s.MaSach = tgs.MaSach " +
                "LEFT JOIN TacGia tg ON tgs.MaTacGia = tg.MaTacGia " +
                "GROUP BY s.MaSach";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SachDTO sachDTO = new SachDTO();
                sachDTO.setMaSach(rs.getInt("MaSach"));
                sachDTO.setTenSach(rs.getString("TenSach"));
                sachDTO.setMaLoai(rs.getInt("MaLoai"));
                sachDTO.setMaNXB(rs.getInt("MaNXB"));
                sachDTO.setHinhAnh(rs.getString("HinhAnh"));
                sachDTO.setGiaGoc(rs.getBigDecimal("GiaGoc"));
                sachDTO.setGiaBan(rs.getBigDecimal("GiaBan"));
                sachDTO.setSoLuongTon(rs.getInt("SoLuongTon"));
                sachDTO.setSoLuongLoi(rs.getInt("SoLuongLoi"));
                sachDTO.setTrangThai(enums.TrangThaiSach.valueOf(rs.getString("TrangThai")));
                sachDTO.setDanhSachTacGia(rs.getString("DanhSachTacGia"));

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

            // FIX: Căn chỉnh lại cho đúng 10 dấu ?
            ps.setString(1, sachDTO.getTenSach());
            ps.setInt(2, sachDTO.getMaLoai());
            ps.setInt(3, sachDTO.getMaNXB());
            ps.setString(4, sachDTO.getHinhAnh());
            ps.setBigDecimal(5, sachDTO.getGiaGoc());
            ps.setBigDecimal(6, sachDTO.getGiaBan());
            ps.setInt(7, sachDTO.getSoLuongTon());
            ps.setInt(8, sachDTO.getSoLuongLoi());
            ps.setString(9, sachDTO.getTrangThai().name());

            ps.setInt(10, sachDTO.getMaSach()); // WHERE MaSach = ?

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

            ps.setString(1, enums.TrangThaiSach.NgungBan.name());
            ps.setInt(2, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean truTonKho(int maSach, int soLuongMua) {
        // FIX: Đổi SoLuongMua thành SoLuongTon
        String sql = "UPDATE Sach SET SoLuongTon = SoLuongTon - ? WHERE MaSach = ? AND SoLuongTon >= ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soLuongMua);
            ps.setInt(2, maSach);
            ps.setInt(3, soLuongMua); // Đảm bảo Kho >= Lượng mua mới cho trừ

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
}