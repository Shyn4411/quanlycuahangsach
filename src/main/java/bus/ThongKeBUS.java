package bus;

import dao.ThongKeDAO;
import java.util.ArrayList;
import java.util.List;

public class ThongKeBUS {

    private ThongKeDAO thongKeDAO = new ThongKeDAO();

    // ========================================================
    // PHẦN 1: CÁC HÀM CHO 3 THẺ TỔNG KẾT (TỔNG VỐN, DOANH THU)
    // ========================================================

    public double getTongDoanhThu(String tuNgay, String denNgay) {
        if (!validateDate(tuNgay, denNgay)) return 0;
        // Gọi DAO lấy tổng tiền các HÓA ĐƠN có trạng thái HOÀN THÀNH trong khoảng thời gian
        return thongKeDAO.getTongDoanhThu(tuNgay, denNgay);
    }

    public double getTongVon(String tuNgay, String denNgay) {
        if (!validateDate(tuNgay, denNgay)) return 0;
        // Gọi DAO lấy tổng tiền các PHIẾU NHẬP có trạng thái HOÀN THÀNH trong khoảng thời gian
        return thongKeDAO.getTongVon(tuNgay, denNgay);
    }

    public double getLoiNhuan(String tuNgay, String denNgay) {
        double doanhThu = getTongDoanhThu(tuNgay, denNgay);
        double von = getTongVon(tuNgay, denNgay);
        return doanhThu - von;
    }

    // ========================================================
    // PHẦN 2: 3 HÀM TƯƠNG ỨNG VỚI 3 LỰA CHỌN TRONG COMBOBOX
    // ========================================================

    // 1. TOP SÁCH BÁN CHẠY (Lọc theo ngày)
    public List<Object[]> getTopSachBanChay(String tuNgay, String denNgay) {
        if (!validateDate(tuNgay, denNgay)) return new ArrayList<>();
        return thongKeDAO.getTopSachBanChay(tuNgay, denNgay);
    }

    // 2. DOANH THU THEO THÁNG (Lọc theo ngày)
    public List<Object[]> getDoanhThuTheoThang(String tuNgay, String denNgay) {
        if (!validateDate(tuNgay, denNgay)) return new ArrayList<>();
        return thongKeDAO.getDoanhThuTheoThang(tuNgay, denNgay);
    }

    // 3. SÁCH SẮP HẾT HÀNG (Tồn kho < 10)
    public List<Object[]> getSachSapHet() {
        // Hàm này KHÔNG cần lọc theo ngày vì tồn kho là con số tính đến thời điểm hiện tại
        return thongKeDAO.getSachSapHet();
    }

    // ========================================================
    // HÀM KIỂM TRA LOGIC NGÀY THÁNG
    // ========================================================
    private boolean validateDate(String tuNgay, String denNgay) {
        if (tuNgay == null || denNgay == null || tuNgay.isEmpty() || denNgay.isEmpty()) {
            return false;
        }
        return true;
    }
}