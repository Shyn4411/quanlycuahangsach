package dao;

import dto.ChiTietTraNhaCungCapDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietTraNhaCungCapDAO {

    public List<ChiTietTraNhaCungCapDTO> getByMaPTN(int maPTN) {
        List<ChiTietTraNhaCungCapDTO> list = new ArrayList<>();
        String sql = "SELECT MaCTPTN, MaPTN, MaSach, SoLuong FROM ChiTietTraNhaCungCap WHERE MaPTN = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPTN);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietTraNhaCungCapDTO dto = new ChiTietTraNhaCungCapDTO();
                    dto.setMaCTPTN(rs.getInt("MaCTPTN"));
                    dto.setMaPTN(rs.getInt("MaPTN"));
                    dto.setMaSach(rs.getInt("MaSach"));
                    dto.setSoLuong(rs.getInt("SoLuong"));

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChiTietTraNhaCungCapDTO dto) {
        String sql = "INSERT INTO ChiTietTraNhaCungCap (MaPTN, MaSach, SoLuong) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dto.getMaPTN());
            ps.setInt(2, dto.getMaSach());
            ps.setInt(3, dto.getSoLuong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}