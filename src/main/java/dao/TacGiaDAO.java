package dao;

import dto.TacGiaDTO;
import enums.TrangThaiCoBan;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TacGiaDAO {
    public List<TacGiaDTO> getAll() {
        List<TacGiaDTO> list = new ArrayList<>();
        String sql = "SELECT MaTacGia, TenTacGia, TrangThai FROM TacGia";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TacGiaDTO tacGiaDTO = new TacGiaDTO();
                tacGiaDTO.setMaTacGia(rs.getInt("MaTacGia"));
                tacGiaDTO.setTenTacGia(rs.getString("TenTacGia"));
                tacGiaDTO.setTrangThai(
                        enums.TrangThaiCoBan.valueOf(rs.getString("TrangThai"))
                );
                list.add(tacGiaDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(TacGiaDTO tacGiaDTO) {
        String sql = "INSERT INTO TacGia (TenTacGia, TrangThai) VALUES (?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tacGiaDTO.getTenTacGia());
            ps.setString(2, tacGiaDTO.getTrangThai().name());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(TacGiaDTO tacGiaDTO) {
        String sql = "UPDATE TacGia SET TenTacGia=?, TrangThai=? WHERE MaTacGia=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tacGiaDTO.getTenTacGia());
            ps.setString(2, tacGiaDTO.getTrangThai().name());
            ps.setInt(3, tacGiaDTO.getMaTacGia());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maTacGia) {
        String sql = "UPDATE TacGia SET TrangThai = ? WHERE MaTacGia=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiCoBan.NGUNG_HOAT_DONG.name());
            ps.setInt(2, maTacGia);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkTenDaTonTai(String tenTacGia) {
        String sql = "SELECT MaTacGia FROM TacGia WHERE TenTacGia = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenTacGia);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean checkTenBiTrungKhiSua(String tenTacGia, int maTacGiaHienTai) {
        String sql = "SELECT MaTacGia FROM TacGia WHERE TenTacGia = ? AND MaTacGia != ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenTacGia);
            ps.setInt(2, maTacGiaHienTai);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
