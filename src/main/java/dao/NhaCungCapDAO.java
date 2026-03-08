package dao;

import dto.NhaCungCapDTO;
import enums.TrangThaiCoBan;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {
    public List<NhaCungCapDTO> getAll() {
        List<NhaCungCapDTO> list = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC, SoDienThoai, DiaChi, TrangThai FROM NhaCungCap";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhaCungCapDTO nhaCungCapDTO = new NhaCungCapDTO();
                nhaCungCapDTO.setMaNCC(rs.getInt("MaNCC"));
                nhaCungCapDTO.setTenNCC(rs.getString("TenNCC"));
                nhaCungCapDTO.setSoDienThoai(rs.getString("SoDienThoai"));
                nhaCungCapDTO.setDiaChi(rs.getString("DiaChi"));
                nhaCungCapDTO.setTrangThai(
                        enums.TrangThaiCoBan.valueOf(rs.getString("TrangThai"))
                );
                list.add(nhaCungCapDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NhaCungCapDTO nhaCungCapDTO) {
        String sql = "INSERT INTO NhaCungCap (TenNCC, SoDienThoai, DiaChi, TrangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nhaCungCapDTO.getTenNCC());
            ps.setString(2, nhaCungCapDTO.getSoDienThoai());
            ps.setString(3, nhaCungCapDTO.getDiaChi());
            ps.setString(4, nhaCungCapDTO.getTrangThai().name());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NhaCungCapDTO nhaCungCapDTO) {
        String sql = "UPDATE NhaCungCap SET TenNCC=?, SoDienThoai = ?, DiaChi = ?, TrangThai=? WHERE MaNCC=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nhaCungCapDTO.getTenNCC());
            ps.setString(2, nhaCungCapDTO.getSoDienThoai());
            ps.setString(3, nhaCungCapDTO.getDiaChi());
            ps.setString(4, nhaCungCapDTO.getTrangThai().name());
            ps.setInt(5, nhaCungCapDTO.getMaNCC());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maNCC) {
        String sql = "UPDATE NhaCungCap SET TrangThai = ? WHERE MaNCC=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiCoBan.NgungHoatDong.name());
            ps.setInt(2, maNCC);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkSoDienThoaiDaTonTai(String sdt) {
        String sql = "SELECT MaNCC FROM NhaCungCap WHERE SoDienThoai = ?";
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

    public boolean checkSoDienThoaiBiTrungKhiSua(String sdt, int maNCCHienTai) {
        String sql = "SELECT MaNCC FROM NhaCungCap WHERE SoDienThoai = ? AND MaNCC != ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ps.setInt(2, maNCCHienTai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
