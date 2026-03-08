package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import enums.LoaiHoaDon;
import enums.TrangThaiGiaoDich;


public class HoaDonDTO {
    private int maHD;
    private Integer maNV;
    private Integer maKH; // Đổi int thành Integer
    private Integer maKM;
    private BigDecimal tongTien;
    private BigDecimal tienGiam;
    private BigDecimal thanhTien;
    private LoaiHoaDon loaiHoaDon;
    private TrangThaiGiaoDich trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime updatedAt;

    public HoaDonDTO() {
    }

    public HoaDonDTO(int maHD, int maNV, int maKM, int maKH, BigDecimal tongTien, BigDecimal tienGiam, BigDecimal thanhTien, LoaiHoaDon loaiHoaDon, TrangThaiGiaoDich trangThai, LocalDateTime ngayTao, LocalDateTime updatedAt) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.maKM = maKM;
        this.maKH = maKH;
        this.tongTien = tongTien;
        this.tienGiam = tienGiam;
        this.thanhTien = thanhTien;
        this.loaiHoaDon = loaiHoaDon;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.updatedAt = updatedAt;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public Integer getMaNV() { return maNV; }
    public void setMaNV(Integer maNV) { this.maNV = maNV; }

    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }

    public Integer getMaKM() { return maKM; }
    public void setMaKM(Integer maKM) { this.maKM = maKM; }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getTienGiam() {
        return tienGiam;
    }

    public void setTienGiam(BigDecimal tienGiam) {
        this.tienGiam = tienGiam;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public LoaiHoaDon getLoaiHoaDon() {
        return loaiHoaDon;
    }

    public void setLoaiHoaDon(LoaiHoaDon loaiHoaDon) {
        this.loaiHoaDon = loaiHoaDon;
    }

    public TrangThaiGiaoDich getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiGiaoDich trangThai) {
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

    @Override
    public String toString() {
        return "HoaDonDTO{" +
                "maHD=" + maHD +
                ", maNV=" + maNV +
                ", thanhTien=" + thanhTien +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
