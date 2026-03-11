package dao;

import dto.ChiTietPhieuNhapDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhapDAO {

    public List<ChiTietPhieuNhapDTO> getByMaPN(int maPN) {
        List<ChiTietPhieuNhapDTO> list = new ArrayList<>();
        String sql = "SELECT MaCTPN, MaPN, MaSach, SoLuong, GiaNhap, ThanhTien FROM ChiTietPhieuNhap WHERE MaPN = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPN);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
                    ct.setMaCTPN(rs.getInt("MaCTPN"));
                    ct.setMaPN(rs.getInt("MaPN"));
                    ct.setMaSach(rs.getInt("MaSach"));
                    ct.setSoLuong(rs.getInt("SoLuong"));
                    ct.setGiaNhap(rs.getBigDecimal("GiaNhap"));

                    ct.setThanhTien(rs.getBigDecimal("ThanhTien"));

                    list.add(ct);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChiTietPhieuNhapDTO ct) {
        String sql = "INSERT INTO ChiTietPhieuNhap (MaPN, MaSach, SoLuong, GiaNhap) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ct.getMaPN());
            ps.setInt(2, ct.getMaSach());
            ps.setInt(3, ct.getSoLuong());
            ps.setBigDecimal(4, ct.getGiaNhap());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}