package bus;

import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import dao.SachDAO;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonDTO;
import dto.SachDTO;
import enums.LoaiHoaDon;
import enums.TrangThaiGiaoDich;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BanHangBUS {

    private SachDAO sachDAO = new SachDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO ctHoaDonDAO = new ChiTietHoaDonDAO();
    public boolean themVaoGioHang(String maSachCode, int soLuongThem, List<ChiTietHoaDonDTO> dsGioHang) {
        // Lấy danh sách sách đang bán để tra cứu (Tủn có thể tối ưu bằng cách viết hàm getSachByCode trong DAO)
        List<SachDTO> dsSach = sachDAO.getAll();
        SachDTO sachChon = null;

        for (SachDTO s : dsSach) {
            if (s.getMaSachCode().equalsIgnoreCase(maSachCode) && s.getTrangThai().name().equals("DangBan")) {
                sachChon = s;
                break;
            }
        }

        // Lỗi: Không tìm thấy sách hoặc số lượng kho không đủ
        if (sachChon == null || sachChon.getSoLuongTon() < soLuongThem) {
            return false;
        }

        // Kiểm tra xem sách này đã có trong giỏ hàng chưa?
        for (ChiTietHoaDonDTO ct : dsGioHang) {
            if (ct.getMaSach() == sachChon.getMaSach()) {
                // Có rồi thì cộng dồn số lượng, nhưng phải check xem cộng vô có lố tồn kho không
                int tongSLMoi = ct.getSoLuong() + soLuongThem;
                if (tongSLMoi > sachChon.getSoLuongTon()) return false;

                ct.setSoLuong(tongSLMoi);

                // Cập nhật lại thành tiền = Đơn giá * Số lượng mới
                BigDecimal sl = new BigDecimal(tongSLMoi);
                ct.setThanhTien(ct.getDonGia().multiply(sl));
                return true;
            }
        }

        // Nếu chưa có trong giỏ thì tạo dòng mới
        ChiTietHoaDonDTO ctMoi = new ChiTietHoaDonDTO();
        ctMoi.setMaSach(sachChon.getMaSach());

        // MẸO: Tủn nhớ thêm thuộc tính `tenSach` (String) vào ChiTietHoaDonDTO để hiển thị lên bảng nhé!
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
        // Thực thu = Tổng tiền hàng - Giảm giá + Phí khác (Giao hàng, VAT...)
        return tongTien - giamGia + phiKhac;
    }

    // ==========================================================
    // 3. XỬ LÝ THANH TOÁN (TRANSACTION: LƯU HÓA ĐƠN & TRỪ KHO)
    // ==========================================================
    public boolean thanhToanHoaDon(Integer maNV, Integer maKH, Integer maKM, List<ChiTietHoaDonDTO> dsGioHang, double tongTien, double giamGia, double thucThu) {
        try {
            // BƯỚC 1: Lập Hóa Đơn Mới
            HoaDonDTO hd = new HoaDonDTO();
            hd.setMaNV(maNV);
            hd.setMaKH(maKH); // Có thể null
            hd.setMaKM(maKM); // Có thể null
            hd.setTongTien(new BigDecimal(tongTien));
            hd.setTienGiam(new BigDecimal(giamGia));
            hd.setThanhTien(new BigDecimal(thucThu));
            hd.setLoaiHoaDon(LoaiHoaDon.TaiQuay); // Mặc định đơn mua trực tiếp

            // Ép cứng trạng thái Hoàn Thành từ code để tránh rủi ro do DB tự sinh
            hd.setTrangThai(TrangThaiGiaoDich.HoanThanh);

            // BƯỚC 2: Lưu Hóa Đơn xuống DB và lấy Mã Hóa Đơn mới
            int maHoaDonMoi = hoaDonDAO.insert(hd);

            if (maHoaDonMoi > 0) {
                // BƯỚC 3: Lưu từng dòng Chi tiết hóa đơn VÀ Trừ tồn kho
                for (ChiTietHoaDonDTO ct : dsGioHang) {
                    ct.setMaHD(maHoaDonMoi); // Gắn ID hóa đơn cha vào

                    // Lưu xuống bảng ChiTietHoaDon
                    boolean luuCTHD = ctHoaDonDAO.insert(ct);

                    // Trừ tồn kho trong bảng Sach
                    if (luuCTHD) {
                        sachDAO.truTonKho(ct.getMaSach(), ct.getSoLuong());
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================================
    // 4. DUYỆT ĐƠN ONLINE (Mock Data để test giao diện)
    // ==========================================================
    public List<Object[]> getDonOnlinePending() {
        // Tủn thay bằng code gọi DAO lấy danh sách đơn có trạng thái "Chờ Duyệt" nhé
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{"HD001", "Trần Văn A", "08/03/2026 10:30", "250,000", "Chờ Duyệt"});
        list.add(new Object[]{"HD002", "Lê Thị B", "08/03/2026 11:15", "180,000", "Chờ Duyệt"});
        return list;
    }

    public boolean duyetDonOnline(String maHD, String tenDangNhap) {
        // Logic:
        // 1. Lấy danh sách Chi tiết hóa đơn của mã HD này
        // 2. Chạy vòng lặp gọi sachDAO.truTonKho()
        // 3. Đổi trạng thái Hóa Đơn thành "Đã Duyệt"
        System.out.println("Đã duyệt đơn " + maHD + " bởi " + tenDangNhap);
        return true;
    }
}