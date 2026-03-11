package gui;

import bus.PhieuNhapBUS;
import bus.PhieuTraNhaCungCapBUS; // Đổi lại tên BUS
import dto.ChiTietPhieuNhapDTO;
import dto.ChiTietTraNhaCungCapDTO; // Đổi lại tên DTO
import dto.PhieuNhapDTO;
import dto.PhieuTraNhaCungCapDTO; // Đổi lại tên DTO
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

public class DoiTraNhaCungCapGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
    private PhieuTraNhaCungCapBUS ptNccBUS = new PhieuTraNhaCungCapBUS();

    private PhieuNhapDTO currentPhieuNhap = null;
    private List<ChiTietPhieuNhapDTO> listChiTietPN = new ArrayList<>();
    private List<ChiTietTraNhaCungCapDTO> dsTraHang = new ArrayList<>();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTextField txtSearchPN, txtLyDo;
    private JTable tblChiTietPN, tblTraHang, tblDanhSachPN;
    private DefaultTableModel modelChiTietPN, modelTraHang, modelDanhSachPN;
    private TableRowSorter<DefaultTableModel> sorterPN;
    private JLabel lblTongTienHoan, lblThongTinPN;
    private JButton btnXacNhan, btnXoaMon;

    private JTable tblLichSu;
    private DefaultTableModel modelLichSu;
    private JTextField txtSearchLichSu;
    private TableRowSorter<DefaultTableModel> sorterLichSu;
    private JButton btnLamMoiLS;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    public DoiTraNhaCungCapGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDanhSachPhieuNhap();
        loadLichSuTraNCC();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(COL_BG_MAIN);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("  TẠO PHIẾU TRẢ NCC  ", createTabTaoPhieuTra());
        tabbedPane.addTab("  LỊCH SỬ TRẢ NCC  ", createTabLichSu());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================
    // GIAO DIỆN TAB 1: TẠO PHIẾU TRẢ NCC
    // =========================================================
    private JPanel createTabTaoPhieuTra() {
        JPanel pnlMain = new JPanel(new BorderLayout(15, 15));
        pnlMain.setBackground(COL_BG_MAIN);
        pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new Dimension(550, 0));

        JPanel pnlTopLeft = new JPanel(new BorderLayout());
        pnlTopLeft.setBackground(Color.WHITE);
        pnlTopLeft.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "CHỌN PHIẾU NHẬP CẦN TRẢ HÀNG",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), COL_SIDEBAR));
        pnlTopLeft.setPreferredSize(new Dimension(0, 250));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.add(new JLabel("Lọc Mã PN / NCC:"));

        txtSearchPN = new JTextField(15);
        txtSearchPN.setPreferredSize(new Dimension(200, 30));
        pnlSearch.add(txtSearchPN);
        pnlTopLeft.add(pnlSearch, BorderLayout.NORTH);

        String[] colsDSPN = {"Mã PN", "Nhà Cung Cấp", "Ngày Nhập", "Tổng Tiền"};
        modelDanhSachPN = new DefaultTableModel(colsDSPN, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDanhSachPN = new JTable(modelDanhSachPN);
        styleTable(tblDanhSachPN);
        sorterPN = new TableRowSorter<>(modelDanhSachPN);
        tblDanhSachPN.setRowSorter(sorterPN);
        pnlTopLeft.add(new JScrollPane(tblDanhSachPN), BorderLayout.CENTER);

        JLabel lblHintTop = new JLabel("<html><i>* Nhấp đúp (Double-click) vào phiếu nhập để xem chi tiết bên dưới</i></html>");
        lblHintTop.setForeground(Color.RED);
        pnlTopLeft.add(lblHintTop, BorderLayout.SOUTH);

        pnlLeft.add(pnlTopLeft, BorderLayout.NORTH);

        JPanel pnlTableLeft = new JPanel(new BorderLayout());
        pnlTableLeft.setBackground(Color.WHITE);
        pnlTableLeft.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "SẢN PHẨM TRONG PHIẾU NHẬP ĐÃ CHỌN",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), COL_SIDEBAR));

        lblThongTinPN = new JLabel("Chưa chọn phiếu nhập nào");
        lblThongTinPN.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblThongTinPN.setBorder(new EmptyBorder(5, 10, 10, 10));
        pnlTableLeft.add(lblThongTinPN, BorderLayout.NORTH);

        String[] colsCT = {"Mã Sách", "Tên Sách", "Giá Nhập", "SL Đã Nhập"};
        modelChiTietPN = new DefaultTableModel(colsCT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTietPN = new JTable(modelChiTietPN);
        styleTable(tblChiTietPN);
        pnlTableLeft.add(new JScrollPane(tblChiTietPN), BorderLayout.CENTER);

        JLabel lblHint = new JLabel("<html><i>* Nhấp đúp (Double-click) vào sản phẩm để đưa vào danh sách trả</i></html>");
        lblHint.setForeground(Color.RED);
        pnlTableLeft.add(lblHint, BorderLayout.SOUTH);

        pnlLeft.add(pnlTableLeft, BorderLayout.CENTER);

        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setOpaque(false);

        JPanel pnlTableRight = new JPanel(new BorderLayout());
        pnlTableRight.setBackground(Color.WHITE);
        pnlTableRight.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "DANH SÁCH SÁCH TRẢ NHÀ CUNG CẤP",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), Color.RED));

        String[] colsTra = {"Mã Sách", "Tên Sách", "SL Trả", "Giá Nhập", "Tiền Y/C Hoàn"};
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
        pnlLyDo.add(new JLabel("Lý do trả hàng:"), BorderLayout.WEST);
        txtLyDo = new JTextField("Hàng lỗi, hỏng hóc trong quá trình vận chuyển");
        pnlLyDo.add(txtLyDo, BorderLayout.CENTER);
        pnlCheckout.add(pnlLyDo, BorderLayout.NORTH);

        JPanel pnlTien = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnlTien.setOpaque(false);
        lblTongTienHoan = new JLabel("Y/C HOÀN TIỀN: 0 VNĐ");
        lblTongTienHoan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongTienHoan.setForeground(COL_PRIMARY);
        pnlTien.add(lblTongTienHoan);
        pnlCheckout.add(pnlTien, BorderLayout.CENTER);

        btnXacNhan = new JButton("XÁC NHẬN TRẢ HÀNG NCC");
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

        initEventsTab1();

        return pnlMain;
    }

    // =========================================================
    // GIAO DIỆN TAB 2: LỊCH SỬ TRẢ NHÀ CUNG CẤP
    // =========================================================
    private JPanel createTabLichSu() {
        JPanel pnlMain = new JPanel(new BorderLayout(15, 15));
        pnlMain.setBackground(COL_BG_MAIN);
        pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        pnlTop.add(new JLabel("Tìm Mã Phiếu/Mã NCC/Mã NV:"));
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

        String[] colsLS = {"Mã Phiếu Trả", "Mã NCC", "Nhân Viên XL", "Lý Do Trả", "Ngày Xử Lý", "Tiền Y/C Hoàn"};
        modelLichSu = new DefaultTableModel(colsLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSu = new JTable(modelLichSu);
        styleTable(tblLichSu);

        sorterLichSu = new TableRowSorter<>(modelLichSu);
        tblLichSu.setRowSorter(sorterLichSu);

        pnlMain.add(new JScrollPane(tblLichSu), BorderLayout.CENTER);

        initEventsTab2();

        return pnlMain;
    }

    private void loadDanhSachPhieuNhap() {
        modelDanhSachPN.setRowCount(0);
        List<PhieuNhapDTO> list = pnBUS.getAll();
        if (list != null) {
            for (PhieuNhapDTO pn : list) {
                if (pn.getTrangThai() == TrangThaiGiaoDich.HOAN_THANH) {
                    modelDanhSachPN.addRow(new Object[]{
                            "PN" + String.format("%03d", pn.getMaPN()),
                            pn.getTenNCC(),
                            pn.getNgayTao() != null ? pn.getNgayTao().format(dtf) : "N/A",
                            String.format("%,.0f", pn.getTongTien())
                    });
                }
            }
        }
    }

    private void initEventsTab1() {
        txtSearchPN.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterLivePN(); }
            public void removeUpdate(DocumentEvent e) { filterLivePN(); }
            public void changedUpdate(DocumentEvent e) { filterLivePN(); }

            private void filterLivePN() {
                String text = txtSearchPN.getText().trim();
                if (text.length() == 0) {
                    sorterPN.setRowFilter(null);
                } else {
                    sorterPN.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1));
                }
            }
        });

        tblDanhSachPN.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblDanhSachPN.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = tblDanhSachPN.convertRowIndexToModel(row);
                        String maPNStr = modelDanhSachPN.getValueAt(modelRow, 0).toString();
                        int maPN = Integer.parseInt(maPNStr.replace("PN", ""));
                        loadChiTietPhieuNhap(maPN);
                    }
                }
            }
        });

        tblChiTietPN.addMouseListener(new MouseAdapter() {
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

        btnXacNhan.addActionListener(e -> xuLyTraHangNCC());
    }

    private void loadChiTietPhieuNhap(int maPN) {
        currentPhieuNhap = pnBUS.getById(maPN);

        if (currentPhieuNhap == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu phiếu nhập!");
            return;
        }

        lblThongTinPN.setText("Đang xử lý Trả hàng cho: PN" + String.format("%03d", maPN) + " - NCC: " + currentPhieuNhap.getTenNCC());

        listChiTietPN = pnBUS.getChiTietByMaPN(maPN);
        modelChiTietPN.setRowCount(0);

        bus.SachBUS sachBUS = new bus.SachBUS();

        if (listChiTietPN != null && !listChiTietPN.isEmpty()) {
            for (ChiTietPhieuNhapDTO ct : listChiTietPN) {
                String tenSach = ct.getTenSach();
                if (tenSach == null || tenSach.isEmpty()) {
                    dto.SachDTO s = sachBUS.getById(ct.getMaSach());
                    tenSach = (s != null) ? s.getTenSach() : "Sách ID: " + ct.getMaSach();
                }

                modelChiTietPN.addRow(new Object[]{
                        "S" + String.format("%03d", ct.getMaSach()),
                        tenSach,
                        String.format("%,.0f", ct.getGiaNhap()),
                        ct.getSoLuong()
                });
            }
        }
        dsTraHang.clear();
        capNhatBangTraHang();
    }

    private void chonSachDeTra() {
        int row = tblChiTietPN.getSelectedRow();
        if(row < 0) return;

        ChiTietPhieuNhapDTO ctGoc = listChiTietPN.get(row);

        String inputSL = JOptionPane.showInputDialog(this,
                "Nhập số lượng muốn trả (Đã nhập " + ctGoc.getSoLuong() + "):", "1");
        if (inputSL == null || inputSL.trim().isEmpty()) return;

        try {
            int slTra = Integer.parseInt(inputSL);
            if (slTra <= 0 || slTra > ctGoc.getSoLuong()) {
                JOptionPane.showMessageDialog(this, "Số lượng trả không hợp lệ!");
                return;
            }

            boolean daCo = false;
            for(ChiTietTraNhaCungCapDTO ctTra : dsTraHang) {
                if(ctTra.getMaSach() == ctGoc.getMaSach()) {
                    if(ctTra.getSoLuong() + slTra > ctGoc.getSoLuong()) {
                        JOptionPane.showMessageDialog(this, "Tổng số lượng trả vượt quá số lượng đã nhập!");
                        return;
                    }
                    ctTra.setSoLuong(ctTra.getSoLuong() + slTra);
                    ctTra.setThanhTienHoan(ctGoc.getGiaNhap().multiply(new BigDecimal(ctTra.getSoLuong())));
                    daCo = true;
                    break;
                }
            }

            if(!daCo) {
                ChiTietTraNhaCungCapDTO traMoi = new ChiTietTraNhaCungCapDTO();
                traMoi.setMaSach(ctGoc.getMaSach());

                String tenSachTbl = tblChiTietPN.getValueAt(row, 1).toString();
                traMoi.setTenSach(tenSachTbl);

                traMoi.setSoLuong(slTra);
                traMoi.setGiaNhap(ctGoc.getGiaNhap());
                traMoi.setThanhTienHoan(ctGoc.getGiaNhap().multiply(new BigDecimal(slTra)));
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

        for (ChiTietTraNhaCungCapDTO ct : dsTraHang) {
            modelTraHang.addRow(new Object[]{
                    "S" + String.format("%03d", ct.getMaSach()),
                    ct.getTenSach(),
                    ct.getSoLuong(),
                    String.format("%,.0f", ct.getGiaNhap()),
                    String.format("%,.0f", ct.getThanhTienHoan())
            });
            tongHoan = tongHoan.add(ct.getThanhTienHoan());
        }
        lblTongTienHoan.setText("Y/C HOÀN TIỀN: " + String.format("%,.0f", tongHoan) + " VNĐ");
    }

    private void xuLyTraHangNCC() {
        if (currentPhieuNhap == null || dsTraHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách để lập phiếu trả!");
            return;
        }

        BigDecimal tongHoan = BigDecimal.ZERO;
        for(ChiTietTraNhaCungCapDTO ct : dsTraHang) tongHoan = tongHoan.add(ct.getThanhTienHoan());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận lập Phiếu Trả Nhà Cung Cấp?\n- Kho sẽ TỰ ĐỘNG TRỪ số lượng tương ứng.\n- Yêu cầu NCC hoàn lại: " + String.format("%,.0f", tongHoan) + " VNĐ",
                "Chốt Trả Hàng NCC", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {

            PhieuTraNhaCungCapDTO pt = new PhieuTraNhaCungCapDTO();
            pt.setMaNV(currentUser.getMaNhanVien() != null ? currentUser.getMaNhanVien() : 1);
            pt.setMaNCC(currentPhieuNhap.getMaNCC());
            pt.setLyDo(txtLyDo.getText());
            pt.setTongTienHoan(tongHoan);

            String ketQua = ptNccBUS.addPhieuTraNCC(pt, dsTraHang);
            JOptionPane.showMessageDialog(this, ketQua);

            if (ketQua.contains("Thành công")) {
                int inPhieu = JOptionPane.showConfirmDialog(this, "Bạn có muốn in Biên bản Trả Hàng ra PDF không?", "In Biên Bản", JOptionPane.YES_NO_OPTION);
                if (inPhieu == JOptionPane.YES_OPTION) {
                    xuatPhieuTraNCCPDF(pt, dsTraHang, currentPhieuNhap.getTenNCC(), currentPhieuNhap.getMaPN());
                }

                dsTraHang.clear();
                capNhatBangTraHang();
                modelChiTietPN.setRowCount(0);
                txtSearchPN.setText("");
                lblThongTinPN.setText("Chưa chọn phiếu nhập nào");
                currentPhieuNhap = null;

                loadLichSuTraNCC();
            }
        }
    }

    private void loadLichSuTraNCC() {
        modelLichSu.setRowCount(0);
        List<PhieuTraNhaCungCapDTO> list = ptNccBUS.getAll();

        if (list != null) {
            for (PhieuTraNhaCungCapDTO pt : list) {
                modelLichSu.addRow(new Object[]{
                        "PTN" + String.format("%03d", pt.getMaPTN()),
                        "NCC" + String.format("%03d", pt.getMaNCC()), // Dùng mã NCC do DTO của ông ko có mã PN
                        "NV" + String.format("%02d", pt.getMaNV()),
                        pt.getLyDo(),
                        pt.getNgayTao() != null ? pt.getNgayTao().format(dtf) : "N/A",
                        String.format("%,.0f VNĐ", pt.getTongTienHoan())
                });
            }
        }
    }

    private void initEventsTab2() {
        txtSearchLichSu.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterLS(); }
            public void removeUpdate(DocumentEvent e) { filterLS(); }
            public void changedUpdate(DocumentEvent e) { filterLS(); }

            private void filterLS() {
                String text = txtSearchLichSu.getText().trim();
                if (text.length() == 0) {
                    sorterLichSu.setRowFilter(null);
                } else {
                    sorterLichSu.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1, 2));
                }
            }
        });

        btnLamMoiLS.addActionListener(e -> {
            txtSearchLichSu.setText("");
            loadLichSuTraNCC();
        });
    }

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

        for(int i=0; i<table.getColumnCount(); i++) {
            if (i != 1 && i != 3) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private void xuatPhieuTraNCCPDF(PhieuTraNhaCungCapDTO pt, List<ChiTietTraNhaCungCapDTO> dsTra, String tenNCC, int maPNGoc) {
        try {
            // Lấy ID thật sau khi insert hoặc lấy ID gốc nếu chưa có
            int maFile = pt.getMaPTN() > 0 ? pt.getMaPTN() : maPNGoc;
            String path = "PhieuTraNCC_PTN" + maFile + "_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(bf, 18, com.itextpdf.text.Font.BOLD, BaseColor.RED);
            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);

            Paragraph title = new Paragraph("BIÊN BẢN TRẢ HÀNG NHÀ CUNG CẤP", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Tham chiếu Phiếu Nhập gốc: PN" + String.format("%03d", maPNGoc), fontNormal));
            document.add(new Paragraph("Kính gửi Nhà cung cấp: " + tenNCC, fontNormal));
            document.add(new Paragraph("Ngày xử lý: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontNormal));
            document.add(new Paragraph("Lý do trả hàng: " + pt.getLyDo(), fontNormal));
            document.add(new Paragraph("---------------------------------------------------------", fontNormal));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(5);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1.5f, 4f, 1.2f, 2f, 2.5f});

            String[] headers = {"Mã Sách", "Tên Sách", "SL Trả", "Đơn Giá Nhập", "Y/C Hoàn Tiền"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            for (ChiTietTraNhaCungCapDTO ct : dsTra) {
                PdfPCell cellMa = new PdfPCell(new Phrase("S" + String.format("%03d", ct.getMaSach()), fontNormal));
                cellMa.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cellMa);

                pdfTable.addCell(new PdfPCell(new Phrase(ct.getTenSach(), fontNormal)));

                PdfPCell cellSL = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                cellSL.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cellSL);

                PdfPCell cellGia = new PdfPCell(new Phrase(String.format("%,.0f", ct.getGiaNhap()), fontNormal));
                cellGia.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfTable.addCell(cellGia);

                PdfPCell cellTien = new PdfPCell(new Phrase(String.format("%,.0f", ct.getThanhTienHoan()), fontNormal));
                cellTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfTable.addCell(cellTien);
            }
            document.add(pdfTable);

            document.add(new Paragraph(" "));

            Paragraph tongTienPara = new Paragraph("TỔNG TIỀN Y/C HOÀN: " + String.format("%,.0f VNĐ", pt.getTongTienHoan()), new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD, BaseColor.RED));
            tongTienPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(tongTienPara);

            document.close();
            java.awt.Desktop.getDesktop().open(new java.io.File(path));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xuất biên lai PDF: " + ex.getMessage());
        }
    }
}