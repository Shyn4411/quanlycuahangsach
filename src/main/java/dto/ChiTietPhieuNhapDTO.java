package dto;

import java.math.BigDecimal;

public class ChiTietPhieuNhapDTO {
    private int maCTPN;
    private int maPN;
    private int maSach;
    private int soLuong;
    private BigDecimal giaNhap;
    private BigDecimal thanhTien;

    public ChiTietPhieuNhapDTO() {
    }

    public ChiTietPhieuNhapDTO(int maCTPN, int maPN, int maSach, BigDecimal giaNhap, int soLuong, BigDecimal thanhTien) {
        this.maCTPN = maCTPN;
        this.maPN = maPN;
        this.maSach = maSach;
        this.giaNhap = giaNhap;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }

    public int getMaCTPN() {
        return maCTPN;
    }

    public void setMaCTPN(int maCTPN) {
        this.maCTPN = maCTPN;
    }

    public int getMaPN() {
        return maPN;
    }

    public void setMaPN(int maPN) {
        this.maPN = maPN;
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }

    public BigDecimal getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(BigDecimal giaNhap) {
        this.giaNhap = giaNhap;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhapDTO{" +
                "maCTPN=" + maCTPN +
                ", maPN=" + maPN +
                ", maSach=" + maSach +
                ", soLuong=" + soLuong +
                ", giaNhap=" + giaNhap +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
