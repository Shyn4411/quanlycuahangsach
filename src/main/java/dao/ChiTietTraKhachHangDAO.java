package dao;

import dto.ChiTietTraKhachHangDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietTraKhachHangDAO {

    public List<ChiTietTraKhachHangDTO> getByMaPTK(int maPTK) {
        List<ChiTietTraKhachHangDTO> list = new ArrayList<>();
        String sql = "SELECT MaCTPTK, MaPTK, MaSach, SoLuong, TinhTrangSach FROM ChiTietTraKhachHang WHERE MaPTK = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPTK);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietTraKhachHangDTO dto = new ChiTietTraKhachHangDTO();
                    dto.setMaCTPTK(rs.getInt("MaCTPTK"));
                    dto.setMaPTK(rs.getInt("MaPTK"));
                    dto.setMaSach(rs.getInt("MaSach"));
                    dto.setSoLuong(rs.getInt("SoLuong"));
                    dto.setTinhTrangSach(rs.getString("TinhTrangSach"));

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChiTietTraKhachHangDTO dto) {
        String sql = "INSERT INTO ChiTietTraKhachHang (MaPTK, MaSach, SoLuong, TinhTrangSach) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dto.getMaPTK());
            ps.setInt(2, dto.getMaSach());
            ps.setInt(3, dto.getSoLuong());
            ps.setString(4, dto.getTinhTrangSach());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}