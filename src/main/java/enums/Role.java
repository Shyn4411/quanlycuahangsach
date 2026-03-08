package enums;

public enum Role {
    ADMIN("Quản trị viên"),
    NHANVIEN_BANHANG("Nhân viên bán hàng"),
    NHANVIEN_KHO("Nhân viên kho");

    private String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}