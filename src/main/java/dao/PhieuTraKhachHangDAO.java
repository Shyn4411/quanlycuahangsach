package dao;

import dto.PhieuTraKhachHangDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhieuTraKhachHangDAO {

    public List<PhieuTraKhachHangDTO> getAll() {
        List<PhieuTraKhachHangDTO> list = new ArrayList<>();
        String sql = "SELECT MaPTK, MaHD, MaNV, LyDo, TienHoan, NgayTao FROM PhieuTraKhachHang ORDER BY NgayTao DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieuTraKhachHangDTO dto = new PhieuTraKhachHangDTO();
                dto.setMaPTK(rs.getInt("MaPTK"));
                dto.setMaHD(rs.getInt("MaHD"));
                dto.setMaNV(rs.getInt("MaNV"));
                dto.setLyDo(rs.getString("LyDo"));
                dto.setTienHoan(rs.getBigDecimal("TienHoan"));
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

    public List<PhieuTraKhachHangDTO> getByMaHD(int maHD) {
        List<PhieuTraKhachHangDTO> list = new ArrayList<>();
        String sql = "SELECT MaPTK, MaHD, MaNV, LyDo, TienHoan, NgayTao FROM PhieuTraKhachHang WHERE MaHD = ? ORDER BY NgayTao DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuTraKhachHangDTO dto = new PhieuTraKhachHangDTO();
                    dto.setMaPTK(rs.getInt("MaPTK"));
                    dto.setMaHD(rs.getInt("MaHD"));
                    dto.setMaNV(rs.getInt("MaNV"));
                    dto.setLyDo(rs.getString("LyDo"));
                    dto.setTienHoan(rs.getBigDecimal("TienHoan"));

                    if (rs.getTimestamp("NgayTao") != null) {
                        dto.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
                    }

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(PhieuTraKhachHangDTO dto) {
        String sql = "INSERT INTO PhieuTraKhachHang (MaHD, MaNV, LyDo, TienHoan) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, dto.getMaHD());
            ps.setInt(2, dto.getMaNV());
            ps.setString(3, dto.getLyDo());
            ps.setBigDecimal(4, dto.getTienHoan());

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