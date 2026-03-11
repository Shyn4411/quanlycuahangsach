package utils;

import enums.Role;
import session.UserSession;

public class PermissionUtils {

    public static boolean isAdmin() {
        return UserSession.getRole() == Role.ADMIN;
    }

    public static boolean isKhachHang() {
        return UserSession.getRole() == Role.KHACH_HANG;
    }

    public static boolean isBanHang() {
        return UserSession.getRole() == Role.NHANVIEN_BANHANG;
    }

    public static boolean isQuanKho() {
        return UserSession.getRole() == Role.NHANVIEN_KHO;
    }

    public static boolean canAccessBanHang() {
        return isAdmin() || isBanHang();
    }

    public static boolean canAccessKho() {
        return isAdmin() || isQuanKho();
    }

    public static boolean isEmployee() {
        return isAdmin() || isBanHang() || isQuanKho();
    }

}
