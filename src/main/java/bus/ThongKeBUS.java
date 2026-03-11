package bus;

import dao.ThongKeDAO;
import java.util.ArrayList;
import java.util.List;

public class ThongKeBUS {
    private ThongKeDAO thongKeDAO = new ThongKeDAO();

    public double getTongDoanhThu(String tu, String den) {
        return validate(tu, den) ? thongKeDAO.getTongDoanhThu(tu, den) : 0;
    }

    public double getTongVon(String tu, String den) {
        return validate(tu, den) ? thongKeDAO.getTongVon(tu, den) : 0;
    }

    public double getLoiNhuan(String tu, String den) {
        return getTongDoanhThu(tu, den) - getTongVon(tu, den);
    }

    public List<Object[]> getDoanhThuTheoThang(String tu, String den) {
        return validate(tu, den) ? thongKeDAO.getDoanhThuTheoThang(tu, den) : new ArrayList<>();
    }

    public List<Object[]> getSachSapHet() {
        return thongKeDAO.getSachSapHet();
    }

    private boolean validate(String t, String d) {
        return t != null && d != null && !t.isEmpty() && !d.isEmpty();
    }
}