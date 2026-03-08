package dao;

import dto.ThanhToanDTO;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThanhToanDAO {

    public List<ThanhToanDTO> getAll() {
        List<ThanhToanDTO> list = new ArrayList<>();
        String sql = "SELECT MaThanhToan, MaHD, PhuongThuc, SoTien, TrangThai, NgayThanhToan, GhiChuGiaoDich FROM ThanhToan ORDER BY NgayThanhToan DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ThanhToanDTO dto = new ThanhToanDTO();
                dto.setMaThanhToan(rs.getInt("MaThanhToan"));
                dto.setMaHD(rs.getInt("MaHD"));
                dto.setPhuongThuc(enums.PhuongThucThanhToan.valueOf(rs.getString("PhuongThuc")));
                dto.setSoTien(rs.getBigDecimal("SoTien"));
                dto.setTrangThai(enums.TrangThaiThanhToan.valueOf(rs.getString("TrangThai")));

                if (rs.getTimestamp("NgayThanhToan") != null) {
                    dto.setNgayThanhToan(rs.getTimestamp("NgayThanhToan").toLocalDateTime());
                }
                dto.setGhiChuGiaoDich(rs.getString("GhiChuGiaoDich"));

                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ThanhToanDTO> getByMaHD(int maHD) {
        List<ThanhToanDTO> list = new ArrayList<>();
        String sql = "SELECT MaThanhToan, MaHD, PhuongThuc, SoTien, TrangThai, NgayThanhToan, GhiChuGiaoDich FROM ThanhToan WHERE MaHD = ? ORDER BY NgayThanhToan DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThanhToanDTO thanhToanDTO = new ThanhToanDTO();
                    thanhToanDTO.setMaThanhToan(rs.getInt("MaThanhToan"));
                    thanhToanDTO.setMaHD(rs.getInt("MaHD"));
                    thanhToanDTO.setPhuongThuc(enums.PhuongThucThanhToan.valueOf(rs.getString("PhuongThuc")));
                    thanhToanDTO.setSoTien(rs.getBigDecimal("SoTien"));
                    thanhToanDTO.setTrangThai(
                            enums.TrangThaiThanhToan.valueOf(rs.getString("TrangThai"))
                    );

                    if (rs.getTimestamp("NgayThanhToan") != null) {
                        thanhToanDTO.setNgayThanhToan(rs.getTimestamp("NgayThanhToan").toLocalDateTime());
                    }
                    thanhToanDTO.setGhiChuGiaoDich(rs.getString("GhiChuGiaoDich"));

                    list.add(thanhToanDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ThanhToanDTO thanhToanDTO) {
        String sql = "INSERT INTO ThanhToan (MaHD, PhuongThuc, SoTien, TrangThai, GhiChuGiaoDich) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, thanhToanDTO.getMaHD());
            ps.setString(2, thanhToanDTO.getPhuongThuc().name());
            ps.setBigDecimal(3, thanhToanDTO.getSoTien());
            ps.setString(4, thanhToanDTO.getTrangThai().name());
            ps.setString(5, thanhToanDTO.getGhiChuGiaoDich());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(int maThanhToan, String trangThaiMoi) {
        String sql = "UPDATE ThanhToan SET TrangThai = ? WHERE MaThanhToan = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maThanhToan);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}