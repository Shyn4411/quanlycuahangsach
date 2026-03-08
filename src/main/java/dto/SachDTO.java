package dto;

import enums.TrangThaiSach;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SachDTO {

    private int maSach;
    private String tenSach;
    private int maLoai;
    private String danhSachTacGia;
    private int maNXB;
    private String hinhAnh;
    private BigDecimal giaGoc;
    private BigDecimal giaBan;
    private int soLuongTon;
    private int soLuongLoi;
    private TrangThaiSach trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime updatedAt;

    public SachDTO() {}

    public SachDTO(int maSach, String tenSach, int maLoai, String danhSachTacGia, int maNXB, String hinhAnh, BigDecimal giaGoc, BigDecimal giaBan, int soLuongTon, int soLuongLoi, TrangThaiSach trangThai, LocalDateTime ngayTao, LocalDateTime updatedAt) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.maLoai = maLoai;
        this.danhSachTacGia = danhSachTacGia;
        this.maNXB = maNXB;
        this.hinhAnh = hinhAnh;
        this.giaGoc = giaGoc;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.soLuongLoi = soLuongLoi;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.updatedAt = updatedAt;
    }

    public String getMaSachCode() {
        return String.format("S%03d", this.maSach);
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }


    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public int getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }

    public String getDanhSachTacGia() {
        return danhSachTacGia;
    }

    public void setDanhSachTacGia(String danhSachTacGia) {
        this.danhSachTacGia = danhSachTacGia;
    }

    public int getMaNXB() {
        return maNXB;
    }

    public void setMaNXB(int maNXB) {
        this.maNXB = maNXB;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public BigDecimal getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(BigDecimal giaGoc) {
        this.giaGoc = giaGoc;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public int getSoLuongLoi() {
        return soLuongLoi;
    }

    public void setSoLuongLoi(int soLuongLoi) {
        this.soLuongLoi = soLuongLoi;
    }

    public TrangThaiSach getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiSach trangThai) {
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
        return "SachDTO{" +
                "maSach=" + maSach +
                ", tenSach='" + tenSach + '\'' +
                ", soLuongTon=" + soLuongTon +
                ", giaBan=" + giaBan +
                '}';
    }

}
