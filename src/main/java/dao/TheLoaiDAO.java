package dao;

import dto.TheLoaiDTO;
import enums.TrangThaiCoBan;
import utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheLoaiDAO {
    public List<TheLoaiDTO> getAll() {
        List<TheLoaiDTO> list = new ArrayList<>();
        String sql = "SELECT MaLoai, TenLoai, TrangThai FROM TheLoai";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TheLoaiDTO theLoaiDTO = new TheLoaiDTO();
                theLoaiDTO.setMaLoai(rs.getInt("MaLoai"));
                theLoaiDTO.setTenLoai(rs.getString("TenLoai"));
                theLoaiDTO.setTrangThai(
                        enums.TrangThaiCoBan.valueOf(rs.getString("TrangThai"))
                );
                list.add(theLoaiDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(TheLoaiDTO theLoaiDTO) {
        String sql = "INSERT INTO TheLoai (TenLoai, TrangThai) VALUES (?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, theLoaiDTO.getTenLoai());
            ps.setString(2, TrangThaiCoBan.HOAT_DONG.name());

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

    public boolean update(TheLoaiDTO theLoaiDTO) {
        String sql = "UPDATE TheLoai SET TenLoai=?, TrangThai=? WHERE MaLoai=?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, theLoaiDTO.getTenLoai());
            ps.setString(2, theLoaiDTO.getTrangThai().name());
            ps.setInt(3, theLoaiDTO.getMaLoai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maLoai) {
        String sql = "UPDATE TheLoai SET TrangThai = ? WHERE MaLoai=?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiCoBan.NGUNG_HOAT_DONG.name());
            ps.setInt(2, maLoai);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public TheLoaiDTO getById(int id) {
        String sql = "SELECT * FROM TheLoai WHERE MaLoai = ?";
        try (java.sql.Connection conn = utils.JDBCUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.TheLoaiDTO tl = new dto.TheLoaiDTO();
                    tl.setMaLoai(rs.getInt("MaLoai"));
                    tl.setTenLoai(rs.getString("TenLoai"));
                    tl.setTrangThai(
                            TrangThaiCoBan.valueOf(rs.getString("TrangThai"))
                    );
                    return tl;
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
