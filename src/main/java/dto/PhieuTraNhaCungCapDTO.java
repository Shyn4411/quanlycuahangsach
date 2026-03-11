package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PhieuTraNhaCungCapDTO {
    private int maPTN;
    private int maNV;
    private int maNCC;
    private String lyDo;
    private BigDecimal tongTienHoan;
    private LocalDateTime ngayTao;

    public PhieuTraNhaCungCapDTO() {
    }

    public PhieuTraNhaCungCapDTO(int maPTN, int maNV, int maNCC, BigDecimal tongTienHoan, String lyDo, LocalDateTime ngayTao) {
        this.maPTN = maPTN;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.tongTienHoan = tongTienHoan;
        this.lyDo = lyDo;
        this.ngayTao = ngayTao;
    }

    public int getMaPTN() {
        return maPTN;
    }

    public void setMaPTN(int maPTN) {
        this.maPTN = maPTN;
    }


    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public int getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(int maNCC) {
        this.maNCC = maNCC;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public BigDecimal getTongTienHoan() {
        return tongTienHoan;
    }

    public void setTongTienHoan(BigDecimal tongTienHoan) {
        this.tongTienHoan = tongTienHoan;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "PhieuTraNhaCungCapDTO{" +
                "maPTN=" + maPTN +
                ", maNCC=" + maNCC +
                ", tongTienHoan=" + tongTienHoan +
                '}';
    }
}
