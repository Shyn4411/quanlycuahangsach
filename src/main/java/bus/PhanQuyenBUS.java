package bus;

import dao.PhanQuyenDAO;
import dto.PhanQuyenDTO;
import java.util.List;

public class PhanQuyenBUS {

    private PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();

    public List<PhanQuyenDTO> getAll() {
        return phanQuyenDAO.getAll();
    }

    public PhanQuyenDTO getById(int maQuyen) {
        if (maQuyen <= 0) return null;
        return phanQuyenDAO.getById(maQuyen);
    }


    private String validatePhanQuyen(PhanQuyenDTO pq) {
        if (pq.getMaCode() == null || pq.getMaCode().trim().isEmpty())
            return "Mã code quyền không được để trống (VD: ADMIN)!";
        pq.setMaCode(pq.getMaCode().trim().toUpperCase());

        if (pq.getTenQuyen() == null || pq.getTenQuyen().trim().isEmpty())
            return "Tên quyền không được để trống!";
        pq.setTenQuyen(pq.getTenQuyen().trim());

        return null;
    }

    public String addPhanQuyen(PhanQuyenDTO pq) {
        String error = validatePhanQuyen(pq);
        if (error != null) return "Lỗi: " + error;

        if (phanQuyenDAO.checkTrungMaCode(pq.getMaCode())) {
            return "Lỗi: Mã code phân quyền này đã tồn tại!";
        }

        if (phanQuyenDAO.insert(pq)) {
            return "Thành công: Đã thêm quyền mới!";
        }
        return "Lỗi: Không thể thêm quyền!";
    }

    public String updatePhanQuyen(PhanQuyenDTO pq) {
        if (pq.getMaQuyen() <= 0) return "Lỗi: Mã quyền không hợp lệ!";

        String error = validatePhanQuyen(pq);
        if (error != null) return "Lỗi: " + error;

        if (phanQuyenDAO.checkMaCodeBiTrungKhiSua(pq.getMaCode(), pq.getMaQuyen())) {
            return "Lỗi: Mã code này đang được sử dụng bởi một quyền khác!";
        }

        if (phanQuyenDAO.update(pq)) {
            return "Thành công: Đã cập nhật phân quyền!";
        }
        return "Lỗi: Cập nhật thất bại!";
    }
}