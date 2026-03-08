package dto;
import enums.TrangThaiTaiKhoan;
import java.time.LocalDateTime;

public class TaiKhoanDTO {
    private int maTaiKhoan;
    private int maQuyen;
    private String tenDangNhap;
    private String matKhau;
    private TrangThaiTaiKhoan trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime updatedAt;
    private Integer maNhanVien;


    public TaiKhoanDTO() {}
    public TaiKhoanDTO(int maTaiKhoan, String tenDangNhap, int maQuyen, String matKhau, TrangThaiTaiKhoan trangThai, LocalDateTime ngayTao, LocalDateTime updatedAt) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.maQuyen = maQuyen;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.updatedAt = updatedAt;
    }

    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(int maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public int getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(int maQuyen) {
        this.maQuyen = maQuyen;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public TrangThaiTaiKhoan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiTaiKhoan trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Integer getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(Integer maNhanVien) { this.maNhanVien = maNhanVien; }
    @Override
    public String toString() {
        return "TaiKhoanDTO{" +
                "maTaiKhoan=" + maTaiKhoan +
                ", maQuyen=" + maQuyen +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
