package bus;
import dao.NhanVienDAO;
import dto.NhanVienDTO;
import java.util.List;

public class NhanVienBUS {
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public List<NhanVienDTO> getAll() {
        return nhanVienDAO.getAll();
    }

    private String validateNhanVien(NhanVienDTO nv) {
        if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty())
            return "Tên nhân viên không được để trống!";
        nv.setHoTen(nv.getHoTen().trim());

        if (nv.getSoDienThoai() != null && !nv.getSoDienThoai().trim().isEmpty()) {
            nv.setSoDienThoai(nv.getSoDienThoai().trim());
            if (!nv.getSoDienThoai().matches("0\\d{9}")) {
                return "SĐT phải bắt đầu bằng số 0 và gồm 10 chữ số!";
            }
        }
        return null;
    }


    public String addNhanVien(NhanVienDTO nv) {
        String error = validateNhanVien(nv);
        if (error != null) return "Lỗi: " + error;

        if (nhanVienDAO.checkSoDienThoaiDaTonTai(nv.getSoDienThoai())) {
            return "Lỗi: Số điện thoại này đã được nhân viên khác sử dụng!";
        }
        if (nhanVienDAO.insert(nv)) {
            return "Thành công: Đã thêm nhân viên mới!";
        }
        return "Lỗi: Không thể thêm nhân viên!";
    }

    public String updateNhanVien(NhanVienDTO nv) {
        if (nv.getMaNV() <= 0) return "Lỗi: Mã nhân viên không hợp lệ!";

        String error = validateNhanVien(nv);
        if (error != null) return "Lỗi: " + error;

        if (nhanVienDAO.checkSoDienThoaiBiTrungKhiSua(nv.getSoDienThoai(), nv.getMaNV())) {
            return "Lỗi: Số điện thoại bị trùng với nhân viên khác!";
        }

        if (nhanVienDAO.update(nv)) {
            return "Thành công: Đã cập nhật thông tin nhân viên!";
        }
        return "Lỗi: Cập nhật thất bại!";
    }

    public String deleteNhanVien(int maNV) {
        if (nhanVienDAO.delete(maNV)) {
            return "Thành công: Đã xóa nhân viên (hoặc ngừng hoạt động)!";
        }
        return "Lỗi: Thao tác thất bại!";
    }

}
