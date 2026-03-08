package dao;

import dto.LichSuKhoDTO;
import enums.LoaiChungTu;
import enums.LoaiGiaoDich;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LichSuKhoDAO {
    public List<LichSuKhoDTO> getAll() {
        List<LichSuKhoDTO> list = new ArrayList<>();
        String sql = "SELECT MaLichSu, MaSach, LoaiGiaoDich, LoaiChungTu, MaChungTu, SoLuongThayDoi, NgayGioTao, GhiChu FROM LichSuKho ORDER BY NgayGioTao DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LichSuKhoDTO lichSuKhoDTO = new LichSuKhoDTO();
                lichSuKhoDTO.setMaLichSu(rs.getInt("MaLichSu"));
                lichSuKhoDTO.setMaSach(rs.getInt("MaSach"));
                lichSuKhoDTO.setLoaiGiaoDich(
                        enums.LoaiGiaoDich.valueOf(rs.getString("LoaiGiaoDich"))
                );
                lichSuKhoDTO.setLoaiChungTu(
                        enums.LoaiChungTu.valueOf(rs.getString("LoaiChungTu"))
                );
                lichSuKhoDTO.setMaChungTu(rs.getInt("MaChungTu"));
                lichSuKhoDTO.setSoLuongThayDoi(rs.getInt("SoLuongThayDoi"));
                lichSuKhoDTO.setGhiChu(rs.getString("GhiChu"));

                if (rs.getTimestamp("NgayGioTao") != null) {
                    lichSuKhoDTO.setNgayGioTao(rs.getTimestamp("NgayGioTao").toLocalDateTime());
                }

                list.add(lichSuKhoDTO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(LichSuKhoDTO lichSuKhoDTO) {
        String sql = "INSERT INTO LichSuKho (MaSach, LoaiGiaoDich, LoaiChungTu, MaChungTu, SoLuongThayDoi, GhiChu) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lichSuKhoDTO.getMaSach());
            ps.setString(2, lichSuKhoDTO.getLoaiGiaoDich().name());
            ps.setString(3, lichSuKhoDTO.getLoaiChungTu().name());
            ps.setInt(4, lichSuKhoDTO.getMaChungTu());
            ps.setInt(5, lichSuKhoDTO.getSoLuongThayDoi()); // Số dương (nếu nhập hàng) hoặc số âm (nếu bán hàng)
            ps.setString(6, lichSuKhoDTO.getGhiChu());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private LichSuKhoDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        LichSuKhoDTO ls = new LichSuKhoDTO();
        ls.setMaLichSu(rs.getInt("MaLichSu"));
        ls.setMaSach(rs.getInt("MaSach"));
        ls.setLoaiGiaoDich(LoaiGiaoDich.valueOf(rs.getString("LoaiGiaoDich")));
        ls.setLoaiChungTu(LoaiChungTu.valueOf(rs.getString("LoaiChungTu")));
        ls.setMaChungTu(rs.getInt("MaChungTu"));
        ls.setSoLuongThayDoi(rs.getInt("SoLuongThayDoi"));
        ls.setNgayGioTao(rs.getTimestamp("NgayGioTao").toLocalDateTime()); // Lấy đầy đủ ngày giờ
        ls.setGhiChu(rs.getString("GhiChu"));
        return ls;
    }


    public List<LichSuKhoDTO> getByMaSach(int maSach) {
        List<LichSuKhoDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM LichSuKho WHERE MaSach = ? ORDER BY NgayGioTao DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<LichSuKhoDTO> getByChungTu(String loaiChungTu, int maChungTu) {
        List<LichSuKhoDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM LichSuKho WHERE LoaiChungTu = ? AND MaChungTu = ? ORDER BY NgayGioTao DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loaiChungTu);
            ps.setInt(2, maChungTu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<LichSuKhoDTO> getByDateRange(String tuNgay, String denNgay) {
        List<LichSuKhoDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM LichSuKho WHERE NgayGioTao >= ? AND NgayGioTao <= ? ORDER BY NgayGioTao DESC";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tuNgay + " 00:00:00");
            ps.setString(2, denNgay + " 23:59:59");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
