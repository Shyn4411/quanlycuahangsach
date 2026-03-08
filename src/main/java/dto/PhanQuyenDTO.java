package dto;

public class PhanQuyenDTO {
    private int maQuyen;
    private String maCode;
    private String tenQuyen;
    private String moTa;


    public PhanQuyenDTO() {}
    public PhanQuyenDTO(int maQuyen, String maCode, String tenQuyen, String moTa) {
        this.maQuyen = maQuyen;
        this.maCode = maCode;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
    }

    public String getMaCode() {
        return maCode;
    }

    public void setMaCode(String maCode) {
        this.maCode = maCode;
    }

    public int getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(int maQuyen) {
        this.maQuyen = maQuyen;
    }

    public String getTenQuyen() {
        return tenQuyen;
    }

    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return "PhanQuyenDTO{" +
                "maQuyen=" + maQuyen +
                ", maCode='" + maCode + '\'' +
                ", tenQuyen='" + tenQuyen + '\'' +
                '}';
    }
}
