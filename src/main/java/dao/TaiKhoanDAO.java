package dao;

import dto.TaiKhoanDTO;
import enums.TrangThaiTaiKhoan;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {
    public List<TaiKhoanDTO> getAll(){
        List<TaiKhoanDTO> list = new ArrayList<>();
        String sql = "SELECT MaTaiKhoan, MaQuyen, TenDangNhap, MatKhau, TrangThai, NgayTao, UpdatedAt FROM TaiKhoan";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaiKhoanDTO taiKhoanDTO = new TaiKhoanDTO();
                taiKhoanDTO.setMaTaiKhoan(rs.getInt("MaTaiKhoan"));
                taiKhoanDTO.setMaQuyen(rs.getInt("MaQuyen"));
                taiKhoanDTO.setTenDangNhap(rs.getString("TenDangNhap"));
                taiKhoanDTO.setMatKhau(rs.getString("MatKhau"));
                taiKhoanDTO.setTrangThai(enums.TrangThaiTaiKhoan.valueOf(rs.getString("TrangThai")));
                if (rs.getTimestamp("NgayTao") != null) {
                    taiKhoanDTO.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
                }
                if (rs.getTimestamp("UpdatedAt") != null) {
                    taiKhoanDTO.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
                }
                list.add(taiKhoanDTO);
            }
        } catch (SQLException e) {
                e.printStackTrace();
        }
        return list;
    }

    public boolean insert(TaiKhoanDTO taiKhoanDTO){
        String sql = "INSERT INTO TaiKhoan (MaQuyen, TenDangNhap, MatKhau, TrangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taiKhoanDTO.getMaQuyen());
            ps.setString(2, taiKhoanDTO.getTenDangNhap());
            ps.setString(3, taiKhoanDTO.getMatKhau());
            ps.setString(4, taiKhoanDTO.getTrangThai().name());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(TaiKhoanDTO taiKhoanDTO){
        String sql = "UPDATE TaiKhoan SET MaQuyen = ?, TenDangNhap = ?, MatKhau = ?, TrangThai = ? WHERE MaTaiKhoan = ?";

        try(Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taiKhoanDTO.getMaQuyen());
            ps.setString(2, taiKhoanDTO.getTenDangNhap());
            ps.setString(3, taiKhoanDTO.getMatKhau());
            ps.setString(4, taiKhoanDTO.getTrangThai().name());

            ps.setInt(5, taiKhoanDTO.getMaTaiKhoan());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maTaiKhoan){
        String sql = "UPDATE TaiKhoan SET TrangThai = ? WHERE MaTaiKhoan = ?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiTaiKhoan.Khoa.name());
            ps.setInt(2, maTaiKhoan);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public TaiKhoanDTO getByTenDangNhap(String username){
        TaiKhoanDTO tk = null;
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tk = new TaiKhoanDTO();
                    tk.setMaTaiKhoan(rs.getInt("MaTaiKhoan"));
                    tk.setMaQuyen(rs.getInt("MaQuyen"));
                    tk.setTenDangNhap(rs.getString("TenDangNhap"));
                    tk.setMatKhau(rs.getString("MatKhau"));
                    tk.setTrangThai(enums.TrangThaiTaiKhoan.valueOf(rs.getString("TrangThai")));
                    tk.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tk;
    }

    public boolean checkTrungTenDangNhap(String username) {
        String sql = "SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(int maTaiKhoan, String trangThaiMoi) {
        String sql = "UPDATE TaiKhoan SET TrangThai = ? WHERE MaTaiKhoan = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maTaiKhoan);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public TaiKhoanDTO login(String username, String password) {
        TaiKhoanDTO tk = null;

        // Dùng LEFT JOIN để lấy luôn MaNV của người đăng nhập
        String sql = "SELECT tk.*, nv.MaNV " +
                "FROM TaiKhoan tk " +
                "LEFT JOIN NhanVien nv ON tk.MaTaiKhoan = nv.MaTaiKhoan " +
                "WHERE tk.TenDangNhap = ? AND tk.MatKhau = ? AND tk.TrangThai = 'HoatDong'";

        try (Connection conn = utils.JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tk = new TaiKhoanDTO();
                    tk.setMaTaiKhoan(rs.getInt("MaTaiKhoan"));
                    tk.setMaQuyen(rs.getInt("MaQuyen"));
                    tk.setTenDangNhap(rs.getString("TenDangNhap"));
                    tk.setMatKhau(rs.getString("MatKhau"));

                    // Ép kiểu TrangThaiTaiKhoan (nếu ông có dùng Enum)
                    tk.setTrangThai(enums.TrangThaiTaiKhoan.valueOf(rs.getString("TrangThai")));

                    // BỐC LẤY MÃ NHÂN VIÊN Ở ĐÂY NÈ!
                    int maNV = rs.getInt("MaNV");
                    if (rs.wasNull()) {
                        tk.setMaNhanVien(null); // Quản trị viên không có trong bảng NhanVien
                    } else {
                        tk.setMaNhanVien(maNV); // Nhân viên bình thường
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tk;
    }
}
