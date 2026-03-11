package bus;

import dao.CauHinhDAO;
import java.util.Map;

public class CauHinhBUS {
    private CauHinhDAO dao = new CauHinhDAO();

    public Map<String, String> getAll() { return dao.getAll(); }

    public String saveConfig(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            if (entry.getValue().trim().isEmpty()) return "Lỗi: Không được để trống các trường!";
            dao.update(entry.getKey(), entry.getValue());
        }
        return "Thành công: Đã cập nhật cấu hình hệ thống!";
    }
}