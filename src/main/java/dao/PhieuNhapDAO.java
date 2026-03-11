package dao;

import dto.PhieuNhapDTO;
import enums.TrangThaiGiaoDich;
import utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDAO {

    public List<PhieuNhapDTO> getAll() {
        List<PhieuNhapDTO> list = new ArrayList<>();
        String sql = "SELECT pn.*, ncc.TenNCC, nv.HoTen " +
                "FROM PhieuNhap pn " +
                "JOIN NhaCungCap ncc ON pn.MaNCC = ncc.MaNCC " +
                "JOIN NhanVien nv ON pn.MaNV = nv.MaNV " +
                "ORDER BY pn.MaPN DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieuNhapDTO pn = new PhieuNhapDTO();
                pn.setMaPN(rs.getInt("MaPN"));
                pn.setMaNV(rs.getInt("MaNV"));
                pn.setMaNCC(rs.getInt("MaNCC"));
                pn.setTenNCC(rs.getString("TenNCC"));
                pn.setTenNV(rs.getString("HoTen"));

                pn.setTongTien(rs.getBigDecimal("TongTien"));
                pn.setTrangThai(enums.TrangThaiGiaoDich.valueOf(rs.getString("TrangThai")));

                if (rs.getTimestamp("NgayTao") != null) {
                    pn.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
                }
                list.add(pn);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int insert(PhieuNhapDTO phieuNhapDTO) {
        String sql = "INSERT INTO PhieuNhap (MaNV, MaNCC, TongTien, TrangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, phieuNhapDTO.getMaNV());
            ps.setInt(2, phieuNhapDTO.getMaNCC());
            ps.setBigDecimal(3, phieuNhapDTO.getTongTien());
            ps.setString(4, phieuNhapDTO.getTrangThai().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public boolean update(PhieuNhapDTO phieuNhapDTO) {
        String sql = "UPDATE PhieuNhap SET MaNV = ?, MaNCC = ?, TongTien = ?, TrangThai = ? WHERE MaPN = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, phieuNhapDTO.getMaNV());
            ps.setInt(2, phieuNhapDTO.getMaNCC());
            ps.setBigDecimal(3, phieuNhapDTO.getTongTien());
            ps.setString(4, phieuNhapDTO.getTrangThai().name());
            ps.setInt(5, phieuNhapDTO.getMaPN());
            return ps.executeUpdate() > 0;
        }  catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int maPN) {
        String sql = "UPDATE PhieuNhap SET TrangThai = ? WHERE MaPN = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, TrangThaiGiaoDich.DA_HUY.name());
            ps.setInt(2, maPN);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateTrangThai(int maPN, String trangThaiMoi) {
        String sql = "UPDATE PhieuNhap SET TrangThai = ? WHERE MaPN = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maPN);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}