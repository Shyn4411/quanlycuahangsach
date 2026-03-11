package enums;

public enum TrangThaiKhuyenMai {
    HOAT_DONG("HOẠT ĐỘNG"),
    HET_HAN("HẾT HẠN");

    private final String display;

    TrangThaiKhuyenMai (String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
