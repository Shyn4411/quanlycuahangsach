package gui;

import bus.LichSuKhoBUS;
import bus.SachBUS;
import dto.LichSuKhoDTO;
import dto.SachDTO;
import enums.LoaiChungTu;
import enums.LoaiGiaoDich;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

// Nhớ import thư viện JDateChooser (Giống bên bảng Thống Kê)
import com.toedter.calendar.JDateChooser;

public class LichSuKhoGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    private JTable tblLichSu;
    private DefaultTableModel modelLichSu;

    // Các UI Bộ Lọc (Đã xóa txtTimTenSach)
    private JTextField txtTimMaCT;
    private JComboBox<String> cbxLoaiGD, cbxLoaiCT;
    private JDateChooser txtTuNgay, txtDenNgay;
    private JButton btnLoc, btnLamMoi;

    private LichSuKhoBUS lsBUS = new LichSuKhoBUS();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public LichSuKhoGUI() {
        initUI();
        loadData();
        initEvents();
    }

    // ==========================================
    // BỘ DỊCH THUẬT (TRANSLATOR) CHO ENUM
    // ==========================================
    private String parseLoaiGiaoDich(LoaiGiaoDich lgd) {
        if (lgd == null) return "";
        switch (lgd) {
            case NHAP_HANG: return "Nhập Hàng";
            case BAN_HANG: return "Bán Hàng";
            case KHACH_TRA: return "Khách Trả";
            case TRA_NCC: return "Trả NCC";
            case KIEM_KE: return "Kiểm Kê Kho";
            case HUY_BAN_HANG: return "Hủy Bán Hàng";
            default: return lgd.name();
        }
    }

    private String parseLoaiChungTu(LoaiChungTu lct) {
        if (lct == null) return "";
        switch (lct) {
            case HOADON: return "Hóa Đơn";
            case PHIEUNHAP: return "Phiếu Nhập";
            case PTKH: return "Phiếu Trả KH";
            case PTNCC: return "Phiếu Trả NCC";
            default: return lct.name();
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ==========================================
        // KHU VỰC BỘ LỌC (TOP PANEL)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);

        JPanel pnlFilterContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlFilterContainer.setBackground(Color.WHITE);
        pnlFilterContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // --- Dòng 1: Từ ngày, Đến ngày, Loại Giao Dịch ---
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row1.setBackground(Color.WHITE);

        row1.add(new JLabel("Từ ngày:"));
        txtTuNgay = new JDateChooser();
        txtTuNgay.setDateFormatString("dd/MM/yyyy");
        txtTuNgay.setPreferredSize(new Dimension(150, 30));
        row1.add(txtTuNgay);

        row1.add(new JLabel("Đến ngày:"));
        txtDenNgay = new JDateChooser();
        txtDenNgay.setDateFormatString("dd/MM/yyyy");
        txtDenNgay.setPreferredSize(new Dimension(150, 30));
        row1.add(txtDenNgay);

        row1.add(new JLabel("Loại Giao Dịch:"));
        cbxLoaiGD = new JComboBox<>(new String[]{"Tất cả", "Nhập Hàng", "Bán Hàng", "Khách Trả", "Trả NCC", "Kiểm Kê Kho", "Hủy Bán Hàng"});
        cbxLoaiGD.setPreferredSize(new Dimension(150, 30));
        row1.add(cbxLoaiGD);

        // --- Dòng 2: Loại CT, Mã CT, Các nút hành động ---
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row2.setBackground(Color.WHITE);

        row2.add(new JLabel("Loại Chứng Từ:"));
        cbxLoaiCT = new JComboBox<>(new String[]{"Tất cả", "Hóa Đơn", "Phiếu Nhập", "Phiếu Trả KH", "Phiếu Trả NCC"});
        cbxLoaiCT.setPreferredSize(new Dimension(130, 30));
        row2.add(cbxLoaiCT);

        row2.add(new JLabel("Mã Chứng Từ:"));
        txtTimMaCT = new JTextField(10);
        txtTimMaCT.setPreferredSize(new Dimension(120, 30));
        row2.add(txtTimMaCT);

        // Đẩy 2 nút ra góc phải
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setBackground(Color.WHITE);
        btnLoc = new JButton("Lọc Dữ Liệu");
        styleButton(btnLoc, COL_SIDEBAR);

        btnLamMoi = new JButton("Làm Mới");
        styleButton(btnLamMoi, new Color(46, 204, 113));

        pnlButtons.add(btnLoc);
        pnlButtons.add(btnLamMoi);
        row2.add(pnlButtons);

        pnlFilterContainer.add(row1);
        pnlFilterContainer.add(row2);

        pnlTop.add(pnlFilterContainer, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // ==========================================
        // KHU VỰC BẢNG
        // ==========================================
        String[] columns = {"Mã LS", "Ngày Giờ", "Giao Dịch", "Chứng Từ", "Mã Số CT", "Thay Đổi", "Ghi Chú", "Chi Tiết"};
        modelLichSu = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblLichSu = new JTable(modelLichSu);
        styleTable(tblLichSu);

        add(new JScrollPane(tblLichSu), BorderLayout.CENTER);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 6) { // Trừ Ghi chú (Index 6) để canh trái cho dễ đọc
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // TÔ MÀU CỘT THAY ĐỔI
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    try {
                        int val = Integer.parseInt(v.toString());
                        if (val > 0) {
                            comp.setForeground(new Color(46, 204, 113)); // Xanh lá
                            setText("+" + val);
                        } else {
                            comp.setForeground(new Color(231, 76, 60)); // Đỏ
                        }
                        setFont(getFont().deriveFont(Font.BOLD));
                    } catch (Exception e) {}
                }
                if (isS) comp.setForeground(t.getSelectionForeground());
                return comp;
            }
        });

        // TÔ MÀU NÚT "XEM CHI TIẾT"
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(new Color(41, 128, 185)); // Xanh dương
                setFont(getFont().deriveFont(Font.BOLD));
                if (isS) comp.setForeground(t.getSelectionForeground());
                return comp;
            }
        });
    }

    private void initEvents() {
        btnLoc.addActionListener(e -> applyFilters());

        btnLamMoi.addActionListener(e -> {
            // 1. Reset toàn bộ UI bộ lọc
            txtTimMaCT.setText("");
            txtTuNgay.setDate(null);
            txtDenNgay.setDate(null);
            cbxLoaiGD.setSelectedIndex(0); // Trả về "Tất cả"
            cbxLoaiCT.setSelectedIndex(0); // Trả về "Tất cả"

            tblLichSu.setModel(modelLichSu); // Phải set lại model gốc trước
            loadData();

            styleTable(tblLichSu);
        });

        // SỰ KIỆN CLICK "XEM CHI TIẾT"
        tblLichSu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblLichSu.getSelectedRow();
                int col = tblLichSu.getSelectedColumn();

                if (row >= 0 && col == 7) {
                    String loaiChungTu = tblLichSu.getValueAt(row, 3).toString();
                    String maSoStr = tblLichSu.getValueAt(row, 4).toString();

                    Window owner = SwingUtilities.getWindowAncestor(LichSuKhoGUI.this);

                    if (loaiChungTu.equalsIgnoreCase("Hóa Đơn")) {
                        ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog((Frame) owner, maSoStr);
                        dialog.setVisible(true);
                    }
                    else if (loaiChungTu.equalsIgnoreCase("Phiếu Nhập")) {
                        bus.PhieuNhapBUS pnBUS = new bus.PhieuNhapBUS();
                        String soChungTu = maSoStr.replaceAll("[^0-9]", "");
                        if(!soChungTu.isEmpty()) {
                            dto.PhieuNhapDTO pnDTO = pnBUS.getById(Integer.parseInt(soChungTu));
                            if (pnDTO != null) {
                                ChiTietPhieuNhapDialog dialog = new ChiTietPhieuNhapDialog(owner, pnDTO);
                                dialog.setVisible(true);
                            }
                        }
                    }
                }
            }
        });
    }

    public void loadData() {
        modelLichSu.setRowCount(0);
        List<LichSuKhoDTO> list = lsBUS.getAll();
        if (list != null) {
            for (LichSuKhoDTO ls : list) {
                addDtoToTable(modelLichSu, ls);
            }
        }
    }

    // ==========================================
    // LOGIC BỘ LỌC SIÊU CẤP (Bản Đã Xóa Tên Sách)
    // ==========================================
    private void applyFilters() {
        String searchMaCT = txtTimMaCT.getText().trim().toLowerCase();
        String filterGD = cbxLoaiGD.getSelectedItem().toString().trim().toLowerCase();
        String filterCT = cbxLoaiCT.getSelectedItem().toString().trim().toLowerCase();

        Date tuNgay = txtTuNgay.getDate();
        Date denNgay = txtDenNgay.getDate();

        LocalDate fromDate = (tuNgay != null) ? tuNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate toDate = (denNgay != null) ? denNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        DefaultTableModel filteredModel = new DefaultTableModel(
                new String[]{"Mã LS", "Ngày Giờ", "Giao Dịch", "Chứng Từ", "Mã Số CT", "Thay Đổi", "Ghi Chú", "Chi Tiết"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        List<LichSuKhoDTO> list = lsBUS.getAll();
        if (list != null) {
            for (LichSuKhoDTO ls : list) {

                // 1. Lọc Ngày Tháng
                LocalDate logDate = ls.getNgayGioTao() != null ? ls.getNgayGioTao().toLocalDate() : null;
                if (fromDate != null && logDate != null && logDate.isBefore(fromDate)) continue;
                if (toDate != null && logDate != null && logDate.isAfter(toDate)) continue;

                // 2. Lọc Loại Giao Dịch
                String strGD = parseLoaiGiaoDich(ls.getLoaiGiaoDich()).toLowerCase();
                if (!filterGD.equals("tất cả") && !strGD.equals(filterGD)) continue;

                // 3. Lọc Loại Chứng Từ
                String strCT = parseLoaiChungTu(ls.getLoaiChungTu()).toLowerCase();
                if (!filterCT.equals("tất cả") && !strCT.equals(filterCT)) continue;

                // 4. Lọc Mã Chứng Từ
                if (!searchMaCT.isEmpty()) {
                    String formattedMaCT = "";
                    switch (ls.getLoaiChungTu()) {
                        case HOADON: formattedMaCT = "hd" + String.format("%03d", ls.getMaChungTu()); break;
                        case PHIEUNHAP: formattedMaCT = "pn" + String.format("%03d", ls.getMaChungTu()); break;
                        case PTKH: formattedMaCT = "ptkh" + String.format("%03d", ls.getMaChungTu()); break;
                        case PTNCC: formattedMaCT = "ptncc" + String.format("%03d", ls.getMaChungTu()); break;
                        default: formattedMaCT = String.valueOf(ls.getMaChungTu());
                    }
                    if (!formattedMaCT.contains(searchMaCT)) continue;
                }

                // Không còn bước 5 (Lọc Tên Sách) nữa

                addDtoToTable(filteredModel, ls);
            }
        }

        tblLichSu.setModel(filteredModel);
        styleTable(tblLichSu);
    }

    // ==========================================
    // HÀM FORMAT DỮ LIỆU ĐỂ ĐẨY LÊN BẢNG
    // ==========================================
    private void addDtoToTable(DefaultTableModel model, LichSuKhoDTO ls) {
        String formattedMaLS = "LS" + String.format("%03d", ls.getMaLichSu());

        String formattedMaCT = "";
        switch (ls.getLoaiChungTu()) {
            case HOADON:
                formattedMaCT = "HD" + String.format("%03d", ls.getMaChungTu()); break;
            case PHIEUNHAP:
                formattedMaCT = "PN" + String.format("%03d", ls.getMaChungTu()); break;
            case PTKH:
                formattedMaCT = "PTKH" + String.format("%03d", ls.getMaChungTu()); break;
            case PTNCC:
                formattedMaCT = "PTNCC" + String.format("%03d", ls.getMaChungTu()); break;
            default:
                formattedMaCT = String.valueOf(ls.getMaChungTu());
        }

        model.addRow(new Object[]{
                formattedMaLS,
                ls.getNgayGioTao() != null ? dtf.format(ls.getNgayGioTao()) : "",
                parseLoaiGiaoDich(ls.getLoaiGiaoDich()),
                parseLoaiChungTu(ls.getLoaiChungTu()),
                formattedMaCT,
                ls.getSoLuongThayDoi(),
                ls.getGhiChu(),
                "Xem"
        });
    }
}