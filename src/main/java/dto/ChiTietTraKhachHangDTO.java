package dto;

public class ChiTietTraKhachHangDTO {
    private int maCTPTK;
    private int maPTK;
    private int maSach;
    private int soLuong;
    private String tinhTrangSach;

    public ChiTietTraKhachHangDTO() {
    }

    public ChiTietTraKhachHangDTO(int maCTPTK, int maPTK, int maSach, int soLuong, String tinhTrangSach) {
        this.maCTPTK = maCTPTK;
        this.maPTK = maPTK;
        this.maSach = maSach;
        this.soLuong = soLuong;
        this.tinhTrangSach = tinhTrangSach;
    }

    public int getMaCTPTK() {
        return maCTPTK;
    }

    public void setMaCTPTK(int maCTPTK) {
        this.maCTPTK = maCTPTK;
    }

    public int getMaPTK() {
        return maPTK;
    }

    public void setMaPTK(int maPTK) {
        this.maPTK = maPTK;
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTinhTrangSach() {
        return tinhTrangSach;
    }

    public void setTinhTrangSach(String tinhTrangSach) {
        this.tinhTrangSach = tinhTrangSach;
    }

    @Override
    public String toString() {
        return "ChiTietTraKhachHangDTO{" +
                "maCTPTK=" + maCTPTK +
                ", maPTK=" + maPTK +
                ", maSach=" + maSach +
                ", soLuong=" + soLuong +
                ", tinhTrangSach='" + tinhTrangSach + '\'' +
                '}';
    }
}
