package bus;

import dao.KhuyenMaiDAO;
import dto.KhuyenMaiDTO;
import java.math.BigDecimal;
import java.util.List;

public class KhuyenMaiBUS {

    private KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    public List<KhuyenMaiDTO> getAll() {
        return khuyenMaiDAO.getAll();
    }

    private String validateKhuyenMai(KhuyenMaiDTO km) {
        if (km.getMaCode() == null || km.getMaCode().trim().isEmpty()) return "Mã code khuyến mãi không được để trống!";
        km.setMaCode(km.getMaCode().trim().toUpperCase());

        if (km.getTenKM() == null || km.getTenKM().trim().isEmpty()) return "Tên khuyến mãi không được để trống!";
        km.setTenKM(km.getTenKM().trim());

        if (km.getPhanTramGiam() != null) {
            if (km.getPhanTramGiam().compareTo(BigDecimal.ZERO) < 0 || km.getPhanTramGiam().compareTo(new BigDecimal("100")) > 0) {
                return "Phần trăm giảm phải từ 0 đến 100!";
            }
        }

        if (km.getNgayBatDau() != null && km.getNgayKetThuc() != null) {
            if (km.getNgayBatDau().isAfter(km.getNgayKetThuc())) {
                return "Ngày kết thúc không được nhỏ hơn ngày bắt đầu!";
            }
        }

        return null;
    }

    public String addKhuyenMai(KhuyenMaiDTO km) {
        String error = validateKhuyenMai(km);
        if (error != null) return "Lỗi: " + error;

        if (khuyenMaiDAO.checkMaCodeDaTonTai(km.getMaCode())) {
            return "Lỗi: Mã code này đã tồn tại!";
        }

        if (khuyenMaiDAO.insert(km)) {
            return "Thành công: Đã tạo chương trình khuyến mãi!";
        }
        return "Lỗi: Không thể tạo khuyến mãi!";
    }

    public String updateKhuyenMai(KhuyenMaiDTO km) {
        if (km.getMaKM() <= 0) return "Lỗi: Mã khuyến mãi không hợp lệ!";

        String error = validateKhuyenMai(km);
        if (error != null) return "Lỗi: " + error;

        if (khuyenMaiDAO.checkMaCodeBiTrungKhiSua(km.getMaCode(), km.getMaKM())) {
            return "Lỗi: Mã code này đang được sử dụng ở chương trình khác!";
        }

        if (khuyenMaiDAO.update(km)) {
            return "Thành công: Đã cập nhật khuyến mãi!";
        }
        return "Lỗi: Cập nhật thất bại!";
    }
}