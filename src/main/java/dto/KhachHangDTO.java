package dto;
import enums.TrangThaiCoBan;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KhachHangDTO {
    private int maKH;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private int diemTichLuy;
    private TrangThaiCoBan trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime updatedAt;

    public KhachHangDTO() {
    }

    public KhachHangDTO(int maKH, String hoTen, String soDienThoai, String diaChi, int diemTichLuy, LocalDateTime ngayTao, TrangThaiCoBan trangThai, LocalDateTime updatedAt) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.updatedAt = updatedAt;
    }

    public int getMaKH() {
        return maKH;
    }

    public void setMaKH(int maKH) {
        this.maKH = maKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public TrangThaiCoBan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiCoBan trangThai) {
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


    public String getMaKHCode () {
        return String.format("KH%03d", maKH);
    }

    public String getNgayTaoFormat() {
        if (this.ngayTao == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        return this.ngayTao.format(formatter);
    }
    @Override
    public String toString() {
        return "KhachHangDTO{" +
                "maKH=" + maKH +
                ", hoTen='" + hoTen + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diemTichLuy=" + diemTichLuy +
                '}';
    }
}
