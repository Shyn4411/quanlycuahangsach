package dto;

import enums.PhuongThucThanhToan;
import enums.TrangThaiThanhToan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ThanhToanDTO {
    private int maThanhToan;
    private int maHD;
    private PhuongThucThanhToan phuongThuc;
    private BigDecimal soTien;
    private TrangThaiThanhToan trangThai;
    private LocalDateTime ngayThanhToan;
    private String ghiChuGiaoDich;

    public ThanhToanDTO() {}

    public ThanhToanDTO(int maThanhToan, int maHD, PhuongThucThanhToan phuongThuc, BigDecimal soTien, TrangThaiThanhToan trangThai, LocalDateTime ngayThanhToan, String ghiChuGiaoDich) {
        this.maThanhToan = maThanhToan;
        this.maHD = maHD;
        this.phuongThuc = phuongThuc;
        this.soTien = soTien;
        this.trangThai = trangThai;
        this.ngayThanhToan = ngayThanhToan;
        this.ghiChuGiaoDich = ghiChuGiaoDich;
    }

    public int getMaThanhToan() {
        return maThanhToan;
    }

    public void setMaThanhToan(int maThanhToan) {
        this.maThanhToan = maThanhToan;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public PhuongThucThanhToan getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(PhuongThucThanhToan phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public TrangThaiThanhToan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiThanhToan trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getGhiChuGiaoDich() {
        return ghiChuGiaoDich;
    }

    public void setGhiChuGiaoDich(String ghiChuGiaoDich) {
        this.ghiChuGiaoDich = ghiChuGiaoDich;
    }

    @Override
    public String toString() {
        return "ThanhToanDTO{" +
                "maThanhToan=" + maThanhToan +
                ", maHD=" + maHD +
                ", phuongThuc=" + phuongThuc +
                ", soTien=" + soTien +
                ", trangThai=" + trangThai +
                '}';
    }
}
