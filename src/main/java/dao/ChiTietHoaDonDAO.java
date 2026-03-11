package dao;

import dto.ChiTietHoaDonDTO;
import utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    public List<ChiTietHoaDonDTO> getByMaHD(int maHD) {
        List<ChiTietHoaDonDTO> list = new ArrayList<>();
        String sql = "SELECT cthd.MaCTHD, cthd.MaHD, cthd.MaSach, s.TenSach, " +
                "cthd.SoLuong, cthd.DonGia, cthd.ThanhTien, " +
                "IFNULL(SUM(ctpt.SoLuong), 0) AS SoLuongDaTra " +
                "FROM ChiTietHoaDon cthd " +
                "JOIN Sach s ON cthd.MaSach = s.MaSach " +
                "LEFT JOIN PhieuTraKhachHang pt ON cthd.MaHD = pt.MaHD " +
                "LEFT JOIN ChiTietTraKhachHang ctpt ON pt.MaPTK = ctpt.MaPTK AND cthd.MaSach = ctpt.MaSach " +
                "WHERE cthd.MaHD = ? " +
                "GROUP BY cthd.MaCTHD, cthd.MaHD, cthd.MaSach, s.TenSach, cthd.SoLuong, cthd.DonGia, cthd.ThanhTien";

        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maHD);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDonDTO ct = new ChiTietHoaDonDTO();
                    ct.setMaCTHD(rs.getInt("MaCTHD"));
                    ct.setMaHD(rs.getInt("MaHD"));
                    ct.setMaSach(rs.getInt("MaSach"));
                    ct.setTenSach(rs.getString("TenSach"));
                    ct.setSoLuong(rs.getInt("SoLuong"));
                    ct.setDonGia(rs.getBigDecimal("DonGia"));
                    ct.setThanhTien(rs.getBigDecimal("ThanhTien"));

                    ct.setSoLuongDaTra(rs.getInt("SoLuongDaTra"));

                    list.add(ct);
                }
            }
        } catch (java.sql.SQLException e) {
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