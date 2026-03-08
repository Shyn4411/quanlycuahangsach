package bus;

import dao.NhaXuatBanDAO;
import dto.NhaXuatBanDTO;
import java.util.List;

public class NhaXuatBanBUS {

    private NhaXuatBanDAO nxbDAO = new NhaXuatBanDAO();

    public List<NhaXuatBanDTO> getAll() {
        return nxbDAO.getAll();
    }

    private String validateNhaXuatBan(NhaXuatBanDTO nxb) {
        if (nxb.getTenNXB() == null || nxb.getTenNXB().trim().isEmpty()) {
            return "Tên nhà xuất bản không được để trống!";
        }
        nxb.setTenNXB(nxb.getTenNXB().trim());

        if (nxb.getTenNXB().length() > 100) {
            return "Tên nhà xuất bản quá dài (tối đa 100 ký tự)!";
        }
        return null;
    }

    public String addNhaXuatBan(NhaXuatBanDTO nxb) {
        String error = validateNhaXuatBan(nxb);
        if (error != null) return "Lỗi: " + error;

        if (nxbDAO.checkTenDaTonTai(nxb.getTenNXB())) {
            return "Lỗi: Tên nhà xuất bản này đã tồn tại!";
        }

        if (nxbDAO.insert(nxb)) {
            return "Thành công: Đã thêm nhà xuất bản mới!";
        }
        return "Lỗi: Không thể thêm nhà xuất bản!";
    }

    public String updateNhaXuatBan(NhaXuatBanDTO nxb) {
        if (nxb.getMaNXB() <= 0) return "Lỗi: Mã nhà xuất bản không hợp lệ!";

        String error = validateNhaXuatBan(nxb);
        if (error != null) return "Lỗi: " + error;

        if (nxbDAO.checkTenBiTrungKhiSua(nxb.getTenNXB(), nxb.getMaNXB())) {
            return "Lỗi: Tên này đã bị trùng với một nhà xuất bản khác!";
        }

        if (nxbDAO.update(nxb)) {
            return "Thành công: Đã cập nhật thông tin nhà xuất bản!";
        }
        return "Lỗi: Cập nhật thất bại!";
    }

    public String deleteNhaXuatBan(int maNXB) {
        if (nxbDAO.delete(maNXB)) {
            return "Thành công: Đã ngừng hoạt động nhà xuất bản này!";
        }
        return "Lỗi: Thao tác thất bại!";
    }

    public NhaXuatBanDTO getById(int id) {
        // Nếu id truyền vào tào lao (<= 0) thì khỏi tìm cho mệt
        if (id <= 0) return null;
        return nxbDAO.getById(id);
    }
}