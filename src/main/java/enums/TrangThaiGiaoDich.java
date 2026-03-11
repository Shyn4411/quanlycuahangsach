package enums;

public enum TrangThaiGiaoDich {
    CHO_XU_LY("CHỜ XỬ LÝ"),
    HOAN_THANH("HOÀN THÀNH"),
    DA_HUY("ĐÃ HỦY");

    private final String display;

    TrangThaiGiaoDich (String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
