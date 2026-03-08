package dao;

import dto.NhaXuatBanDTO;
import enums.TrangThaiCoBan;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhaXuatBanDAO {
    public List<NhaXuatBanDTO> getAll() {
        List<NhaXuatBanDTO> list = new ArrayList<>();
        String sql = "SELECT MaNXB, TenNXB, TrangThai FROM NhaXuatBan";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhaXuatBanDTO nhaXuatBanDTO = new NhaXuatBanDTO();
                nhaXuatBanDTO.setMaNXB(rs.getInt("MaNXB"));
                nhaXuatBanDTO.setTenNXB(rs.getString("TenNXB"));
                nhaXuatBanDTO.setTrangThai(
                        enums.TrangThaiCoBan.valueOf(rs.getString("TrangThai"))
                );
                list.add(nhaXuatBanDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NhaXuatBanDTO nhaXuatBanDTO) {
        String sql = "INSERT INTO NhaXuatBan (TenNXB, TrangThai) VALUES (?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nhaXuatBanDTO.getTenNXB());
            ps.setString(2, nhaXuatBanDTO.getTrangThai().name());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NhaXuatBanDTO nhaXuatBanDTO) {
        String sql = "UPDATE NhaXuatBan SET TenNXB=?, TrangThai=? WHERE MaNXB=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nhaXuatBanDTO.getTenNXB());
            ps.setString(2, nhaXuatBanDTO.getTrangThai().name());
            ps.setInt(3, nhaXuatBanDTO.getMaNXB());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maNXB) {
        String sql = "UPDATE NhaXuatBan SET TrangThai = ? WHERE MaNXB=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiCoBan.NgungHoatDong.name());
            ps.setInt(2, maNXB);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkTenDaTonTai(String tenNXB) {
        String sql = "SELECT MaNXB FROM NhaXuatBan WHERE TenNXB = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenNXB);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkTenBiTrungKhiSua(String tenNXB, int maNXBHienTai) {
        String sql = "SELECT MaNXB FROM NhaXuatBan WHERE TenNXB = ? AND MaNXB != ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenNXB);
            ps.setInt(2, maNXBHienTai);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public NhaXuatBanDTO getById(int maNXB) {
        String sql = "SELECT MaNXB, TenNXB, TrangThai FROM NhaXuatBan WHERE MaNXB = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNXB);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhaXuatBanDTO nxb = new NhaXuatBanDTO();
                    nxb.setMaNXB(rs.getInt("MaNXB"));
                    nxb.setTenNXB(rs.getString("TenNXB"));
                    nxb.setTrangThai(TrangThaiCoBan.valueOf(rs.getString("TrangThai")));
                    return nxb;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
