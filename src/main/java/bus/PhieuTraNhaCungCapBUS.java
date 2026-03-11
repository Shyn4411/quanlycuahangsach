package bus;

import dao.ChiTietTraNhaCungCapDAO;
import dao.LichSuKhoDAO;
import dao.PhieuTraNhaCungCapDAO;
import dao.SachDAO;
import dto.ChiTietTraNhaCungCapDTO;
import dto.LichSuKhoDTO;
import dto.PhieuTraNhaCungCapDTO;
import enums.LoaiChungTu;
import enums.LoaiGiaoDich;

import java.util.List;

public class PhieuTraNhaCungCapBUS {

    private PhieuTraNhaCungCapDAO ptnccDAO = new PhieuTraNhaCungCapDAO();
    private ChiTietTraNhaCungCapDAO ctptnccDAO = new ChiTietTraNhaCungCapDAO();
    private SachDAO sachDAO = new SachDAO();
    private LichSuKhoDAO lichSuKhoDAO = new LichSuKhoDAO();

    public List<PhieuTraNhaCungCapDTO> getAll() {
        return ptnccDAO.getAll();
    }

    public String addPhieuTraNCC(PhieuTraNhaCungCapDTO pt, List<ChiTietTraNhaCungCapDTO> dsChiTiet) {
        if (pt.getMaNCC() <= 0) return "Lỗi: Không xác định được Nhà Cung Cấp!";
        if (dsChiTiet == null || dsChiTiet.isEmpty()) return "Lỗi: Phải có ít nhất 1 cuốn sách được trả!";

        int maPhieuTra = ptnccDAO.insert(pt);

        if (maPhieuTra > 0) {
            for (ChiTietTraNhaCungCapDTO ct : dsChiTiet) {
                ct.setMaPTN(maPhieuTra);
                ctptnccDAO.insert(ct);

                sachDAO.truTonKho(ct.getMaSach(), ct.getSoLuong());
                LichSuKhoDTO lichSu = new LichSuKhoDTO();
                lichSu.setMaSach(ct.getMaSach());

                lichSu.setLoaiGiaoDich(LoaiGiaoDich.TRA_NCC);
                lichSu.setLoaiChungTu(LoaiChungTu.PHIEU_TRA_NHA_CUNG_CAP);

                lichSu.setMaChungTu(maPhieuTra);
                lichSu.setSoLuongThayDoi(-ct.getSoLuong());
                lichSu.setGhiChu("Trả hàng Nhà Cung Cấp (Mã PTN: " + maPhieuTra + ")");

                lichSuKhoDAO.insert(lichSu);
            }
            return "Thành công: Đã trả hàng cho Nhà Cung Cấp và tự động cập nhật kho!";
        }
        return "Lỗi: Hệ thống không thể tạo phiếu trả hàng NCC!";
    }
}