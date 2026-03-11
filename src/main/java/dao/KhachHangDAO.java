package dao;

import dto.KhachHangDTO;
import enums.TrangThaiCoBan;
import enums.TrangThaiTaiKhoan;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHangDTO> getAll() {
        List<KhachHangDTO> list = new ArrayList<>();
        String sql = "SELECT MaKH, HoTen, SoDienThoai, DiaChi, DiemTichLuy, TrangThai, NgayTao, UpdatedAt FROM KhachHang";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHangDTO khachHangDTO = new KhachHangDTO();
                khachHangDTO.setMaKH(rs.getInt("MaKH"));
                khachHangDTO.setHoTen(rs.getString("HoTen"));
                khachHangDTO.setSoDienThoai(rs.getString("SoDienThoai"));
                khachHangDTO.setDiaChi(rs.getString("DiaChi"));
                khachHangDTO.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                khachHangDTO.setTrangThai(enums.TrangThaiCoBan.valueOf(rs.getString("TrangThai")));
                if (rs.getTimestamp("NgayTao") != null) {
                    khachHangDTO.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
                }
                if (rs.getTimestamp("UpdatedAt") != null) {
                    khachHangDTO.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
                }

                list.add(khachHangDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(KhachHangDTO khachHangDTO) {
        String sql = "INSERT INTO KhachHang (HoTen, SoDienThoai, DiaChi, TrangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, khachHangDTO.getHoTen());
            ps.setString(2, khachHangDTO.getSoDienThoai());
            ps.setString(3, khachHangDTO.getDiaChi());
            ps.setString(4, TrangThaiCoBan.HOAT_DONG.name());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KhachHangDTO khachHangDTO) {
        String sql = "UPDATE KhachHang SET HoTen = ?, SoDienThoai = ?, DiaChi = ?, DiemTichLuy = ?, TrangThai = ? WHERE MaKH = ?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, khachHangDTO.getHoTen());
            ps.setString(2, khachHangDTO.getSoDienThoai());
            ps.setString(3, khachHangDTO.getDiaChi());
            ps.setInt(4, khachHangDTO.getDiemTichLuy());
            ps.setString(5, khachHangDTO.getTrangThai().name());
            ps.setInt(6, khachHangDTO.getMaKH());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maKH) {
        String sql = "UPDATE KhachHang SET TrangThai = ? WHERE MaKH = ?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiCoBan.NGUNG_HOAT_DONG.name());
            ps.setInt(2, maKH);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean checkSoDienThoaiDaTonTai(String sdt) {
        String sql = "SELECT MaKH FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkSoDienThoaiBiTrungKhiSua(String sdt, int maKHHienTai) {
        String sql = "SELECT MaKH FROM KhachHang WHERE SoDienThoai = ? AND MaKH != ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ps.setInt(2, maKHHienTai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public KhachHangDTO getKhachHangByPhone(String phone) {
        KhachHangDTO kh = null;
        String sql = "SELECT * FROM KhachHang WHERE SoDienThoai = ? AND TrangThai = ?";

        try (Connection conn = utils.JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setString(2, TrangThaiCoBan.HOAT_DONG.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kh = new KhachHangDTO();
                    kh.setMaKH(rs.getInt("MaKH"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setSoDienThoai(rs.getString("SoDienThoai"));
                    kh.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                    kh.setTrangThai(enums.TrangThaiCoBan.valueOf(rs.getString("TrangThai")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kh;
    }

    public KhachHangDTO getKhachHangById(int maKH) {
        String sql = "SELECT * FROM KhachHang WHERE MaKH = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHangDTO kh = new KhachHangDTO();
                    kh.setMaKH(rs.getInt("MaKH"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setSoDienThoai(rs.getString("SoDienThoai"));
                    return kh;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTrangThai(int maKH, String trangThai) {
        String sql = "UPDATE KhachHang SET TrangThai = ?, UpdatedAt = CURRENT_TIMESTAMP WHERE MaKH = ?";
        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

