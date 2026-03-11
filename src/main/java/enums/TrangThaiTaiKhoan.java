package enums;

public enum TrangThaiTaiKhoan {
    HOAT_DONG("HOẠT ĐỘNG"),
    KHOA("KHÓA");

    private final String display;

    TrangThaiTaiKhoan(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
