package dao;

import dto.NhanVienDTO;
import enums.TrangThaiTaiKhoan;
import utils.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public List<NhanVienDTO> getAll() {
        List<NhanVienDTO> list = new ArrayList<>();
        String sql = "SELECT MaNV, MaTaiKhoan, MaNVCode, HoTen, SoDienThoai FROM NhanVien";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhanVienDTO nhanVienDTO = new NhanVienDTO();
                nhanVienDTO.setMaNV(rs.getInt("MaNV"));
                nhanVienDTO.setMaTaiKhoan(rs.getInt("MaTaiKhoan"));
                nhanVienDTO.setMaNVCode(rs.getString("MaNVCode"));
                nhanVienDTO.setHoTen(rs.getString("HoTen"));
                nhanVienDTO.setSoDienThoai(rs.getString("SoDienThoai"));
                list.add(nhanVienDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NhanVienDTO nhanVienDTO) {
        String sql = "INSERT INTO NhanVien (MaTaiKhoan, MaNVCode, HoTen, SoDienThoai) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nhanVienDTO.getMaTaiKhoan());
            ps.setString(2, nhanVienDTO.getMaNVCode());
            ps.setString(3, nhanVienDTO.getHoTen());
            ps.setString(4, nhanVienDTO.getSoDienThoai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean update(NhanVienDTO nhanVienDTO) {
        String sql = "UPDATE NhanVien SET MaTaiKhoan = ?, MaNVCode = ?, HoTen = ?, SoDienThoai = ? WHERE MaNV = ?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nhanVienDTO.getMaTaiKhoan());
            ps.setString(2, nhanVienDTO.getMaNVCode());
            ps.setString(3, nhanVienDTO.getHoTen());
            ps.setString(4, nhanVienDTO.getSoDienThoai());
            ps.setInt(5, nhanVienDTO.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean delete(int maNV) {
        String sql = "UPDATE TaiKhoan tk " +
                     "INNER JOIN NhanVien nv ON tk.MaTaiKhoan = nv.MaTaiKhoan " +
                     "SET tk.TrangThai = ? " +
                     "WHERE nv.MaNV = ?";

        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TrangThaiTaiKhoan.Khoa.name());
            ps.setInt(2, maNV);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkSoDienThoaiDaTonTai(String sdt) {
        String sql = "SELECT MaNV FROM NhanVien WHERE SoDienThoai = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkSoDienThoaiBiTrungKhiSua(String sdt, int maNVHienTai) {
        String sql = "SELECT MaNV FROM NhanVien WHERE SoDienThoai = ? AND MaNV != ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ps.setInt(2, maNVHienTai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
