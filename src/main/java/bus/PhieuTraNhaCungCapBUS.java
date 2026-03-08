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

    public String addPhieuTraNhaCungCap(PhieuTraNhaCungCapDTO ptn, List<ChiTietTraNhaCungCapDTO> dsChiTiet) {
        if (ptn.getMaNCC() <= 0) return "Lỗi: Không xác định được Nhà cung cấp!";
        if (dsChiTiet == null || dsChiTiet.isEmpty()) return "Lỗi: Phải chọn sách cần trả!";

        int maPhieuTra = ptnccDAO.insert(ptn);

        if (maPhieuTra > 0) {
            for (ChiTietTraNhaCungCapDTO ct : dsChiTiet) {
                ct.setMaPTN(maPhieuTra);
                ctptnccDAO.insert(ct);

                sachDAO.truTonKho(ct.getMaSach(), ct.getSoLuong());

                LichSuKhoDTO lichSu = new LichSuKhoDTO();
                lichSu.setMaSach(ct.getMaSach());
                lichSu.setLoaiGiaoDich(LoaiGiaoDich.TRA_NCC);
                lichSu.setLoaiChungTu(LoaiChungTu.PTNCC);
                lichSu.setMaChungTu(maPhieuTra);
                lichSu.setSoLuongThayDoi(-ct.getSoLuong());
                lichSu.setGhiChu("Trả hàng cho Nhà cung cấp (Mã PTN: " + maPhieuTra + ")");

                lichSuKhoDAO.insert(lichSu);
            }
            return "Thành công: Đã lập phiếu trả Nhà cung cấp và xuất kho sách!";
        }
        return "Lỗi: Không thể tạo phiếu trả hàng cho Nhà cung cấp!";
    }
}