package bus;

import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import dao.LichSuKhoDAO;
import dao.SachDAO;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonDTO;
import dto.LichSuKhoDTO;
import enums.LoaiGiaoDich;
import enums.PhuongThucThanhToan;

import java.util.List;

public class HoaDonBUS {

    private dao.ThanhToanDAO thanhToanDAO = new dao.ThanhToanDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private SachDAO sachDAO = new SachDAO();
    private LichSuKhoDAO lichSuKhoDAO = new LichSuKhoDAO();

    public List<HoaDonDTO> getAll() {
        return hoaDonDAO.getAll();
    }

    public String addHoaDon(HoaDonDTO hoaDon, List<ChiTietHoaDonDTO> danhSachCTHD, PhuongThucThanhToan pttt) {
        if (hoaDon.getMaNV() != null && hoaDon.getMaNV() <= 0) {
            return "Lỗi: Nhân viên không hợp lệ!";
        }
        if (danhSachCTHD == null || danhSachCTHD.isEmpty()) {
            return "Lỗi: Hóa đơn phải có ít nhất 1 sản phẩm!";
        }

        int maHoaDonMoi = hoaDonDAO.insert(hoaDon);

        if (maHoaDonMoi > 0) {

            // --- 2. THÊM ĐOẠN LƯU GIAO DỊCH THANH TOÁN VÀO DB ---
            dto.ThanhToanDTO tt = new dto.ThanhToanDTO();
            tt.setMaHD(maHoaDonMoi);
            tt.setPhuongThuc(pttt); // Lấy chữ 'TienMat' hoặc 'ChuyenKhoan' từ Enum
            tt.setSoTien(hoaDon.getThanhTien());
            tt.setGhiChuGiaoDich("Thanh toán tại quầy");

            thanhToanDAO.insert(tt);
            // ---------------------------------------------------

            // 3. CODE TRỪ KHO CỦA ÔNG (Giữ nguyên không sai một ly)
            for (ChiTietHoaDonDTO chiTietHoaDon : danhSachCTHD) {
                chiTietHoaDon.setMaHD(maHoaDonMoi);
                chiTietHoaDonDAO.insert(chiTietHoaDon);

                sachDAO.truTonKho(chiTietHoaDon.getMaSach(), chiTietHoaDon.getSoLuong());

                LichSuKhoDTO lichSu = new LichSuKhoDTO();
                lichSu.setMaSach(chiTietHoaDon.getMaSach());
                lichSu.setLoaiGiaoDich(enums.LoaiGiaoDich.BAN_HANG);
                lichSu.setLoaiChungTu(enums.LoaiChungTu.HOADON);
                lichSu.setMaChungTu(maHoaDonMoi);
                lichSu.setSoLuongThayDoi(-chiTietHoaDon.getSoLuong());
                lichSu.setGhiChu("Bán hàng theo hóa đơn " + String.format("HD%03d", maHoaDonMoi));

                lichSuKhoDAO.insert(lichSu);

            }
            return "Thành công: Đã tạo hóa đơn và xuất kho sách!";
        }
        return "Lỗi: Không thể tạo hóa đơn!";
    }

    public String deleteHoaDon(int maHoaDon, String lyDoHuy) {
        if (hoaDonDAO.delete(maHoaDon)) {

            List<ChiTietHoaDonDTO> dsChiTiet = chiTietHoaDonDAO.getByMaHD(maHoaDon);

            for (ChiTietHoaDonDTO ds : dsChiTiet) {

                sachDAO.congTonKho(ds.getMaSach(), ds.getSoLuong());

                LichSuKhoDTO lichSu = new LichSuKhoDTO();
                lichSu.setMaSach(ds.getMaSach());
                lichSu.setLoaiGiaoDich(LoaiGiaoDich.HUY_BAN_HANG);
                lichSu.setLoaiChungTu(enums.LoaiChungTu.HOADON);
                lichSu.setMaChungTu(maHoaDon);
                lichSu.setSoLuongThayDoi(ds.getSoLuong());
                lichSu.setGhiChu("Hoàn kho do hủy đơn " + String.format("HD%03d", maHoaDon) + " - Lý do: " + lyDoHuy);

                lichSuKhoDAO.insert(lichSu);
            }
            return "Thành công: Đã hủy đơn hàng và hoàn trả sách về kho!";
        }
        return "Lỗi: Không thể hủy đơn hàng";
    }

    public List<ChiTietHoaDonDTO> getChiTietByMaHD(int maHD) {
        if (maHD <= 0) return null;
        return chiTietHoaDonDAO.getByMaHD(maHD);
    }

    // ==========================================================
    // LOGIC PHỤC VỤ DASHBOARD MAINFRAME
    // ==========================================================

    public double getDoanhThuHomNay() {
        java.time.LocalDate today = java.time.LocalDate.now();
        double doanhThu = 0;

        List<HoaDonDTO> dsHoaDon = hoaDonDAO.getAll();
        for (HoaDonDTO hd : dsHoaDon) {
            // Check hóa đơn Hoàn Thành và lập trong ngày hôm nay
            if (hd.getTrangThai() == enums.TrangThaiGiaoDich.HoanThanh
                    && hd.getNgayTao() != null
                    && hd.getNgayTao().toLocalDate().equals(today)) {

                // Nếu ThanhTien null thì tự tính từ TongTien - TienGiam
                if(hd.getThanhTien() != null) {
                    doanhThu += hd.getThanhTien().doubleValue();
                } else {
                    double tong = hd.getTongTien() != null ? hd.getTongTien().doubleValue() : 0;
                    double giam = hd.getTienGiam() != null ? hd.getTienGiam().doubleValue() : 0;
                    doanhThu += (tong - giam);
                }
            }
        }
        return doanhThu;
    }

    public int getSoDonHomNay() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int soDon = 0;

        List<HoaDonDTO> dsHoaDon = hoaDonDAO.getAll();
        for (HoaDonDTO hd : dsHoaDon) {
            if (hd.getNgayTao() != null && hd.getNgayTao().toLocalDate().equals(today)) {
                soDon++;
            }
        }
        return soDon;
    }

    public List<HoaDonDTO> getRecentOrders(int limit) {
        List<HoaDonDTO> dsHoaDon = hoaDonDAO.getAll();

        // Sắp xếp ID giảm dần (đơn mới lên đầu)
        dsHoaDon.sort((hd1, hd2) -> Integer.compare(hd2.getMaHD(), hd1.getMaHD()));

        // Trả về số lượng dòng giới hạn
        if (dsHoaDon.size() > limit) {
            return dsHoaDon.subList(0, limit);
        }
        return dsHoaDon;
    }

    public dto.HoaDonDTO getHoaDonById(int maHD) {
        if (maHD <= 0) return null;
        return hoaDonDAO.getById(maHD);
    }

}