package bus;

import dao.ChiTietPhieuNhapDAO;
import dao.LichSuKhoDAO;
import dao.PhieuNhapDAO;
import dao.SachDAO;
import dto.ChiTietPhieuNhapDTO;
import dto.LichSuKhoDTO;
import dto.PhieuNhapDTO;
import enums.LoaiChungTu;
import enums.LoaiGiaoDich;
import enums.TrangThaiGiaoDich;

import java.util.List;

public class PhieuNhapBUS {

    private PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private ChiTietPhieuNhapDAO chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAO();
    private SachDAO sachDAO = new SachDAO();
    private LichSuKhoDAO lichSuKhoDAO = new LichSuKhoDAO();


    public String addPhieuNhap(PhieuNhapDTO phieuNhap, List<ChiTietPhieuNhapDTO> dsChiTiet) {
        if (phieuNhap.getMaNV() <= 0) return "Lỗi: Nhân viên không hợp lệ!";
        if (dsChiTiet == null || dsChiTiet.isEmpty()) return "Lỗi: Phiếu nhập trống!";

        int maPhieuNhap = phieuNhapDAO.insert(phieuNhap);

        if (maPhieuNhap > 0) {
            for (ChiTietPhieuNhapDTO ct : dsChiTiet) {
                ct.setMaPN(maPhieuNhap);
                chiTietPhieuNhapDAO.insert(ct);
            }
            return "Thành công: Đã tạo phiếu nhập (Trạng thái: Chờ xử lý)!";
        }
        return "Lỗi: Không thể tạo phiếu nhập!";
    }


    public String hoanThanhPhieuNhap(int maPhieuNhap) {
        if (phieuNhapDAO.updateTrangThai(maPhieuNhap, TrangThaiGiaoDich.HOAN_THANH.name())) {

            List<ChiTietPhieuNhapDTO> dsChiTiet = chiTietPhieuNhapDAO.getByMaPN(maPhieuNhap);

            for (ChiTietPhieuNhapDTO ct : dsChiTiet) {
                sachDAO.congTonKho(ct.getMaSach(), ct.getSoLuong());

                LichSuKhoDTO lichSu = new LichSuKhoDTO();
                lichSu.setMaSach(ct.getMaSach());
                lichSu.setLoaiGiaoDich(LoaiGiaoDich.NHAP_HANG);
                lichSu.setLoaiChungTu(LoaiChungTu.PHIEU_NHAP);
                lichSu.setMaChungTu(maPhieuNhap);
                lichSu.setSoLuongThayDoi(ct.getSoLuong());
                lichSu.setGhiChu("Hoàn tất nhập hàng mã PN" + String.format("%03d", maPhieuNhap));

                lichSuKhoDAO.insert(lichSu);
            }
            return "Thành công: Đã nhập hàng vào kho!";
        }
        return "Lỗi: Không thể hoàn tất phiếu nhập!";
    }

    public String huyPhieuNhap(int maPhieuNhap, String trangThaiHienTai) {
        if (!trangThaiHienTai.equals(TrangThaiGiaoDich.CHO_XU_LY.name())) {
            return "Lỗi: Chỉ có thể hủy phiếu nhập đang chờ xử lý!";
        }

        if (phieuNhapDAO.updateTrangThai(maPhieuNhap, TrangThaiGiaoDich.DA_HUY.name())) {
            return "Thành công: Đã hủy phiếu nhập!";
        }
        return "Lỗi: Không thể hủy phiếu nhập!";
    }
    public List<PhieuNhapDTO> getAll() {
        return phieuNhapDAO.getAll();
    }

    public List<ChiTietPhieuNhapDTO> getChiTietByMaPN(int maPN) {
        if (maPN <= 0) return null;
        return chiTietPhieuNhapDAO.getByMaPN(maPN);
    }

    public PhieuNhapDTO getById(int maPN) {
        List<PhieuNhapDTO> all = phieuNhapDAO.getAll();
        for (PhieuNhapDTO pn : all) {
            if (pn.getMaPN() == maPN) return pn;
        }
        return null;
    }
}
