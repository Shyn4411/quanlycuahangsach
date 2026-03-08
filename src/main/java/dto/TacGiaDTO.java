package dto;
import enums.TrangThaiCoBan;

public class TacGiaDTO {
    private int maTacGia;
    private String tenTacGia;
    private TrangThaiCoBan trangThai;

    public TacGiaDTO() {
    }

    public TacGiaDTO(int maTacGia, String tenTacGia, TrangThaiCoBan trangThai) {
        this.maTacGia = maTacGia;
        this.tenTacGia = tenTacGia;
        this.trangThai = trangThai;
    }

    public int getMaTacGia() {
        return maTacGia;
    }

    public void setMaTacGia(int maTacGia) {
        this.maTacGia = maTacGia;
    }

    public String getTenTacGia() {
        return tenTacGia;
    }

    public void setTenTacGia(String tenTacGia) {
        this.tenTacGia = tenTacGia;
    }

    public TrangThaiCoBan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiCoBan trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "TacGiaDTO{" +
                "maTacGia=" + maTacGia +
                ", tenTacGia='" + tenTacGia + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
