package dto;
import enums.TrangThaiCoBan;

public class TheLoaiDTO {
    private int maLoai;
    private String tenLoai;
    private TrangThaiCoBan trangThai;

    public TheLoaiDTO() {
    }

    public TheLoaiDTO(int maLoai, String tenLoai, TrangThaiCoBan trangThai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.trangThai = trangThai;
    }

    public int getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public TrangThaiCoBan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiCoBan trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "TheLoaiDTO{" +
                "maLoai=" + maLoai +
                ", tenLoai='" + tenLoai + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
