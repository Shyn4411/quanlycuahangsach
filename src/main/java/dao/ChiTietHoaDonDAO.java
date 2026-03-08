package dao;

import dto.ChiTietHoaDonDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    // Đã JOIN với bảng Sach để lấy được TenSach
    public List<ChiTietHoaDonDTO> getByMaHD(int maHD) {
        List<ChiTietHoaDonDTO> list = new ArrayList<>();
        String sql = "SELECT ct.MaCTHD, ct.MaHD, ct.MaSach, s.TenSach, ct.SoLuong, ct.DonGia, ct.ThanhTien " +
                "FROM ChiTietHoaDon ct " +
                "JOIN Sach s ON ct.MaSach = s.MaSach " +
                "WHERE ct.MaHD = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
                    dto.setMaCTHD(rs.getInt("MaCTHD"));
                    dto.setMaHD(rs.getInt("MaHD"));
                    dto.setMaSach(rs.getInt("MaSach"));
                    // Lấy Tên Sách từ câu JOIN
                    dto.setTenSach(rs.getString("TenSach"));
                    dto.setSoLuong(rs.getInt("SoLuong"));
                    dto.setDonGia(rs.getBigDecimal("DonGia"));
                    dto.setThanhTien(rs.getBigDecimal("ThanhTien"));

                    list.add(dto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChiTietHoaDonDTO chiTietHoaDonDTO) {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaSach, SoLuong, DonGia) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chiTietHoaDonDTO.getMaHD());
            ps.setInt(2, chiTietHoaDonDTO.getMaSach());
            ps.setInt(3, chiTietHoaDonDTO.getSoLuong());
            ps.setBigDecimal(4, chiTietHoaDonDTO.getDonGia());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}