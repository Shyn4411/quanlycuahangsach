package bus;

import dao.ChiTietTraKhachHangDAO;
import dao.LichSuKhoDAO;
import dao.PhieuTraKhachHangDAO;
import dao.SachDAO;
import dto.ChiTietTraKhachHangDTO;
import dto.LichSuKhoDTO;
import dto.PhieuTraKhachHangDTO;
import enums.LoaiChungTu;
import enums.LoaiGiaoDich;

import java.util.List;

public class PhieuTraKhachHangBUS {

    private PhieuTraKhachHangDAO ptkhDAO = new PhieuTraKhachHangDAO();
    private ChiTietTraKhachHangDAO ctptkhDAO = new ChiTietTraKhachHangDAO();
    private SachDAO sachDAO = new SachDAO();
    private LichSuKhoDAO lichSuKhoDAO = new LichSuKhoDAO();

    public String addPhieuTraKhachHang(PhieuTraKhachHangDTO ptk, List<ChiTietTraKhachHangDTO> dsChiTiet) {
        if (ptk.getMaHD() <= 0) return "Lỗi: Không xác định được hóa đơn gốc!";
        if (dsChiTiet == null || dsChiTiet.isEmpty()) return "Lỗi: Phải có ít nhất 1 cuốn sách được trả!";

        int maPhieuTra = ptkhDAO.insert(ptk);

        if (maPhieuTra > 0) {
            for (ChiTietTraKhachHangDTO ct : dsChiTiet) {
                ct.setMaPTK(maPhieuTra);
                ctptkhDAO.insert(ct);

                sachDAO.congTonKho(ct.getMaSach(), ct.getSoLuong());

                LichSuKhoDTO lichSu = new LichSuKhoDTO();
                lichSu.setMaSach(ct.getMaSach());
                lichSu.setLoaiGiaoDich(LoaiGiaoDich.KHACH_TRA);
                lichSu.setLoaiChungTu(LoaiChungTu.PHIEU_TRA_KHACH_HANG);
                lichSu.setMaChungTu(maPhieuTra);
                lichSu.setSoLuongThayDoi(ct.getSoLuong());
                lichSu.setGhiChu("Khách trả hàng (Mã PTK: " + maPhieuTra + ")");

                lichSuKhoDAO.insert(lichSu);
            }
            return "Thành công: Đã nhận lại sách từ khách và cộng vào kho!";
        }
        return "Lỗi: Không thể tạo phiếu trả hàng!";
    }

    public List<PhieuTraKhachHangDTO> getAll() {
        return ptkhDAO.getAll();
    }

}