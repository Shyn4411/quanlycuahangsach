package bus;

import dao.SachDAO;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonDTO;
import dto.SachDTO;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiGiaoDich;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BanHangBUS {

    private SachDAO sachDAO = new SachDAO();
    // Thay vì gọi từng DAO lắt nhắt, ta gọi HoaDonBUS để nó lo trọn gói từ A-Z
    private HoaDonBUS hoaDonBUS = new HoaDonBUS();

    // ==========================================================
    // 1. QUẢN LÝ GIỎ HÀNG
    // ==========================================================
    public boolean themVaoGioHang(String maSachCode, int soLuongThem, List<ChiTietHoaDonDTO> dsGioHang) {
        List<SachDTO> dsSach = sachDAO.getAll();
        SachDTO sachChon = null;

        for (SachDTO s : dsSach) {
            if (s.getMaSachCode().equalsIgnoreCase(maSachCode) && s.getTrangThai().name().equals("DangBan")) {
                sachChon = s;
                break;
            }
        }

        if (sachChon == null || sachChon.getSoLuongTon() < soLuongThem) {
            return false;
        }

        for (ChiTietHoaDonDTO ct : dsGioHang) {
            if (ct.getMaSach() == sachChon.getMaSach()) {
                int tongSLMoi = ct.getSoLuong() + soLuongThem;
                if (tongSLMoi > sachChon.getSoLuongTon()) return false;

                ct.setSoLuong(tongSLMoi);
                ct.setThanhTien(ct.getDonGia().multiply(new BigDecimal(tongSLMoi)));
                return true;
            }
        }

        ChiTietHoaDonDTO ctMoi = new ChiTietHoaDonDTO();
        ctMoi.setMaSach(sachChon.getMaSach());
        ctMoi.setTenSach(sachChon.getTenSach());
        ctMoi.setSoLuong(soLuongThem);
        ctMoi.setDonGia(sachChon.getGiaBan());
        ctMoi.setThanhTien(sachChon.getGiaBan().multiply(new BigDecimal(soLuongThem)));

        dsGioHang.add(ctMoi);
        return true;
    }

    // ==========================================================
    // 2. TÍNH TOÁN TIỀN NONG
    // ==========================================================
    public double tinhTongTienHang(List<ChiTietHoaDonDTO> dsGioHang) {
        double tongTien = 0;
        for (ChiTietHoaDonDTO ct : dsGioHang) {
            tongTien += ct.getThanhTien().doubleValue();
        }
        return tongTien;
    }

    public double tinhTienThanhToan(double tongTien, double giamGia, double phiKhac) {
        return tongTien - giamGia + phiKhac;
    }

    // ==========================================================
    // 3. XỬ LÝ THANH TOÁN (GỌI SANG HOADON_BUS)
    // ==========================================================
    public boolean thanhToanHoaDon(Integer maNV, Integer maKH, Integer maKM, List<ChiTietHoaDonDTO> dsGioHang, double tongTien, double giamGia, double thucThu, PhuongThucThanhToan pttt) {
        try {
            // Đóng gói dữ liệu vào HoaDonDTO
            HoaDonDTO hd = new HoaDonDTO();
            hd.setMaNV(maNV);
            hd.setMaKH(maKH);
            hd.setMaKM(maKM);
            hd.setTongTien(new BigDecimal(tongTien));
            hd.setTienGiam(new BigDecimal(giamGia));
            // Đã xóa bỏ cái hd.set bị lỗi ở đây

            hd.setLoaiHoaDon(LoaiHoaDon.TaiQuay);
            hd.setTrangThai(TrangThaiGiaoDich.HoanThanh);

            // GỌI HÀM CỦA HOADON_BUS ĐỂ NÓ LƯU DB, LƯU THANH TOÁN VÀ TRỪ KHO
            // Lưu ý: Tủn phải đảm bảo hàm addHoaDon bên HoaDonBUS đã được Tủn update thêm tham số pttt.name() như tui hướng dẫn ở bước trước nhé!
            String ketQua = hoaDonBUS.addHoaDon(hd, dsGioHang, pttt);

            return ketQua.toLowerCase().contains("thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================================
    // 4. DUYỆT ĐƠN ONLINE (Mock Data)
    // ==========================================================
    public List<Object[]> getDonOnlinePending() {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{"HD001", "Trần Văn A", "08/03/2026 10:30", "250,000", "Chờ Duyệt"});
        list.add(new Object[]{"HD002", "Lê Thị B", "08/03/2026 11:15", "180,000", "Chờ Duyệt"});
        return list;
    }

    public boolean duyetDonOnline(String maHD, String tenDangNhap) {
        System.out.println("Đã duyệt đơn " + maHD + " bởi " + tenDangNhap);
        return true;
    }
}