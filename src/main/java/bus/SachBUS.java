package bus;

import dao.SachDAO;
import dao.TacGia_SachDAO;
import dto.SachDTO;
import dto.TacGia_SachDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SachBUS {
    private SachDAO sachDAO = new SachDAO();
    private TacGia_SachDAO tacGia_SachDAO = new TacGia_SachDAO();

    public List<SachDTO> getAll() {
        return sachDAO.getAll();
    }

    public String addSach(SachDTO sach, List<Integer> danhSachMaTacGia) {
        if (sach.getTenSach() == null || sach.getTenSach().trim().isEmpty()){
            return "Lỗi: Tên sách không được để trống!";
        }
        if (sach.getGiaBan() == null || sach.getGiaBan().compareTo(BigDecimal.ZERO) < 0){
            return "Lỗi: Giá bán không hợp lệ!";
        }
        if (danhSachMaTacGia == null || danhSachMaTacGia.isEmpty()){
            return "Lỗi: Sách phải có ít nhất 1 tác giả!";
        }

        int maSachMoi = sachDAO.insert(sach);
        if (maSachMoi > 0){
            for (Integer maTacGia : danhSachMaTacGia){
                TacGia_SachDTO tacGiaDTO = new TacGia_SachDTO(maSachMoi, maTacGia);
                tacGia_SachDAO.insert(tacGiaDTO);
            }
            return "Thành công: Đã thêm sách mới!";
        }

        return "Lỗi: Hệ thống không thể thêm sách vào CSDL!";
    }


    public String updateSach(SachDTO sach, List<Integer> danhSachMaTacGiaMoi) {
        if (sach.getTenSach() == null || sach.getTenSach().trim().isEmpty()){
            return "Lỗi: Tên sách không được để trống!";
        }
        if (sach.getGiaBan() == null || sach.getGiaBan().compareTo(BigDecimal.ZERO) < 0){
            return "Lỗi: Giá bán không hợp lệ!";
        }
        if (danhSachMaTacGiaMoi == null || danhSachMaTacGiaMoi.isEmpty()){
            return "Lỗi: Sách phải có ít nhất 1 tác giả!";
        }

        boolean isSuccess = sachDAO.update(sach);
        if (isSuccess){
            tacGia_SachDAO.deleteByMaSach(sach.getMaSach());

                for (Integer maTacGia : danhSachMaTacGiaMoi) {
                    TacGia_SachDTO tacGia_sachDTO = new TacGia_SachDTO(sach.getMaSach(), maTacGia);
                    tacGia_SachDAO.insert(tacGia_sachDTO);
                }

            return "Thành công: Đã cập nhật thông tin sách!";
        }
        return "Lỗi: Không thể cập nhật sách!";
    }

    public String deleteSach(int maSach) {
        if (sachDAO.delete(maSach)){
            return "Thành công: Đã chuyển trạng thái thành NGỪNG BÁN!";
        }
        return "Lỗi: Không thể ngừng bán sách này!";
    }

    public SachDTO getById(int id) {
        List<SachDTO> ds = getAll();
        for (SachDTO s : ds) {
            if (s.getMaSach() == id) {
                return s;
            }
        }
        return null;
    }


    public int getSoLuongSachSapHet(int gioiHan) {
        return sachDAO.getSoLuongSachSapHet(gioiHan);
    }

    public Map<String, Integer> getTopSachBanChay(int soLuong) {
        return sachDAO.getTopSachBanChay(soLuong);
    }

}
