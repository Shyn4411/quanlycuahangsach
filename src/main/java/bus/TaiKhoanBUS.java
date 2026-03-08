package bus;

import dao.TaiKhoanDAO;
import dto.TaiKhoanDTO;
import java.util.List;

public class TaiKhoanBUS {

    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public List<TaiKhoanDTO> getAll() {
        return taiKhoanDAO.getAll();
    }

    public TaiKhoanDTO login(String username, String password) throws Exception {

        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập tên đăng nhập!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập mật khẩu!");
        }


        TaiKhoanDTO tk = taiKhoanDAO.getByTenDangNhap(username);

        if (tk == null) {
            throw new Exception("Tên đăng nhập không tồn tại!");
        }

        if (!tk.getMatKhau().equals(password)) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        if ("Khoa".equalsIgnoreCase(tk.getTrangThai().name())) {
            throw new Exception("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin!");
        }

        return tk;
    }

    public String addTaiKhoan(TaiKhoanDTO tk) {
        if (tk.getTenDangNhap() == null || tk.getTenDangNhap().trim().isEmpty()) return "Tên đăng nhập không được rỗng!";
        if (tk.getMatKhau() == null || tk.getMatKhau().trim().isEmpty()) return "Mật khẩu không được rỗng!";

        if (taiKhoanDAO.checkTrungTenDangNhap(tk.getTenDangNhap())) {
            return "Lỗi: Tên đăng nhập này đã có người sử dụng!";
        }

        if (taiKhoanDAO.insert(tk)) {
            return "Thành công: Đã tạo tài khoản mới!";
        }
        return "Lỗi: Không thể tạo tài khoản!";
    }

    public String lockTaiKhoan(int maTaiKhoan) {
        if (taiKhoanDAO.updateTrangThai(maTaiKhoan, "Khoa")) {
            return "Thành công: Đã khóa tài khoản!";
        }
        return "Lỗi: Không thể khóa tài khoản này!";
    }
}
