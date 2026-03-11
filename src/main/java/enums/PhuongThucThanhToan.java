package enums;

public enum PhuongThucThanhToan {
    TIEN_MAT("TIỀN MẶT"),
    CHUYEN_KHOAN("CHUYỂN KHOẢN");


    private final String display;

    PhuongThucThanhToan(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

}
