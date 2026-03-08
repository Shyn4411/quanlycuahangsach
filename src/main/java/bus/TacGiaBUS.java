package bus;

import dao.TacGiaDAO;
import dto.TacGiaDTO;
import java.util.List;

public class TacGiaBUS {
    private TacGiaDAO tgDAO = new TacGiaDAO();

    public List<TacGiaDTO> getAll() {
        return tgDAO.getAll();
    }

    public TacGiaDTO getById(int id) {
        // Tủn có thể bổ sung hàm getById trong DAO nếu cần,
        // hoặc lọc từ danh sách getAll()
        return tgDAO.getAll().stream()
                .filter(tg -> tg.getMaTacGia() == id)
                .findFirst().orElse(null);
    }

    public String addTacGia(TacGiaDTO tg) {
        if (tg.getTenTacGia() == null || tg.getTenTacGia().trim().isEmpty()) {
            return "Lỗi: Tên tác giả không được để trống!";
        }
        if (tgDAO.checkTenDaTonTai(tg.getTenTacGia())) {
            return "Lỗi: Tên tác giả này đã tồn tại trong hệ thống!";
        }
        return tgDAO.insert(tg) ? "Thành công: Thêm tác giả mới!" : "Lỗi: Không thể thêm tác giả!";
    }

    public String updateTacGia(TacGiaDTO tg) {
        if (tg.getTenTacGia().trim().isEmpty()) {
            return "Lỗi: Tên tác giả không được trống!";
        }
        if (tgDAO.checkTenBiTrungKhiSua(tg.getTenTacGia(), tg.getMaTacGia())) {
            return "Lỗi: Tên tác giả mới bị trùng với người khác!";
        }
        return tgDAO.update(tg) ? "Thành công: Cập nhật thông tin!" : "Lỗi: Không thể cập nhật!";
    }

    public String deleteTacGia(int id) {
        return tgDAO.delete(id) ? "Thành công: Đã chuyển trạng thái Ngừng hoạt động!" : "Lỗi: Thất bại!";
    }
}