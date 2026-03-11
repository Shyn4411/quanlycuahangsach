package dao;

import dto.KhuyenMaiDTO;
import enums.TrangThaiKhuyenMai;
import utils.JDBCUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    public List<KhuyenMaiDTO> getAll() {

        List<KhuyenMaiDTO> list = new ArrayList<>();
        String sql = "SELECT MaKM, MaCode, TenKM, PhanTramGiam, SoTienGiam, DonHangToiThieu, NgayBatDau, NgayKetThuc, TrangThai FROM KhuyenMai";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhuyenMaiDTO khuyenMaiDTO = new KhuyenMaiDTO();
                khuyenMaiDTO.setMaKM(rs.getInt("MaKM"));
                khuyenMaiDTO.setMaCode(rs.getString("MaCode"));
                khuyenMaiDTO.setTenKM(rs.getString("TenKM"));
                khuyenMaiDTO.setPhanTramGiam(rs.getBigDecimal("PhanTramGiam"));
                khuyenMaiDTO.setSoTienGiam(rs.getBigDecimal("SoTienGiam"));
                khuyenMaiDTO.setDonHangToiThieu(rs.getBigDecimal("DonHangToiThieu"));
                khuyenMaiDTO.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
                khuyenMaiDTO.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                khuyenMaiDTO.setTrangThai(
                        enums.TrangThaiKhuyenMai.valueOf(rs.getString("TrangThai"))
                );

                list.add(khuyenMaiDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean insert(KhuyenMaiDTO khuyenMaiDTO) {
        String sql = "INSERT INTO KhuyenMai (MaCode, TenKM, PhanTramGiam, SoTienGiam, DonHangToiThieu, NgayBatDau, NgayKetThuc, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, khuyenMaiDTO.getMaCode());
            ps.setString(2, khuyenMaiDTO.getTenKM());
            ps.setBigDecimal(3, khuyenMaiDTO.getPhanTramGiam());
            ps.setBigDecimal(4, khuyenMaiDTO.getSoTienGiam());
            ps.setBigDecimal(5, khuyenMaiDTO.getDonHangToiThieu());
            ps.setDate(6, Date.valueOf(khuyenMaiDTO.getNgayBatDau()));
            ps.setDate(7, Date.valueOf(khuyenMaiDTO.getNgayKetThuc()));

            ps.setString(8, khuyenMaiDTO.getTrangThai().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KhuyenMaiDTO khuyenMaiDTO) {
        String sql = "UPDATE KhuyenMai SET MaCode = ?, TenKM = ?, PhanTramGiam = ?, SoTienGiam = ?, DonHangToiThieu = ?, NgayBatDau = ?, NgayKetThuc = ?, TrangThai = ? WHERE MaKM = ?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, khuyenMaiDTO.getMaCode());
            ps.setString(2, khuyenMaiDTO.getTenKM());
            ps.setBigDecimal(3, khuyenMaiDTO.getPhanTramGiam());
            ps.setBigDecimal(4, khuyenMaiDTO.getSoTienGiam());
            ps.setBigDecimal(5, khuyenMaiDTO.getDonHangToiThieu());
            ps.setDate(6, Date.valueOf(khuyenMaiDTO.getNgayBatDau()));
            ps.setDate(7, Date.valueOf(khuyenMaiDTO.getNgayKetThuc()));
            ps.setString(8, khuyenMaiDTO.getTrangThai().name());

            ps.setInt(9, khuyenMaiDTO.getMaKM());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean delete(int maKM) {
        String sql = "UPDATE KhuyenMai SET TrangThai = ? WHERE MaKM = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiKhuyenMai.HET_HAN.name());
            ps.setInt(2, maKM);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean checkMaCodeDaTonTai(String maCode) {
        String sql = "SELECT MaKM FROM KhuyenMai WHERE MaCode = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkMaCodeBiTrungKhiSua(String maCode, int maKMHienTai) {
        String sql = "SELECT MaKM FROM KhuyenMai WHERE MaCode = ? AND MaKM != ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCode);
            ps.setInt(2, maKMHienTai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }





}
