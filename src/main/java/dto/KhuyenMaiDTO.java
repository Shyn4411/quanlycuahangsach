package dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import enums.TrangThaiKhuyenMai;

public class KhuyenMaiDTO {
    private int maKM;
    private String maCode;
    private String tenKM;
    private BigDecimal phanTramGiam;
    private BigDecimal soTienGiam;
    private BigDecimal donHangToiThieu;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private TrangThaiKhuyenMai trangThai;

    public KhuyenMaiDTO() {
    }

    public KhuyenMaiDTO(int maKM, String maCode, BigDecimal phanTramGiam, String tenKM, BigDecimal soTienGiam, BigDecimal donHangToiThieu, LocalDate ngayBatDau, LocalDate ngayKetThuc, TrangThaiKhuyenMai trangThai) {
        this.maKM = maKM;
        this.maCode = maCode;
        this.phanTramGiam = phanTramGiam;
        this.tenKM = tenKM;
        this.soTienGiam = soTienGiam;
        this.donHangToiThieu = donHangToiThieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public int getMaKM() {
        return maKM;
    }

    public void setMaKM(int maKM) {
        this.maKM = maKM;
    }

    public String getMaCode() {
        return maCode;
    }

    public void setMaCode(String maCode) {
        this.maCode = maCode;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public BigDecimal getPhanTramGiam() {
        return phanTramGiam;
    }

    public void setPhanTramGiam(BigDecimal phanTramGiam) {
        this.phanTramGiam = phanTramGiam;
    }

    public BigDecimal getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(BigDecimal soTienGiam) {
        this.soTienGiam = soTienGiam;
    }

    public BigDecimal getDonHangToiThieu() {
        return donHangToiThieu;
    }

    public void setDonHangToiThieu(BigDecimal donHangToiThieu) {
        this.donHangToiThieu = donHangToiThieu;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public TrangThaiKhuyenMai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiKhuyenMai trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "KhuyenMaiDTO{" +
                "maKM=" + maKM +
                ", maCode='" + maCode + '\'' +
                ", tenKM='" + tenKM + '\'' +
                ", phanTramGiam=" + phanTramGiam +
                ", soTienGiam=" + soTienGiam +
                '}';
    }
}
