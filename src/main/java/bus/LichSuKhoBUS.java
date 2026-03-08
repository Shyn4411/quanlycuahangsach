package bus;

import dao.LichSuKhoDAO;
import dto.LichSuKhoDTO;
import java.util.List;

public class LichSuKhoBUS {

    private LichSuKhoDAO lichSuKhoDAO = new LichSuKhoDAO();

    public List<LichSuKhoDTO> getAll() {
        return lichSuKhoDAO.getAll();
    }

    public List<LichSuKhoDTO> getByMaSach(int maSach) {
        if (maSach <= 0) return null;
        return lichSuKhoDAO.getByMaSach(maSach);
    }

    public List<LichSuKhoDTO> getByChungTu(String loaiChungTu, int maChungTu) {
        if (loaiChungTu == null || loaiChungTu.trim().isEmpty() || maChungTu <= 0) {
            return null;
        }
        return lichSuKhoDAO.getByChungTu(loaiChungTu, maChungTu);
    }

    public List<LichSuKhoDTO> getByDateRange(String tuNgay, String denNgay) {
        return lichSuKhoDAO.getByDateRange(tuNgay, denNgay);
    }

    public boolean ghiLogKho(int maSach, int soLuong, enums.LoaiGiaoDich giaoDich, enums.LoaiChungTu chungTu, int maCT, String ghiChu) {
        LichSuKhoDTO ls = new LichSuKhoDTO();
        ls.setMaSach(maSach);
        ls.setSoLuongThayDoi(soLuong); // Ghi số âm nếu bán, số dương nếu nhập
        ls.setLoaiGiaoDich(giaoDich);
        ls.setLoaiChungTu(chungTu);
        ls.setMaChungTu(maCT);
        ls.setGhiChu(ghiChu);

        return lichSuKhoDAO.insert(ls);
    }
}