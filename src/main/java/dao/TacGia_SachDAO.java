package dao;

import dto.TacGia_SachDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TacGia_SachDAO {
    public List<TacGia_SachDTO> getByMaSach(int maSach) {
        List<TacGia_SachDTO> list = new ArrayList<>();
        String sql = "SELECT MaSach, MaTacGia FROM TacGia_Sach WHERE MaSach = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TacGia_SachDTO tacGia_sachDTO = new TacGia_SachDTO();
                    tacGia_sachDTO.setMaSach(rs.getInt("MaSach"));
                    tacGia_sachDTO.setMaTacGia(rs.getInt("MaTacGia"));
                    list.add(tacGia_sachDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(TacGia_SachDTO tacGia_SachDTO) {
        String sql = "INSERT INTO TacGia_Sach (MaSach, MaTacGia) VALUES (?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tacGia_SachDTO.getMaSach());
            ps.setInt(2, tacGia_SachDTO.getMaTacGia());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByMaSach(int maSach) {
        String sql = "DELETE FROM TacGia_Sach WHERE MaSach = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
