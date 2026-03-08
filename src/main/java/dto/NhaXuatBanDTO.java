package dto;
import enums.TrangThaiCoBan;

public class NhaXuatBanDTO {
    private int maNXB;
    private String tenNXB;
    private TrangThaiCoBan trangThai;

    public NhaXuatBanDTO() {
    }

    public NhaXuatBanDTO(int maNXB, String tenNXB, TrangThaiCoBan trangThai) {
        this.maNXB = maNXB;
        this.tenNXB = tenNXB;
        this.trangThai = trangThai;
    }

    public int getMaNXB() {
        return maNXB;
    }

    public void setMaNXB(int maNXB) {
        this.maNXB = maNXB;
    }

    public String getTenNXB() {
        return tenNXB;
    }

    public void setTenNXB(String tenNXB) {
        this.tenNXB = tenNXB;
    }

    public TrangThaiCoBan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiCoBan trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "NhaXuatBanDTO{" +
                "maNXB=" + maNXB +
                ", tenNXB='" + tenNXB + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
