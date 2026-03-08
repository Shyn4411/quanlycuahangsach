package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import enums.TrangThaiGiaoDich;

public class PhieuNhapDTO {
    private int maPN;
    private int maNV;
    private String tenNV;
    private int maNCC;
    private String tenNCC;
    private BigDecimal tongTien;
    private TrangThaiGiaoDich trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime updatedAt;

    public PhieuNhapDTO() {
    }

    public PhieuNhapDTO(int maPN, int maNV, int maNCC, BigDecimal tongTien, TrangThaiGiaoDich trangThai, LocalDateTime ngayTao, LocalDateTime updatedAt) {
        this.maPN = maPN;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.updatedAt = updatedAt;
    }

    public int getMaPN() {
        return maPN;
    }

    public void setMaPN(int maPN) {
        this.maPN = maPN;
    }


    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public int getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(int maNCC) {
        this.maNCC = maNCC;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
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
        return "PhieuNhapDTO{" +
                "maPN=" + maPN +
                ", maNV=" + maNV +
                ", maNCC=" + maNCC +
                ", tongTien=" + tongTien +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
