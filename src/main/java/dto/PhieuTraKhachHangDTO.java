package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PhieuTraKhachHangDTO {
    private int maPTK;
    private String maPTKCode;
    private int maHD;
    private int maNV;
    private String lyDo;
    private BigDecimal tienHoan;
    private LocalDateTime ngayTao;

    public PhieuTraKhachHangDTO() {
    }

    public PhieuTraKhachHangDTO(String maPTKCode, int maPTK, int maNV, int maHD, String lyDo, BigDecimal tienHoan, LocalDateTime ngayTao) {
        this.maPTKCode = maPTKCode;
        this.maPTK = maPTK;
        this.maNV = maNV;
        this.maHD = maHD;
        this.lyDo = lyDo;
        this.tienHoan = tienHoan;
        this.ngayTao = ngayTao;
    }

    public int getMaPTK() {
        return maPTK;
    }

    public void setMaPTK(int maPTK) {
        this.maPTK = maPTK;
    }

    public String getMaPTKCode() {
        return maPTKCode;
    }

    public void setMaPTKCode(String maPTKCode) {
        this.maPTKCode = maPTKCode;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public BigDecimal getTienHoan() {
        return tienHoan;
    }

    public void setTienHoan(BigDecimal tienHoan) {
        this.tienHoan = tienHoan;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "PhieuTraKhachHangDTO{" +
                "maPTK=" + maPTK +
                ", maPTKCode='" + maPTKCode + '\'' +
                ", maHD=" + maHD +
                ", tienHoan=" + tienHoan +
                '}';
    }
}
