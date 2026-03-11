package bus;

import dao.ThanhToanDAO;
import dto.ThanhToanDTO;
import enums.TrangThaiGiaoDich;
import enums.TrangThaiThanhToan;

import java.math.BigDecimal;
import java.util.List;

public class ThanhToanBUS {

    private ThanhToanDAO thanhToanDAO = new ThanhToanDAO();

    public List<ThanhToanDTO> getAll() {
        return thanhToanDAO.getAll();
    }

    public ThanhToanDTO getByMaHD(int maHD) {
        if (maHD <= 0) return null;
        return thanhToanDAO.getByMaHD(maHD);
    }

    private String validateThanhToan(ThanhToanDTO tt) {
        if (tt.getMaHD() <= 0) return "Lỗi: Không xác định được hóa đơn thanh toán!";

        if (tt.getPhuongThuc() == null || tt.getPhuongThuc().name().trim().isEmpty()) {
            return "Lỗi: Phải chọn phương thức thanh toán (Tiền mặt/ Chuyển khoản)!";
        }

        if (tt.getSoTien() == null || tt.getSoTien().compareTo(BigDecimal.ZERO) <= 0) {
            return "Lỗi: Số tiền thanh toán phải lớn hơn 0!";
        }

        if (tt.getGhiChuGiaoDich() != null) {
            tt.setGhiChuGiaoDich(tt.getGhiChuGiaoDich().trim());
        }

        return null;
    }

    public String addThanhToan(ThanhToanDTO tt) {
        String error = validateThanhToan(tt);
        if (error != null) return "Lỗi: " + error;

        if (tt.getTrangThai() == null) {
            tt.setTrangThai(TrangThaiThanhToan.valueOf("ThanhCong"));
        }

        if (thanhToanDAO.insert(tt)) {
            return "Thành công: Đã ghi nhận thanh toán!";
        }
        return "Lỗi: Không thể ghi nhận giao dịch thanh toán này!";
    }

    public String updateTrangThaiThanhToan(int maThanhToan, String trangThaiMoi) {
        if (maThanhToan <= 0) return "Lỗi: Mã thanh toán không hợp lệ!";

        if (thanhToanDAO.updateTrangThai(maThanhToan, trangThaiMoi)) {
            return "Thành công: Đã cập nhật trạng thái giao dịch!";
        }
        return "Lỗi: Không thể cập nhật trạng thái!";
    }
}