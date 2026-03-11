package bus;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO; // Cần import thêm thằng này
import dto.NhanVienDTO;
import dto.TaiKhoanDTO; // Cần dùng để đóng gói dữ liệu tài khoản
import enums.TrangThaiTaiKhoan;
import java.util.List;

public class NhanVienBUS {
    private NhanVienDAO nvDAO = new NhanVienDAO();
    private TaiKhoanDAO tkDAO = new TaiKhoanDAO(); // Quản lý phần đăng nhập

    public List<NhanVienDTO> getAll() {
        return nvDAO.getAll();
    }

    public NhanVienDTO getById(int maNV) {
        return nvDAO.getByID(maNV);
    }

    // --- LOGIC KIỂM TRA DỮ LIỆU ---
    private String validateNhanVien(NhanVienDTO nv, boolean isUpdate) {
        if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty())
            return "Tên nhân viên không được để trống!";

        if (nv.getSoDienThoai() == null || !nv.getSoDienThoai().matches("0\\d{9}"))
            return "SĐT phải bắt đầu bằng số 0 và gồm 10 chữ số!";

        if (!isUpdate) { // Nếu là thêm mới thì check thêm Username/Pass
            if (nv.getTenDangNhap() == null || nv.getTenDangNhap().length() < 4)
                return "Tên đăng nhập phải ít nhất 4 ký tự!";
            if (nv.getMatKhau() == null || nv.getMatKhau().length() < 4)
                return "Mật khẩu quá ngắn!";
        }
        return null;
    }

    // --- NGHIỆP VỤ THÊM MỚI (TẠO 2 BẢNG) ---
    public String addNhanVien(NhanVienDTO nv) {
        String err = validateNhanVien(nv, false);
        if (err != null) return "Lỗi: " + err;

        // 1. Check trùng dữ liệu
        if (nvDAO.checkSoDienThoaiDaTonTai(nv.getSoDienThoai()))
            return "Lỗi: Số điện thoại này đã được sử dụng!";
        if (tkDAO.checkTrungTenDangNhap(nv.getTenDangNhap()))
            return "Lỗi: Tên đăng nhập này đã tồn tại!";

        // 2. BƯỚC 1: Tạo tài khoản trước
        TaiKhoanDTO tk = new TaiKhoanDTO();
        tk.setTenDangNhap(nv.getTenDangNhap());
        tk.setMatKhau(nv.getMatKhau()); // Admin nhập từ Dialog
        tk.setMaQuyen(nv.getMaQuyen());
        tk.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);

        // Hàm này phải trả về MaTaiKhoan vừa tạo (AI dùng Generated Keys)
        int maTK = tkDAO.insertAndGetId(tk);

        if (maTK > 0) {
            // 3. BƯỚC 2: Dùng MaTK đó để tạo nhân viên
            nv.setMaTaiKhoan(maTK);
            if (nvDAO.insert(nv)) {
                return "Thành công: Đã thêm nhân viên và cấp tài khoản!";
            } else {
                // Nếu lỗi bước này, thực tế nên rollback xóa luôn tài khoản vừa tạo
                return "Lỗi: Không thể lưu thông tin nhân viên!";
            }
        }
        return "Lỗi: Không thể tạo tài khoản đăng nhập!";
    }

    // --- NGHIỆP VỤ CẬP NHẬT ---
    public String updateNhanVien(NhanVienDTO nv) {
        String err = validateNhanVien(nv, true);
        if (err != null) return "Lỗi: " + err;

        if (nvDAO.checkSoDienThoaiBiTrungKhiSua(nv.getSoDienThoai(), nv.getMaNV()))
            return "Lỗi: Số điện thoại bị trùng!";

        // Cập nhật info nhân viên (Họ tên, SĐT)
        boolean updateNV = nvDAO.update(nv);
        // Cập nhật quyền hạn bên bảng Tài Khoản (MaQuyen)
        boolean updateTK = tkDAO.updateQuyen(nv.getMaTaiKhoan(), nv.getMaQuyen());

        if (updateNV && updateTK) return "Thành công: Đã cập nhật thông tin!";
        return "Lỗi: Cập nhật thất bại!";
    }

    // --- NGHIỆP VỤ KHÓA/MỞ TÀI KHOẢN ---
    public String updateTrangThai(int maTK, TrangThaiTaiKhoan tt) {
        if (tkDAO.updateTrangThai(maTK, tt)) {
            return "Thành công: Đã chuyển trạng thái sang " + tt;
        }
        return "Lỗi: Thao tác thất bại!";
    }

    // --- NGHIỆP VỤ RESET MẬT KHẨU ---
    public String updatePassword(int maTK, String newPass) {
        if (newPass == null || newPass.trim().length() < 4)
            return "Lỗi: Mật khẩu mới phải từ 4 ký tự!";

        if (tkDAO.updatePassword(maTK, newPass)) {
            return "Thành công: Đã thay đổi mật khẩu!";
        }
        return "Lỗi: Không thể đổi mật khẩu!";
    }

    // --- XÓA NHÂN VIÊN ---
    public String deleteNhanVien(int maNV) {
        // Soft delete: Thực tế gọi hàm khóa tài khoản thì an toàn hơn xóa hẳn
        if (nvDAO.delete(maNV)) {
            return "Thành công: Đã ngừng hoạt động tài khoản nhân viên!";
        }
        return "Lỗi: Không thể thực hiện!";
    }
}