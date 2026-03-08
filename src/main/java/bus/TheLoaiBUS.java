package bus;

import dao.TheLoaiDAO;
import dto.TheLoaiDTO;
import java.util.List;

public class TheLoaiBUS {

    private TheLoaiDAO theLoaiDAO = new TheLoaiDAO();

    public List<TheLoaiDTO> getAll() {
        return theLoaiDAO.getAll();
    }

    public TheLoaiDTO getById(int id) {
        if (id <= 0) return null;
        return theLoaiDAO.getById(id); // Sử dụng biến theLoaiDAO chung ở trên
    }

    public String addTheLoai(TheLoaiDTO theLoai) {
        if (theLoai.getTenLoai() == null || theLoai.getTenLoai().trim().isEmpty()) {
            return "Lỗi: Tên thể loại không được để trống";
        }

        int idMoi = theLoaiDAO.insert(theLoai);
        if (idMoi > 0) {
            return "Thành công: Đã thêm thể loại mới!";
        }
        return "Lỗi: Không thể thêm thể loại (Tên có thể đã tồn tại)!";
    }

    public String updateTheLoai(TheLoaiDTO theLoai) {
        if (theLoai.getTenLoai() == null || theLoai.getTenLoai().trim().isEmpty()) {
            return "Lỗi: Tên thể loại không được để trống!";
        }

        if (theLoaiDAO.update(theLoai)) {
            return "Thành công: Đã cập nhật thông tin thể loại!";
        }
        return "Lỗi: Không thể cập nhật thể loại!";
    }

    public String deleteTheLoai(int maLoai) {
        if (theLoaiDAO.delete(maLoai)) {
            return "Thành công: Đã xóa thể loại!";
        }
        return "Lỗi: Không thể xóa thể loại này!";
    }
}