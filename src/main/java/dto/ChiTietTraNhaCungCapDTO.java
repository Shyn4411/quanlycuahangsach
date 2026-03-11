package dto;

import java.math.BigDecimal;

public class ChiTietTraNhaCungCapDTO {
    private int maCTPTN;
    private int maPTN;
    private int maSach;
    private int soLuong;

    private String tenSach;
    private BigDecimal giaNhap;
    private BigDecimal thanhTienHoan;

    public ChiTietTraNhaCungCapDTO() {
    }

    public ChiTietTraNhaCungCapDTO(int maCTPTN, int maPTN, int maSach, int soLuong) {
        this.maCTPTN = maCTPTN;
        this.maPTN = maPTN;
        this.maSach = maSach;
        this.soLuong = soLuong;
    }

    public int getMaCTPTN() { return maCTPTN; }
    public void setMaCTPTN(int maCTPTN) { this.maCTPTN = maCTPTN; }

    public int getMaPTN() { return maPTN; }
    public void setMaPTN(int maPTN) { this.maPTN = maPTN; }

    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }


    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }

    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal giaNhap) { this.giaNhap = giaNhap; }

    public BigDecimal getThanhTienHoan() { return thanhTienHoan; }
    public void setThanhTienHoan(BigDecimal thanhTienHoan) { this.thanhTienHoan = thanhTienHoan; }

    @Override
    public String toString() {
        return "ChiTietTraNhaCungCapDTO{" +
                "maCTPTN=" + maCTPTN +
                ", maPTN=" + maPTN +
                ", maSach=" + maSach +
                ", soLuong=" + soLuong +
                '}';
    }
}