package bus;

import dao.NhaCungCapDAO;
import dto.NhaCungCapDTO;
import java.util.List;

public class NhaCungCapBUS {
    private NhaCungCapDAO nccDAO = new NhaCungCapDAO();

    public List<NhaCungCapDTO> getAll() {
        return nccDAO.getAll();
    }

    private String validateNhaCungCap(NhaCungCapDTO ncc) {
        if (ncc.getTenNCC() == null || ncc.getTenNCC().trim().isEmpty())
            return "Tên nhà cung cấp không được để trống!";
        ncc.setTenNCC(ncc.getTenNCC().trim());
        if (ncc.getTenNCC().length() > 255)
            return "Tên nhà cung cấp quá dài (tối đa 255 ký tự)!";

        if (ncc.getSoDienThoai() == null || ncc.getSoDienThoai().trim().isEmpty())
            return "Số điện thoại không được để trống!";
        ncc.setSoDienThoai(ncc.getSoDienThoai().trim());
        if (!ncc.getSoDienThoai().matches("0\\d{9}"))
            return "SĐT phải bắt đầu bằng số 0 và gồm 10 chữ số!";

        if (ncc.getDiaChi() != null) {
            ncc.setDiaChi(ncc.getDiaChi().trim());
        }
        return null;
    }

    public String addNhaCungCap(NhaCungCapDTO ncc) {
        String error = validateNhaCungCap(ncc);
        if (error != null) return "Lỗi: " + error;

        if (nccDAO.checkSoDienThoaiDaTonTai(ncc.getSoDienThoai())) {
            return "Lỗi: Số điện thoại này đã tồn tại, trùng với một nhà cung cấp khác!";
        }

        if (nccDAO.insert(ncc)) {
            return "Thành công: Đã thêm nhà cung cấp mới!";
        }
        return "Lỗi: Không thể thêm nhà cung cấp!";
    }

    public String updateNhaCungCap(NhaCungCapDTO ncc) {
        if (ncc.getMaNCC() <= 0) return "Lỗi: Mã nhà cung cấp không hợp lệ!";

        String error = validateNhaCungCap(ncc);
        if (error != null) return "Lỗi: " + error;

        if (nccDAO.checkSoDienThoaiBiTrungKhiSua(ncc.getSoDienThoai(), ncc.getMaNCC())) {
            return "Lỗi: Số điện thoại bị trùng với nhà cung cấp khác!";
        }

        if (nccDAO.update(ncc)) {
            return "Thành công: Đã cập nhật thông tin nhà cung cấp!";
        }
        return "Lỗi: Cập nhật thất bại!";
    }

    public String deleteNhaCungCap(int maNCC) {
        if (nccDAO.delete(maNCC)) {
            return "Thành công: Đã ngừng giao dịch với nhà cung cấp này!";
        }
        return "Lỗi: Thao tác thất bại!";
    }
}
