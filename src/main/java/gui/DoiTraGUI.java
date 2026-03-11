package gui;

import bus.HoaDonBUS;
import bus.PhieuTraKhachHangBUS;
import dto.ChiTietHoaDonDTO;
import dto.ChiTietTraKhachHangDTO;
import dto.HoaDonDTO;
import dto.PhieuTraKhachHangDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiGiaoDich;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class DoiTraGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private PhieuTraKhachHangBUS phieuTraBUS = new PhieuTraKhachHangBUS();

    // Dữ liệu tạm Tab 1
    private HoaDonDTO currentHoaDon = null;
    private List<ChiTietHoaDonDTO> listChiTietHD = new ArrayList<>();
    private List<ChiTietTraKhachHangDTO> dsTraHang = new ArrayList<>();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // UI Components Tab 1 (TẠO ĐỔI TRẢ)
    private JTextField txtSearchHD, txtLyDo;
    private JTable tblHoaDonCu, tblTraHang, tblDanhSachHD;
    private DefaultTableModel modelHDCu, modelTraHang, modelDanhSachHD;
    private TableRowSorter<DefaultTableModel> sorterDSHD;
    private JLabel lblTongTienHoan, lblThongTinHD;
    private JButton btnXacNhan, btnXoaMon;

    // UI Components Tab 2 (LỊCH SỬ)
    private JTable tblLichSu;
    private DefaultTableModel modelLichSu;
    private JTextField txtSearchLichSu;
    private TableRowSorter<DefaultTableModel> sorterLichSu;
    private JButton btnLamMoiLS;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    public DoiTraGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDanhSachHoaDon();
        loadLichSuTraHang(); // Tải data lịch sử
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(COL_BG_MAIN);

        // NÂNG CẤP: Tạo JTabbedPane chứa 2 giao diện
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // Thêm 2 Tabs
        tabbedPane.addTab("  TẠO PHIẾU ĐỔI TRẢ  ", createTabTaoDoiTra());
        tabbedPane.addTab("  LỊCH SỬ TRẢ HÀNG  ", createTabLichSu());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================
    // GIAO DIỆN TAB 1: TẠO PHIẾU ĐỔI TRẢ (Giữ nguyên cấu trúc cũ)
    // =========================================================
    private JPanel createTabTaoDoiTra() {
        JPanel pnlMain = new JPanel(new BorderLayout(15, 15));
        pnlMain.setBackground(COL_BG_MAIN);
        pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));

        // TÁCH TRÁI - PHẢI
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new Dimension(550, 0));

        JPanel pnlTopLeft = new JPanel(new BorderLayout());
        pnlTopLeft.setBackground(Color.WHITE);
        pnlTopLeft.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "CHỌN HÓA ĐƠN CẦN ĐỔI TRẢ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), COL_SIDEBAR));
        pnlTopLeft.setPreferredSize(new Dimension(0, 250));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.add(new JLabel("Tìm Mã HĐ:"));

        txtSearchHD = new JTextField(15);
        txtSearchHD.setPreferredSize(new Dimension(200, 30));
        pnlSearch.add(txtSearchHD);
        pnlTopLeft.add(pnlSearch, BorderLayout.NORTH);

        String[] colsDSHD = {"Mã HĐ", "Ngày Bán", "Tổng Tiền", "Trạng Thái"};
        modelDanhSachHD = new DefaultTableModel(colsDSHD, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDanhSachHD = new JTable(modelDanhSachHD);
        styleTable(tblDanhSachHD);
        sorterDSHD = new TableRowSorter<>(modelDanhSachHD);
        tblDanhSachHD.setRowSorter(sorterDSHD);
        pnlTopLeft.add(new JScrollPane(tblDanhSachHD), BorderLayout.CENTER);

        JLabel lblHintTop = new JLabel("<html><i>* Nhấp đúp (Double-click) vào hóa đơn để xem chi tiết bên dưới</i></html>");
        lblHintTop.setForeground(Color.RED);
        pnlTopLeft.add(lblHintTop, BorderLayout.SOUTH);

        pnlLeft.add(pnlTopLeft, BorderLayout.NORTH);

        JPanel pnlTableLeft = new JPanel(new BorderLayout());
        pnlTableLeft.setBackground(Color.WHITE);
        pnlTableLeft.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "SẢN PHẨM TRONG HÓA ĐƠN ĐÃ CHỌN",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), COL_SIDEBAR));

        lblThongTinHD = new JLabel("Chưa chọn hóa đơn nào");
        lblThongTinHD.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblThongTinHD.setBorder(new EmptyBorder(5, 10, 10, 10));
        pnlTableLeft.add(lblThongTinHD, BorderLayout.NORTH);

        String[] colsHD = {"Mã Sách", "Tên Sách", "Giá Mua", "SL Đã Mua"};
        modelHDCu = new DefaultTableModel(colsHD, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHoaDonCu = new JTable(modelHDCu);
        styleTable(tblHoaDonCu);
        pnlTableLeft.add(new JScrollPane(tblHoaDonCu), BorderLayout.CENTER);

        JLabel lblHint = new JLabel("<html><i>* Nhấp đúp (Double-click) vào sản phẩm để đưa vào danh sách trả</i></html>");
        lblHint.setForeground(Color.RED);
        pnlTableLeft.add(lblHint, BorderLayout.SOUTH);

        pnlLeft.add(pnlTableLeft, BorderLayout.CENTER);

        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setOpaque(false);

        JPanel pnlTableRight = new JPanel(new BorderLayout());
        pnlTableRight.setBackground(Color.WHITE);
        pnlTableRight.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "DANH SÁCH SÁCH TRẢ LẠI",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), Color.RED));

        String[] colsTra = {"Mã Sách", "Sách", "SL Trả", "Tình Trạng", "Tiền Hoàn"};
        modelTraHang = new DefaultTableModel(colsTra, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTraHang = new JTable(modelTraHang);
        styleTable(tblTraHang);

        tblTraHang.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblTraHang.getColumnModel().getColumn(1).setPreferredWidth(180);
        pnlTableRight.add(new JScrollPane(tblTraHang), BorderLayout.CENTER);

        btnXoaMon = new JButton("Xóa món trả");
        btnXoaMon.setForeground(Color.RED);
        btnXoaMon.setContentAreaFilled(false);
        btnXoaMon.setBorderPainted(false);
        btnXoaMon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlTableRight.add(btnXoaMon, BorderLayout.SOUTH);

        pnlRight.add(pnlTableRight, BorderLayout.CENTER);

        JPanel pnlCheckout = new JPanel(new BorderLayout(10, 15));
        pnlCheckout.setBackground(Color.WHITE);
        pnlCheckout.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(15, 20, 15, 20)));

        JPanel pnlLyDo = new JPanel(new BorderLayout(10, 0));
        pnlLyDo.setOpaque(false);
        pnlLyDo.add(new JLabel("Lý do trả:"), BorderLayout.WEST);
        txtLyDo = new JTextField("Khách không ưng ý");
        pnlLyDo.add(txtLyDo, BorderLayout.CENTER);
        pnlCheckout.add(pnlLyDo, BorderLayout.NORTH);

        JPanel pnlTien = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnlTien.setOpaque(false);
        lblTongTienHoan = new JLabel("CẦN HOÀN TRẢ: 0 VNĐ");
        lblTongTienHoan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongTienHoan.setForeground(COL_PRIMARY);
        pnlTien.add(lblTongTienHoan);
        pnlCheckout.add(pnlTien, BorderLayout.CENTER);

        btnXacNhan = new JButton("XÁC NHẬN TRẢ HÀNG & HOÀN TIỀN");
        btnXacNhan.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnXacNhan.setPreferredSize(new Dimension(0, 45));
        btnXacNhan.setBackground(COL_PRIMARY);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlCheckout.add(btnXacNhan, BorderLayout.SOUTH);

        pnlRight.add(pnlCheckout, BorderLayout.SOUTH);

        pnlMain.add(pnlLeft, BorderLayout.WEST);
        pnlMain.add(pnlRight, BorderLayout.CENTER);

        initEventsTab1(); // Cài đặt sự kiện cho Tab 1

        return pnlMain;
    }

    // =========================================================
    // GIAO DIỆN TAB 2: LỊCH SỬ TRẢ HÀNG
    // =========================================================
    private JPanel createTabLichSu() {
        JPanel pnlMain = new JPanel(new BorderLayout(15, 15));
        pnlMain.setBackground(COL_BG_MAIN);
        pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Khung lọc tìm kiếm trên cùng
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        pnlTop.add(new JLabel("Tìm Mã Phiếu/Mã HĐ/Mã NV:"));
        txtSearchLichSu = new JTextField(20);
        txtSearchLichSu.setPreferredSize(new Dimension(200, 32));
        pnlTop.add(txtSearchLichSu);

        btnLamMoiLS = new JButton("Làm Mới");
        btnLamMoiLS.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnLamMoiLS.setBackground(COL_SIDEBAR);
        btnLamMoiLS.setForeground(Color.WHITE);
        btnLamMoiLS.setPreferredSize(new Dimension(100, 32));
        btnLamMoiLS.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlTop.add(btnLamMoiLS);

        pnlMain.add(pnlTop, BorderLayout.NORTH);

        // Bảng lịch sử
        String[] colsLS = {"Mã Phiếu Trả", "Mã HĐ Gốc", "Nhân Viên XL", "Lý Do Trả", "Ngày Xử Lý", "Tiền Hoàn Lại"};
        modelLichSu = new DefaultTableModel(colsLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSu = new JTable(modelLichSu);
        styleTable(tblLichSu);

        sorterLichSu = new TableRowSorter<>(modelLichSu);
        tblLichSu.setRowSorter(sorterLichSu);

        pnlMain.add(new JScrollPane(tblLichSu), BorderLayout.CENTER);

        initEventsTab2(); // Cài đặt sự kiện cho Tab 2

        return pnlMain;
    }

    // =========================================================
    // CÁC HÀM XỬ LÝ DỮ LIỆU & SỰ KIỆN TAB 1
    // =========================================================
    private void loadDanhSachHoaDon() {
        modelDanhSachHD.setRowCount(0);
        List<HoaDonDTO> list = hoaDonBUS.getAll();
        if (list != null) {
            for (HoaDonDTO hd : list) {
                if (hd.getTrangThai() == TrangThaiGiaoDich.HOAN_THANH) {
                    modelDanhSachHD.addRow(new Object[]{
                            "HD" + String.format("%03d", hd.getMaHD()),
                            hd.getNgayTao() != null ? hd.getNgayTao().format(dtf) : "N/A",
                            String.format("%,.0f", hd.getThanhTien()),
                            "HOÀN THÀNH"
                    });
                }
            }
        }
    }

    private void initEventsTab1() {
        txtSearchHD.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterLiveHD(); }
            public void removeUpdate(DocumentEvent e) { filterLiveHD(); }
            public void changedUpdate(DocumentEvent e) { filterLiveHD(); }

            private void filterLiveHD() {
                String text = txtSearchHD.getText().trim();
                if (text.length() == 0) {
                    sorterDSHD.setRowFilter(null);
                } else {
                    sorterDSHD.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
                }
            }
        });

        tblDanhSachHD.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblDanhSachHD.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = tblDanhSachHD.convertRowIndexToModel(row);
                        String maHDStr = modelDanhSachHD.getValueAt(modelRow, 0).toString();
                        int maHD = Integer.parseInt(maHDStr.replace("HD", ""));
                        loadChiTietHoaDon(maHD);
                    }
                }
            }
        });

        tblHoaDonCu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) chonSachDeTra();
            }
        });

        btnXoaMon.addActionListener(e -> {
            int r = tblTraHang.getSelectedRow();
            if(r >= 0) {
                dsTraHang.remove(r);
                capNhatBangTraHang();
            }
        });

        btnXacNhan.addActionListener(e -> xuLyHoanTien());
    }

    // Các logic thêm sửa xóa giỏ hàng giữ nguyên...
    private void loadChiTietHoaDon(int maHD) {
        currentHoaDon = hoaDonBUS.getHoaDonById(maHD);

        if (currentHoaDon == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu hóa đơn!");
            return;
        }

        if (currentHoaDon.getTrangThai() == TrangThaiGiaoDich.DA_HUY) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã bị hủy, không thể đổi trả!");
            return;
        }

        lblThongTinHD.setText("Đang xử lý HĐ: HD" + String.format("%03d", maHD) + " - Tổng tiền gốc: " + String.format("%,.0f", currentHoaDon.getThanhTien()) + " VNĐ");

        listChiTietHD = hoaDonBUS.getChiTietByMaHD(maHD);
        modelHDCu.setRowCount(0);

        bus.SachBUS sachBUS = new bus.SachBUS();

        if (listChiTietHD != null && !listChiTietHD.isEmpty()) {
            for (ChiTietHoaDonDTO ct : listChiTietHD) {
                String tenSach = ct.getTenSach();
                if (tenSach == null || tenSach.isEmpty()) {
                    dto.SachDTO s = sachBUS.getById(ct.getMaSach());
                    tenSach = (s != null) ? s.getTenSach() : "Sách ẩn/ID: " + ct.getMaSach();
                    ct.setTenSach(tenSach);
                }

                modelHDCu.addRow(new Object[]{
                        "S" + String.format("%03d", ct.getMaSach()),
                        tenSach,
                        String.format("%,.0f", ct.getDonGia()),
                        ct.getSoLuong()
                });
            }
        }
        dsTraHang.clear();
        capNhatBangTraHang();
    }

    private void chonSachDeTra() {
        int row = tblHoaDonCu.getSelectedRow();
        if(row < 0) return;

        ChiTietHoaDonDTO ctGoc = listChiTietHD.get(row);

        String inputSL = JOptionPane.showInputDialog(this,
                "Nhập số lượng muốn trả (Tối đa " + ctGoc.getSoLuong() + "):", "1");
        if (inputSL == null || inputSL.trim().isEmpty()) return;

        try {
            int slTra = Integer.parseInt(inputSL);
            if (slTra <= 0 || slTra > ctGoc.getSoLuong()) {
                JOptionPane.showMessageDialog(this, "Số lượng trả không hợp lệ!");
                return;
            }

            String[] options = {"Lỗi NSX", "Nguyên vẹn (Khách đổi ý)"};
            String tinhTrang = (String) JOptionPane.showInputDialog(this,
                    "Tình trạng sách trả lại:", "Đánh giá tình trạng",
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (tinhTrang == null) return;

            boolean daCo = false;
            for(ChiTietTraKhachHangDTO ctTra : dsTraHang) {
                if(ctTra.getMaSach() == ctGoc.getMaSach() && ctTra.getTinhTrangSach().equals(tinhTrang)) {
                    if(ctTra.getSoLuong() + slTra > ctGoc.getSoLuong()) {
                        JOptionPane.showMessageDialog(this, "Tổng số lượng trả vượt quá số lượng đã mua!");
                        return;
                    }
                    ctTra.setSoLuong(ctTra.getSoLuong() + slTra);
                    ctTra.setThanhTienHoan(ctGoc.getDonGia().multiply(new BigDecimal(ctTra.getSoLuong())));
                    daCo = true;
                    break;
                }
            }

            if(!daCo) {
                ChiTietTraKhachHangDTO traMoi = new ChiTietTraKhachHangDTO();
                traMoi.setMaSach(ctGoc.getMaSach());
                traMoi.setTenSach(ctGoc.getTenSach() != null ? ctGoc.getTenSach() : "Sách ID: " + ctGoc.getMaSach());
                traMoi.setSoLuong(slTra);
                traMoi.setTinhTrangSach(tinhTrang);
                traMoi.setDonGia(ctGoc.getDonGia());
                traMoi.setThanhTienHoan(ctGoc.getDonGia().multiply(new BigDecimal(slTra)));
                dsTraHang.add(traMoi);
            }

            capNhatBangTraHang();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        }
    }

    private void capNhatBangTraHang() {
        modelTraHang.setRowCount(0);
        BigDecimal tongHoan = BigDecimal.ZERO;

        for (ChiTietTraKhachHangDTO ct : dsTraHang) {
            modelTraHang.addRow(new Object[]{
                    "S" + String.format("%03d", ct.getMaSach()),
                    ct.getTenSach(),
                    ct.getSoLuong(),
                    ct.getTinhTrangSach(),
                    String.format("%,.0f", ct.getThanhTienHoan())
            });
            tongHoan = tongHoan.add(ct.getThanhTienHoan());
        }
        lblTongTienHoan.setText("CẦN HOÀN TRẢ: " + String.format("%,.0f", tongHoan) + " VNĐ");
    }

    private void xuLyHoanTien() {
        if (currentHoaDon == null || dsTraHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào để trả!");
            return;
        }

        BigDecimal tongHoan = BigDecimal.ZERO;
        for(ChiTietTraKhachHangDTO ct : dsTraHang) tongHoan = tongHoan.add(ct.getThanhTienHoan());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận lập phiếu trả hàng và hoàn lại " + String.format("%,.0f", tongHoan) + " VNĐ?",
                "Chốt Đổi Trả", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            PhieuTraKhachHangDTO ptk = new PhieuTraKhachHangDTO();
            ptk.setMaHD(currentHoaDon.getMaHD());
            ptk.setMaNV(currentUser.getMaNhanVien() != null ? currentUser.getMaNhanVien() : 1);
            ptk.setLyDo(txtLyDo.getText());
            ptk.setTienHoan(tongHoan);

            String ketQua = phieuTraBUS.addPhieuTraKhachHang(ptk, dsTraHang);
            JOptionPane.showMessageDialog(this, ketQua);

            if (ketQua.contains("Thành công")) {
                int inPhieu = JOptionPane.showConfirmDialog(this, "Bạn có muốn in biên lai hoàn tiền không?", "In Biên Lai", JOptionPane.YES_NO_OPTION);
                if (inPhieu == JOptionPane.YES_OPTION) {
                    xuatPhieuTraPDF(ptk, dsTraHang);
                }

                // Reset Tab 1
                dsTraHang.clear();
                capNhatBangTraHang();
                modelHDCu.setRowCount(0);
                txtSearchHD.setText("");
                lblThongTinHD.setText("Chưa chọn hóa đơn nào");
                currentHoaDon = null;

                // NÂNG CẤP: GỌI HÀM NÀY ĐỂ BẢNG Ở TAB 2 TỰ ĐỘNG CẬP NHẬT PHIẾU VỪA TẠO
                loadLichSuTraHang();
            }
        }
    }

    // =========================================================
    // CÁC HÀM XỬ LÝ DỮ LIỆU & SỰ KIỆN TAB 2
    // =========================================================
    private void loadLichSuTraHang() {
        modelLichSu.setRowCount(0);
        // Tủn nhớ đảm bảo trong PhieuTraKhachHangBUS có hàm getAll() nhé
        List<PhieuTraKhachHangDTO> list = phieuTraBUS.getAll();

        if (list != null) {
            for (PhieuTraKhachHangDTO pt : list) {
                modelLichSu.addRow(new Object[]{
                        "PT" + String.format("%03d", pt.getMaPTK()),
                        "HD" + String.format("%03d", pt.getMaHD()),
                        "NV" + String.format("%02d", pt.getMaNV()),
                        pt.getLyDo(),
                        pt.getNgayTao() != null ? pt.getNgayTao().format(dtf) : "N/A",
                        String.format("%,.0f VNĐ", pt.getTienHoan())
                });
            }
        }
    }

    private void initEventsTab2() {
        // Sự kiện Live Search cho Lịch Sử
        txtSearchLichSu.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterLS(); }
            public void removeUpdate(DocumentEvent e) { filterLS(); }
            public void changedUpdate(DocumentEvent e) { filterLS(); }

            private void filterLS() {
                String text = txtSearchLichSu.getText().trim();
                if (text.length() == 0) {
                    sorterLichSu.setRowFilter(null);
                } else {
                    // Lọc theo PT, HD hoặc NV (Cột 0, 1, 2)
                    sorterLichSu.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1, 2));
                }
            }
        });

        btnLamMoiLS.addActionListener(e -> {
            txtSearchLichSu.setText("");
            loadLichSuTraHang();
        });
    }

    // =========================================================
    // TIỆN ÍCH GIAO DIỆN & PDF
    // =========================================================
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 35));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    String status = v.toString();
                    if (status.equalsIgnoreCase("HOÀN THÀNH") || status.equalsIgnoreCase("Nguyên vẹn (Khách đổi ý)")) {
                        comp.setForeground(new Color(46, 204, 113));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                    else if (status.equalsIgnoreCase("Lỗi NSX") || status.equalsIgnoreCase("ĐÃ HỦY")) {
                        comp.setForeground(new Color(231, 76, 60));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                    else {
                        comp.setForeground(Color.BLACK);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                if (isS) comp.setForeground(t.getSelectionForeground());
                return comp;
            }
        };

        for(int i = 0; i < table.getColumnCount(); i++) {
            // Cột 3 ở bảng Hóa Đơn và Bảng Trả Hàng (Tạo) là trạng thái
            if (i == 3 && (table == tblDanhSachHD || table == tblTraHang)) {
                table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
            }
            else if (i != 1 && i != 0 && i != 3) { // Né cột Tên, Lý Do ra
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private void xuatPhieuTraPDF(PhieuTraKhachHangDTO ptk, List<ChiTietTraKhachHangDTO> dsTra) {
        try {
            String path = "PhieuTraHang_HD" + ptk.getMaHD() + "_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(bf, 18, com.itextpdf.text.Font.BOLD, BaseColor.RED);
            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);

            Paragraph title = new Paragraph("BIÊN LAI ĐỔI TRẢ & HOÀN TIỀN", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Hóa đơn gốc: HD" + String.format("%03d", ptk.getMaHD()), fontNormal));
            document.add(new Paragraph("Ngày xử lý: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontNormal));
            document.add(new Paragraph("Lý do trả: " + ptk.getLyDo(), fontNormal));
            document.add(new Paragraph("---------------------------------------------------------", fontNormal));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(5);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1.5f, 4f, 1.2f, 2.5f, 2.5f});

            String[] headers = {"Mã Sách", "Tên Sách", "SL Trả", "Tình Trạng", "Tiền Hoàn"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            for (ChiTietTraKhachHangDTO ct : dsTra) {
                PdfPCell cellMa = new PdfPCell(new Phrase("S" + String.format("%03d", ct.getMaSach()), fontNormal));
                cellMa.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cellMa);

                pdfTable.addCell(new PdfPCell(new Phrase(ct.getTenSach(), fontNormal)));

                PdfPCell cellSL = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                cellSL.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cellSL);

                PdfPCell cellTinhTrang = new PdfPCell(new Phrase(ct.getTinhTrangSach(), fontNormal));
                cellTinhTrang.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cellTinhTrang);

                PdfPCell cellTien = new PdfPCell(new Phrase(String.format("%,.0f", ct.getThanhTienHoan()), fontNormal));
                cellTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfTable.addCell(cellTien);
            }
            document.add(pdfTable);

            document.add(new Paragraph(" "));

            Paragraph tongTienPara = new Paragraph("TỔNG TIỀN HOÀN: " + String.format("%,.0f VNĐ", ptk.getTienHoan()), new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD, BaseColor.RED));
            tongTienPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(tongTienPara);

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Đã nhận lại hàng và hoàn đủ tiền cho khách.", new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.ITALIC));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            java.awt.Desktop.getDesktop().open(new java.io.File(path));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xuất biên lai PDF: " + ex.getMessage());
        }
    }
}