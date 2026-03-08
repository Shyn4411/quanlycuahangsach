package bus;

import dao.KhachHangDAO;
import dto.KhachHangDTO;
import java.util.List;

public class KhachHangBUS {

    private KhachHangDAO khachHangDAO = new KhachHangDAO();

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

    public String addKhachHang(KhachHangDTO kh) {
        String error = validateKhachHang(kh);
        if (error != null) return "Lỗi: " + error;


        if (khachHangDAO.checkSoDienThoaiDaTonTai(kh.getSoDienThoai())) {
            return "Lỗi: Số điện thoại này đã tồn tại!";
        }

        if (khachHangDAO.insert(kh)) {
            return "Thành công: Đã thêm khách hàng!";
        }
        return "Lỗi: Không thể thêm khách hàng!";
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
        // Kiểm tra xem người dùng có gõ khoảng trắng bậy bạ hay không
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        // Gọi DAO đi tìm
        return khachHangDAO.getKhachHangByPhone(phone.trim());
    }

}