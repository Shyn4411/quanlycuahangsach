package dao;

import dto.PhieuTraNhaCungCapDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhieuTraNhaCungCapDAO {

    public List<PhieuTraNhaCungCapDTO> getAll() {
        List<PhieuTraNhaCungCapDTO> list = new ArrayList<>();
        String sql = "SELECT MaPTN, MaPTNCode, MaNV, MaNCC, LyDo, TongTienHoan, NgayTao FROM PhieuTraNhaCungCap ORDER BY NgayTao DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieuTraNhaCungCapDTO dto = new PhieuTraNhaCungCapDTO();
                dto.setMaPTN(rs.getInt("MaPTN"));
                dto.setMaPTNCode(rs.getString("MaPTNCode"));
                dto.setMaNV(rs.getInt("MaNV"));
                dto.setMaNCC(rs.getInt("MaNCC"));
                dto.setLyDo(rs.getString("LyDo"));
                dto.setTongTienHoan(rs.getBigDecimal("TongTienHoan"));

                if (rs.getTimestamp("NgayTao") != null) {
                    dto.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
                }

                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(PhieuTraNhaCungCapDTO dto) {
        String sql = "INSERT INTO PhieuTraNhaCungCap (MaPTNCode, MaNV, MaNCC, LyDo, TongTienHoan) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dto.getMaPTNCode());
            ps.setInt(2, dto.getMaNV());
            ps.setInt(3, dto.getMaNCC());
            ps.setString(4, dto.getLyDo());
            ps.setBigDecimal(5, dto.getTongTienHoan());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}