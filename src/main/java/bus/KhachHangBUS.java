package bus;

import dao.KhachHangDAO;
import dao.TaiKhoanDAO;
import dto.KhachHangDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiTaiKhoan;

import java.util.List;

public class KhachHangBUS {

    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public List<KhachHangDTO> getAll() {
        return khachHangDAO.getAll();
    }

    private String validateKhachHang(KhachHangDTO kh) {
        if (kh.getHoTen() == null || kh.getHoTen().trim().isEmpty())
            return "Tên không được để trống!";
        kh.setHoTen(kh.getHoTen().trim());

        if (kh.getSoDienThoai() == null || kh.getSoDienThoai().trim().isEmpty())
            return "SĐT không được để trống!";
        kh.setSoDienThoai(kh.getSoDienThoai().trim());
        if (!kh.getSoDienThoai().matches("0\\d{9}"))
            return "SĐT phải bắt đầu bằng số 0 và đủ 10 chữ số!";

        if (kh.getDiaChi() != null) {
            kh.setDiaChi(kh.getDiaChi().trim());
            if (kh.getDiaChi().length() > 255)
                return "Địa chỉ quá dài (tối đa 255 ký tự)!";
        }

        return null;
    }

    // Thêm tham số matKhau vào hàm
    public String addKhachHang(KhachHangDTO kh, String matKhau) {

        if (khachHangDAO.checkSoDienThoaiDaTonTai(kh.getSoDienThoai())) {
            return "Lỗi: Số điện thoại này đã tồn tại trong hệ thống!";
        }

        // BƯỚC 1: TẠO TÀI KHOẢN TRƯỚC
        TaiKhoanDTO tk = new TaiKhoanDTO();
        tk.setMaQuyen(4); // Quyền khách hàng
        tk.setTenDangNhap(kh.getSoDienThoai()); // SĐT làm username
        tk.setMatKhau(matKhau); // Lấy mật khẩu từ giao diện truyền xuống
        tk.setTrangThai(enums.TrangThaiTaiKhoan.HOAT_DONG);

        int maTK = taiKhoanDAO.insertAndGetId(tk);

        if (maTK <= 0) {
            return "Lỗi: Không thể khởi tạo tài khoản cho khách hàng này!";
        }

        // BƯỚC 2: GẮN MÃ TÀI KHOẢN VÀO KHÁCH HÀNG VÀ LƯU
        kh.setMaKH(maTK);

        if (khachHangDAO.insert(kh)) {
            return "Thành công: Đã tạo khách hàng! (Tài khoản: " + kh.getSoDienThoai() + ")";
        } else {
            return "Lỗi: Không thể lưu thông tin khách hàng!";
        }
    }

    public String updateKhachHang(KhachHangDTO kh) {
        String error = validateKhachHang(kh);
        if (error != null) return "Lỗi: " + error;

        if (khachHangDAO.checkSoDienThoaiBiTrungKhiSua(kh.getSoDienThoai(), kh.getMaKH())) {
            return "Lỗi: Số điện thoại bị trùng với khách khác!";
        }

        if (khachHangDAO.update(kh)) {
            return "Thành công: Đã cập nhật!";
        }
        return "Lỗi: Cập nhật thất bại!";
    }

    public String deleteKhachHang(int maKH) {
        if (khachHangDAO.delete(maKH)) {
            return "Thành công: Đã ngừng hoạt động khách hàng!";
        }
        return "Lỗi: Thao tác thất bại!";
    }



    public KhachHangDTO getKhachHangByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        return khachHangDAO.getKhachHangByPhone(phone.trim());
    }

    public KhachHangDTO getKhachHangById(int maKH) {
        if (maKH <= 0) return null;
        return khachHangDAO.getKhachHangById(maKH);
    }

    public String updateTrangThai(int maKH, enums.TrangThaiCoBan trangThaiMoi) {
        if (khachHangDAO.updateTrangThai(maKH, trangThaiMoi.name())) {
            return "Thành công: Đã cập nhật trạng thái khách hàng!";
        }
        return "Lỗi: Không thể cập nhật trạng thái!";
    }

}