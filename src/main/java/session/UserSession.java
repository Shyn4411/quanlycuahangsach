package session;

import dto.TaiKhoanDTO;
import enums.Role;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
    private static TaiKhoanDTO currentUser;

    // Giỏ hàng lưu trên RAM: Map<MaSach, SoLuong>
    private static Map<Integer, Integer> gioHang = new HashMap<>();

    public static void login(TaiKhoanDTO user) {
        currentUser = user;
        gioHang.clear(); // Mỗi lần đăng nhập thì làm trống giỏ hàng
    }

    public static void logout() {
        currentUser = null;
        gioHang.clear();
    }

    public static TaiKhoanDTO getCurrentUser() {
        return currentUser;
    }

    public static Role getRole() {
        if (currentUser == null) {
            return null;
        }
        return Role.fromRole(currentUser.getMaQuyen());
    }

    // Thêm sách vào giỏ
    public static void themVaoGio(int maSach, int soLuong) {
        if (gioHang.containsKey(maSach)) {
            // Nếu đã có trong giỏ thì cộng dồn số lượng
            int slCu = gioHang.get(maSach);
            gioHang.put(maSach, slCu + soLuong);
        } else {
            // Nếu chưa có thì thêm mới
            gioHang.put(maSach, soLuong);
        }
    }

    // Lấy tổng số lượng MÓN HÀNG trong giỏ (để hiện lên Navbar)
    public static int getTongSoMonTrongGio() {
        int total = 0;
        for (int sl : gioHang.values()) {
            total += sl;
        }
        return total;
    }

    // Lấy toàn bộ giỏ hàng ra để tính tiền (Truyền qua màn hình Thanh Toán)
    public static Map<Integer, Integer> getGioHang() {
        return gioHang;
    }

    // Xóa giỏ hàng (Sau khi thanh toán thành công)
    public static void xoaGioHang() {
        gioHang.clear();
    }
}