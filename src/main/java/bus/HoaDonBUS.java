package bus;

import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import dao.LichSuKhoDAO;
import dao.SachDAO;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonDTO;
import dto.LichSuKhoDTO;
import dto.ThanhToanDTO;
import enums.LoaiGiaoDich;
import enums.PhuongThucThanhToan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
            try {

                ThanhToanDTO tt = new ThanhToanDTO();
                tt.setMaHD(maHoaDonMoi);
                tt.setPhuongThuc(pttt);
                tt.setSoTien(hoaDon.getThanhTien());
                tt.setGhiChuGiaoDich("Thanh toán tại quầy");

                thanhToanDAO.insert(tt);

                for (ChiTietHoaDonDTO chiTietHoaDon : danhSachCTHD) {
                    chiTietHoaDon.setMaHD(maHoaDonMoi);
                    chiTietHoaDonDAO.insert(chiTietHoaDon);

                    sachDAO.truTonKho(chiTietHoaDon.getMaSach(), chiTietHoaDon.getSoLuong());

                    LichSuKhoDTO lichSu = new LichSuKhoDTO();
                    lichSu.setMaSach(chiTietHoaDon.getMaSach());
                    lichSu.setLoaiGiaoDich(enums.LoaiGiaoDich.BAN_HANG);

                    lichSu.setLoaiChungTu(enums.LoaiChungTu.HOA_DON);

                    lichSu.setMaChungTu(maHoaDonMoi);
                    lichSu.setSoLuongThayDoi(-chiTietHoaDon.getSoLuong());
                    lichSu.setGhiChu("Bán hàng theo hóa đơn " + String.format("HD%03d", maHoaDonMoi));

                    lichSuKhoDAO.insert(lichSu);
                }
                return "Thành công: Đã tạo hóa đơn và xuất kho sách!";

            } catch (Exception e) {
                e.printStackTrace();
                hoaDonDAO.delete(maHoaDonMoi);
                return "Lỗi: Quá trình xử lý chi tiết thất bại, đã hoàn tác hóa đơn!";
            }
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
                lichSu.setLoaiChungTu(enums.LoaiChungTu.HOA_DON);

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


    public BigDecimal getDoanhThuHomNay() {
        return hoaDonDAO.getDoanhThuHomNay();
    }

    public int getSoDonHangMoi() {
        return hoaDonDAO.getSoDonHangMoi();
    }

    public BigDecimal getLoiNhuanHomNay() {
        return hoaDonDAO.getLoiNhuanHomNay();
    }

    public double[] getDoanhThuTheoTuanTrongThang() {
        return hoaDonDAO.getDoanhThuTheoTuanTrongThang();
    }

    public dto.HoaDonDTO getHoaDonById(int maHD) {
        if (maHD <= 0) return null;
        return hoaDonDAO.getById(maHD);
    }


    public String taoDonHangOnline(int maKhachHang, Map<Integer, Integer> gioHang, BigDecimal tongTien) {
        if (gioHang == null || gioHang.isEmpty()) {
            return "Lỗi: Giỏ hàng trống!";
        }

        boolean isSuccess = hoaDonDAO.insertDonHangOnline(maKhachHang, gioHang, tongTien);

        if (isSuccess) {
            return "Thành công: Đơn hàng đã được ghi nhận!";
        } else {
            return "Lỗi: Không thể lưu đơn hàng vào hệ thống lúc này.";
        }
    }

}