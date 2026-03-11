package bus;

import dao.SachDAO;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonDTO;
import dto.SachDTO;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiGiaoDich;

import java.math.BigDecimal;
import java.util.List;

public class BanHangBUS {

    private SachDAO sachDAO = new SachDAO();
    private HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private dao.HoaDonDAO hoaDonDAO = new dao.HoaDonDAO();

    public boolean themVaoGioHang(String maSachCode, int soLuongThem, List<ChiTietHoaDonDTO> dsGioHang) {
        int idSach;
        try {
            idSach = Integer.parseInt(maSachCode.trim());
        } catch (NumberFormatException e) {
            System.out.println("Mã sách không hợp lệ, phải là số nguyên!");
            return false;
        }
        SachDTO sachChon = sachDAO.getById(idSach);
        if (sachChon == null || !sachChon.getTrangThai().name().equals("DANG_BAN") || sachChon.getSoLuongTon() < soLuongThem) {
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

    public boolean thanhToanHoaDon(Integer maNV, Integer maKH, Integer maKM, List<ChiTietHoaDonDTO> dsGioHang, double tongTien, double giamGia, double thucThu, PhuongThucThanhToan pttt) {
        try {
            HoaDonDTO hd = new HoaDonDTO();
            hd.setMaNV(maNV);
            hd.setMaKH(maKH);
            hd.setMaKM(maKM);
            hd.setTongTien(new BigDecimal(tongTien));
            hd.setTienGiam(new BigDecimal(giamGia));

            hd.setLoaiHoaDon(LoaiHoaDon.TAI_QUAY);
            hd.setTrangThai(TrangThaiGiaoDich.HOAN_THANH);

            String ketQua = hoaDonBUS.addHoaDon(hd, dsGioHang, pttt);

            return ketQua.toLowerCase().contains("thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getDonOnlinePending() {

        return hoaDonDAO.getDonOnlinePending();
    }

    public boolean duyetDonOnline(int maHD, String tenDangNhap) {
        return hoaDonDAO.duyetDonOnline(maHD, tenDangNhap);
    }

    public String huyDonOnline(int maHD, String lyDo) {
        if (lyDo == null || lyDo.trim().isEmpty()) {
            return "Lỗi: Vui lòng cung cấp lý do hủy đơn!";
        }

        boolean thanhCong = hoaDonDAO.huyDonOnline(maHD, lyDo);

        if (thanhCong) {
            return "Đã từ chối và hủy đơn hàng HD" + String.format("%03d", maHD) + " thành công!";
        } else {
            return "Lỗi hệ thống: Không thể hủy đơn hàng này. Vui lòng thử lại!";
        }
    }
}