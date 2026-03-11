package enums;

public enum Role {
    ADMIN(1, "Quản trị viên"),
    NHANVIEN_BANHANG(2, "Nhân viên bán hàng"),
    NHANVIEN_KHO(3, "Nhân viên kho"),
    KHACH_HANG(4, "Khách hàng");

    private int maQuyen;
    private String tenChucVu;

    Role(int maQuyen, String tenChucVu) {
        this.maQuyen = maQuyen;
        this.tenChucVu = tenChucVu;
    }

    public int getMaQuyen() {
        return maQuyen;
    }

    public String getTenChucVu() {
        return tenChucVu;
    }

    public static Role fromRole(int maQuyen) {
        for (Role role : Role.values()) {
            if (role.maQuyen == maQuyen) {
                return role;
            }
        }
        return null;
    }

}