package dto;

import java.time.LocalDateTime;
import enums.LoaiGiaoDich;
import enums.LoaiChungTu;

public class LichSuKhoDTO {
    private int maLichSu;
    private int maSach;
    private LoaiGiaoDich loaiGiaoDich;
    private LoaiChungTu loaiChungTu;
    private int maChungTu;
    private int soLuongThayDoi;
    private LocalDateTime ngayGioTao;
    private String ghiChu;

    public LichSuKhoDTO() {
    }

    public LichSuKhoDTO(String ghiChu, LocalDateTime ngayGioTao, int soLuongThayDoi, int maChungTu, LoaiChungTu loaiChungTu, LoaiGiaoDich loaiGiaoDich, int maLichSu, int maSach) {
        this.ghiChu = ghiChu;
        this.ngayGioTao = ngayGioTao;
        this.soLuongThayDoi = soLuongThayDoi;
        this.maChungTu = maChungTu;
        this.loaiChungTu = loaiChungTu;
        this.loaiGiaoDich = loaiGiaoDich;
        this.maLichSu = maLichSu;
        this.maSach = maSach;
    }

    public int getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(int maLichSu) {
        this.maLichSu = maLichSu;
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }

    public LoaiChungTu getLoaiChungTu() {
        return loaiChungTu;
    }

    public void setLoaiChungTu(LoaiChungTu loaiChungTu) {
        this.loaiChungTu = loaiChungTu;
    }

    public LoaiGiaoDich getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public int getMaChungTu() {
        return maChungTu;
    }

    public void setMaChungTu(int maChungTu) {
        this.maChungTu = maChungTu;
    }

    public int getSoLuongThayDoi() {
        return soLuongThayDoi;
    }

    public void setSoLuongThayDoi(int soLuongThayDoi) {
        this.soLuongThayDoi = soLuongThayDoi;
    }

    public LocalDateTime getNgayGioTao() {
        return ngayGioTao;
    }

    public void setNgayGioTao(LocalDateTime ngayGioTao) {
        this.ngayGioTao = ngayGioTao;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return "LichSuKhoDTO{" +
                "maLichSu=" + maLichSu +
                ", maSach=" + maSach +
                ", loaiGiaoDich='" + loaiGiaoDich + '\'' +
                ", soLuongThayDoi=" + soLuongThayDoi +
                ", ngayGioTao=" + ngayGioTao +
                '}';
    }
}
