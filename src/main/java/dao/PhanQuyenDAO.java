package dao;

import dto.PhanQuyenDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhanQuyenDAO {
    public List<PhanQuyenDTO> getAll() {
        List<PhanQuyenDTO> list = new ArrayList<>();
        String sql = "SELECT MaQuyen, MaCode, TenQuyen, MoTa FROM PhanQuyen";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhanQuyenDTO phanQuyenDTO = new PhanQuyenDTO();
                phanQuyenDTO.setMaQuyen(rs.getInt("MaQuyen"));
                phanQuyenDTO.setMaCode(rs.getString("MaCode"));
                phanQuyenDTO.setTenQuyen(rs.getString("TenQuyen"));
                phanQuyenDTO.setMoTa(rs.getString("MoTa"));

                list.add(phanQuyenDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(PhanQuyenDTO phanQuyenDTO) {
        String sql = "INSERT INTO PhanQuyen (MaCode, TenQuyen, MoTa) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phanQuyenDTO.getMaCode());
            ps.setString(2, phanQuyenDTO.getTenQuyen());
            ps.setString(3, phanQuyenDTO.getMoTa());


            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(PhanQuyenDTO phanQuyenDTO) {
        String sql = "UPDATE PhanQuyen SET MaCode = ?, TenQuyen=?, MoTa = ? WHERE MaQuyen=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phanQuyenDTO.getMaCode());
            ps.setString(2, phanQuyenDTO.getTenQuyen());
            ps.setString(3, phanQuyenDTO.getMoTa());
            ps.setInt(4, phanQuyenDTO.getMaQuyen());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maQuyen) {
        String sql = "DELETE FROM PhanQuyen WHERE MaQuyen=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maQuyen);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public PhanQuyenDTO getById(int maQuyen) {
        PhanQuyenDTO pq = null;
        String sql = "SELECT * FROM PhanQuyen WHERE MaQuyen = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maQuyen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pq = new PhanQuyenDTO();
                    pq.setMaQuyen(rs.getInt("MaQuyen"));
                    pq.setMaCode(rs.getString("MaCode"));
                    pq.setTenQuyen(rs.getString("TenQuyen"));
                    pq.setMoTa(rs.getString("MoTa"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pq;
    }


    public boolean checkTrungMaCode(String maCode) {
        String sql = "SELECT MaQuyen FROM PhanQuyen WHERE MaCode = ?";
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

    public boolean checkMaCodeBiTrungKhiSua(String maCode, int maQuyenHienTai) {
        String sql = "SELECT MaQuyen FROM PhanQuyen WHERE MaCode = ? AND MaQuyen != ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCode);
            ps.setInt(2, maQuyenHienTai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
