package enums;

//Bao gồm cho các Bảng:
// Khách hàng
// Thể loại
// Tác giả
// Nhà xuất bản
// Nhà cung cấp


public enum TrangThaiCoBan {
    HOAT_DONG("HOẠT ĐỘNG"),
    NGUNG_HOAT_DONG("NGỪNG HOẠT ĐỘNG");

    private final String display;

    TrangThaiCoBan(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
