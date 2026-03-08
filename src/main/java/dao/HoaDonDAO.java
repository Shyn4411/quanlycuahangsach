package dao;

import dto.HoaDonDTO;
import enums.TrangThaiGiaoDich;
import utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                hoaDonDTO.setMaNV(rs.getInt("MaNV"));

                // Tránh lỗi khi getInt từ giá trị NULL
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
                ps.setNull(1, java.sql.Types.INTEGER); // Admin bán -> Null
            } else {
                ps.setInt(1, hd.getMaNV()); // Nhân viên bán -> Mã NV
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
        // FIX CỰC MẠNH: Xóa ThanhTien ra khỏi câu lệnh UPDATE vì DB tự tính
        String sql = "UPDATE HoaDon SET MaNV = ?, MaKH = ?, MaKM = ?, TongTien = ?, TienGiam = ?, LoaiHoaDon = ?, TrangThai = ? WHERE MaHD = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hoaDonDTO.getMaNV());

            // FIX: Check null an toàn giống hàm insert
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

            ps.setString(1, enums.TrangThaiGiaoDich.DaHuy.name());
            ps.setInt(2, maHD);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}