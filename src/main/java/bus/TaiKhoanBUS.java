package bus;

import dao.TaiKhoanDAO;
import dto.TaiKhoanDTO;
import enums.TrangThaiTaiKhoan;
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

        TaiKhoanDTO tk = taiKhoanDAO.login(username, password);

        if (tk == null) {
            TaiKhoanDTO checkExist = taiKhoanDAO.getByTenDangNhap(username);
            if (checkExist == null) {
                throw new Exception("Tên đăng nhập không tồn tại!");
            }
            if (!checkExist.getMatKhau().equals(password)) {
                throw new Exception("Mật khẩu không chính xác!");
            }
            if (checkExist.getTrangThai() == TrangThaiTaiKhoan.KHOA) {
                throw new Exception("Tài khoản đã bị khóa. Vui lòng liên hệ Admin!");
            }
            throw new Exception("Lỗi đăng nhập không xác định!");
        }
        return tk;
    }

    public String addTaiKhoan(TaiKhoanDTO tk) {
        if (tk.getTenDangNhap() == null || tk.getTenDangNhap().trim().length() < 4)
            return "Lỗi: Tên đăng nhập phải từ 4 ký tự!";
        if (tk.getMatKhau() == null || tk.getMatKhau().trim().length() < 4)
            return "Lỗi: Mật khẩu quá ngắn!";

        if (taiKhoanDAO.checkTrungTenDangNhap(tk.getTenDangNhap())) {
            return "Lỗi: Tên đăng nhập này đã có người sử dụng!";
        }

        if (taiKhoanDAO.insertAndGetId(tk) > 0) {
            return "Thành công: Đã tạo tài khoản mới!";
        }
        return "Lỗi: Không thể tạo tài khoản!";
    }


    public String updateTrangThai(int maTaiKhoan, TrangThaiTaiKhoan trangThaiMoi) {
        if (taiKhoanDAO.updateTrangThai(maTaiKhoan, trangThaiMoi)) {
            return "Thành công: Đã cập nhật trạng thái tài khoản!";
        }
        return "Lỗi: Không thể cập nhật trạng thái!";
    }


    public String updatePassword(int maTK, String newPass) {
        if (newPass == null || newPass.trim().length() < 4) {
            return "Lỗi: Mật khẩu mới phải có ít nhất 4 ký tự!";
        }

        if (taiKhoanDAO.updatePassword(maTK, newPass)) {
            return "Thành công: Mật khẩu đã được thay đổi!";
        }
        return "Lỗi: Không thể cập nhật mật khẩu mới!";
    }



    public String register(String username, String password) {
        if (username.length() < 4) {
            return "Tên đăng nhập phải có ít nhất 4 ký tự!";
        }
        if (password.length() < 4) {
            return "Mật khẩu phải có ít nhất 4 ký tự!";
        }

        if (taiKhoanDAO.checkUsernameExists(username)) {
            return "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.";
        }

        TaiKhoanDTO newTK = new TaiKhoanDTO();
        newTK.setTenDangNhap(username);
        newTK.setMatKhau(password);
        newTK.setMaQuyen(4);

        if (taiKhoanDAO.insert(newTK)) {
            return "Thành công";
        } else {
            return "Lỗi hệ thống! Không thể tạo tài khoản lúc này.";
        }
    }
}