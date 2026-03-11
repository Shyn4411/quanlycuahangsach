package gui;

import bus.BanHangBUS;
import bus.KhachHangBUS;
import bus.KhuyenMaiBUS;
import bus.SachBUS;
import dto.ChiTietHoaDonDTO;
import dto.KhachHangDTO;
import dto.KhuyenMaiDTO;
import dto.SachDTO;
import dto.TaiKhoanDTO;
import enums.PhuongThucThanhToan;
import enums.TrangThaiSach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class BanHangGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private BanHangBUS banHangBUS = new BanHangBUS();
    private KhachHangBUS khachHangBUS = new KhachHangBUS();
    private KhuyenMaiBUS kmBUS = new KhuyenMaiBUS();
    private SachBUS sachBUS = new SachBUS();

    private List<ChiTietHoaDonDTO> dsGioHang = new ArrayList<>();
    private Integer currentCustomerId = null;
    private KhuyenMaiDTO kmApDung = null;
    private BigDecimal soTienDuocGiam = BigDecimal.ZERO;

    private JTable tblCart, tblBooks, tblOnline;
    private DefaultTableModel cartModel, bookModel, onlineModel;
    private JLabel lblTongTien, lblThanhToan, lblTenKhach, lblGiamGia;
    private JTextField txtSearch, txtSoLuong, txtSDT, txtMaGiamGia;
    private JComboBox<PhuongThucThanhToan> cbxPhuongThucThanhToan;

    private DecimalFormat df = new DecimalFormat("#,###");

    // ===== BẢNG MÀU ĐỒNG BỘ TỪ UI THIẾT KẾ =====
    private final Color CLR_BG_MAIN = Color.decode("#F8F4EC");
    private final Color CLR_SIDEBAR = Color.decode("#43334C");
    private final Color CLR_ACTIVE  = Color.decode("#E83C91");
    private final Color CLR_WHITE   = Color.WHITE;
    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    public BanHangGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(CLR_BG_MAIN);

        initUI();
        loadDanhSachSach();
        loadDonOnline();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CLR_WHITE);
        tabbedPane.setForeground(CLR_SIDEBAR);

        tabbedPane.addTab("Bán hàng Tại quầy (POS)", createPOSPanel());
        tabbedPane.addTab("Duyệt đơn Online", createOnlineOrderPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createPOSPanel() {
        JPanel posPanel = new JPanel(new BorderLayout(15, 15));
        posPanel.setBackground(CLR_BG_MAIN);
        posPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ================= TRÁI: TÌM KIẾM & BẢNG SÁCH =================
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(CLR_BG_MAIN);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(CLR_WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        searchPanel.add(new JLabel("Tìm sách (Tên/Mã): "));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = new JButton("Tìm");
        styleButton(btnSearch, CLR_SIDEBAR);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        bookModel = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Giá Bán", "Kho"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBooks = new JTable(bookModel);
        styleTable(tblBooks);

        // Logic Tìm kiếm tự động (RowSorter)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(bookModel);
        tblBooks.setRowSorter(sorter);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            private void search() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        tblBooks.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { txtSoLuong.setText("1"); handlingAddToCart(); }
            }
        });

        JScrollPane scrollBooks = new JScrollPane(tblBooks);
        scrollBooks.getViewport().setBackground(CLR_WHITE);
        scrollBooks.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        leftPanel.add(scrollBooks, BorderLayout.CENTER);

        JPanel pnlAddAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAddAction.setBackground(CLR_BG_MAIN);
        pnlAddAction.add(new JLabel("Số lượng: "));
        txtSoLuong = new JTextField("1", 5);
        txtSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSoLuong.setHorizontalAlignment(JTextField.CENTER);
        JButton btnAddCart = new JButton("Thêm vào giỏ");
        styleButton(btnAddCart, CLR_SIDEBAR);
        pnlAddAction.add(txtSoLuong);
        pnlAddAction.add(btnAddCart);
        leftPanel.add(pnlAddAction, BorderLayout.SOUTH);
        btnAddCart.addActionListener(e -> handlingAddToCart());

        // ================= PHẢI: GIỎ HÀNG & THANH TOÁN =================
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setPreferredSize(new Dimension(480, 0));
        rightPanel.setBackground(CLR_BG_MAIN);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(CLR_WHITE);
        cartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Giỏ Hàng", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), CLR_SIDEBAR));

        cartModel = new DefaultTableModel(new String[]{"Tên Sách", "SL", "Đơn Giá", "Thành Tiền"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCart = new JTable(cartModel);
        styleTable(tblCart);
        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.getViewport().setBackground(CLR_WHITE);
        scrollCart.setBorder(BorderFactory.createEmptyBorder());
        cartPanel.add(scrollCart, BorderLayout.CENTER);

        JButton btnXoaMon = new JButton("Xóa dòng chọn");
        btnXoaMon.setForeground(Color.RED);
        btnXoaMon.setBackground(CLR_WHITE);
        btnXoaMon.setFocusPainted(false);
        btnXoaMon.addActionListener(e -> {
            int r = tblCart.getSelectedRow();
            if(r != -1) { dsGioHang.remove(r); capNhatBangGioHang(); }
        });
        cartPanel.add(btnXoaMon, BorderLayout.SOUTH);
        rightPanel.add(cartPanel, BorderLayout.CENTER);

        // KHUNG THANH TOÁN (Gộp Khách Hàng + Khuyến Mãi + Tổng tiền)
        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setBackground(CLR_WHITE);
        checkoutPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(15, 15, 15, 15)));

        // 1. Khách hàng
        JPanel pnlKH = new JPanel(new BorderLayout(10, 0));
        pnlKH.setBackground(CLR_WHITE);
        txtSDT = new JTextField();
        JButton btnCheck = new JButton("Check");
        styleButton(btnCheck, CLR_SIDEBAR);
        pnlKH.add(new JLabel("SĐT Khách: "), BorderLayout.WEST);
        pnlKH.add(txtSDT, BorderLayout.CENTER);
        pnlKH.add(btnCheck, BorderLayout.EAST);
        lblTenKhach = new JLabel("Khách: Vãng lai");
        lblTenKhach.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        // 2. Khuyến mãi
        JPanel pnlPromo = new JPanel(new BorderLayout(10, 0));
        pnlPromo.setBackground(CLR_WHITE);
        txtMaGiamGia = new JTextField();
        JButton btnApply = new JButton("Áp dụng");
        styleButton(btnApply, CLR_SIDEBAR);
        pnlPromo.add(new JLabel("Mã KM:      "), BorderLayout.WEST);
        pnlPromo.add(txtMaGiamGia, BorderLayout.CENTER);
        pnlPromo.add(btnApply, BorderLayout.EAST);

        // 3. Tiền bạc
        lblTongTien = new JLabel("Tổng cộng: 0 VNĐ");
        lblTongTien.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblGiamGia = new JLabel("Giảm giá: 0 VNĐ");
        lblGiamGia.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblGiamGia.setForeground(new Color(46, 204, 113));
        lblThanhToan = new JLabel("CẦN TRẢ: 0 VNĐ");
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblThanhToan.setForeground(CLR_ACTIVE);

        // 4. Phương thức TT
        JPanel pnlPTTT = new JPanel(new BorderLayout(10, 0));
        pnlPTTT.setBackground(CLR_WHITE);
        cbxPhuongThucThanhToan = new JComboBox<>(PhuongThucThanhToan.values());
        pnlPTTT.add(new JLabel("Thanh toán: "), BorderLayout.WEST);
        pnlPTTT.add(cbxPhuongThucThanhToan, BorderLayout.CENTER);

        JButton btnPay = new JButton("XÁC NHẬN THANH TOÁN");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnPay.setBackground(COL_PRIMARY);
        btnPay.setForeground(CLR_WHITE);
        btnPay.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnPay.setFocusPainted(false);
        btnPay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnPay.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ráp vào Box
        checkoutPanel.add(pnlKH);
        checkoutPanel.add(lblTenKhach);
        checkoutPanel.add(Box.createVerticalStrut(10));
        checkoutPanel.add(pnlPromo);
        checkoutPanel.add(Box.createVerticalStrut(15));
        checkoutPanel.add(new JSeparator());
        checkoutPanel.add(Box.createVerticalStrut(10));
        checkoutPanel.add(lblTongTien);
        checkoutPanel.add(lblGiamGia);
        checkoutPanel.add(lblThanhToan);
        checkoutPanel.add(Box.createVerticalStrut(10));
        checkoutPanel.add(pnlPTTT);
        checkoutPanel.add(Box.createVerticalStrut(15));
        checkoutPanel.add(btnPay);

        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);
        posPanel.add(leftPanel, BorderLayout.CENTER);
        posPanel.add(rightPanel, BorderLayout.EAST);

        // Events
        btnCheck.addActionListener(e -> checkCustomer());
        btnApply.addActionListener(e -> handlingApplyPromo());
        btnPay.addActionListener(e -> handlingPayment());

        return posPanel;
    }

    private JPanel createOnlineOrderPanel() {
        JPanel onlinePanel = new JPanel(new BorderLayout(15, 15));
        onlinePanel.setBackground(CLR_BG_MAIN);
        onlinePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerOnline = new JPanel(new BorderLayout());
        headerOnline.setBackground(CLR_BG_MAIN);
        JLabel lblTitle = new JLabel("Danh sách Đơn hàng chờ duyệt");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(CLR_SIDEBAR);
        headerOnline.add(lblTitle, BorderLayout.WEST);

        JButton btnLamMoiOnline = new JButton("Làm mới");
        styleButton(btnLamMoiOnline, new Color(100, 100, 100));
        btnLamMoiOnline.addActionListener(e -> loadDonOnline());
        headerOnline.add(btnLamMoiOnline, BorderLayout.EAST);
        onlinePanel.add(headerOnline, BorderLayout.NORTH);

        onlineModel = new DefaultTableModel(new String[]{"Mã HĐ", "Khách Hàng", "Ngày Đặt", "Tổng Tiền", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblOnline = new JTable(onlineModel);
        styleTable(tblOnline);

        JScrollPane scroll = new JScrollPane(tblOnline);
        scroll.getViewport().setBackground(CLR_WHITE);
        scroll.setBorder(new LineBorder(Color.LIGHT_GRAY));
        onlinePanel.add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(CLR_BG_MAIN);

        JButton btnHuyDon = new JButton("Từ Chối / Hủy Đơn");
        styleButton(btnHuyDon, new Color(231, 76, 60)); // Màu đỏ

        btnHuyDon.addActionListener(e -> {
            int row = tblOnline.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần từ chối!");
                return;
            }

            String maHD = tblOnline.getValueAt(row, 0).toString();
            String[] options = {"Hết hàng", "Khách đổi ý", "Không liên lạc được khách", "Lý do khác..."};
            String lyDo = (String) JOptionPane.showInputDialog(this,
                    "Bạn đang từ chối đơn hàng " + maHD + "\nVui lòng chọn lý do:",
                    "Từ chối đơn Online", JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (lyDo != null && !lyDo.trim().isEmpty()) {
                if (lyDo.equals("Lý do khác...")) {
                    lyDo = JOptionPane.showInputDialog(this, "Nhập lý do từ chối:");
                    if (lyDo == null || lyDo.trim().isEmpty()) return;
                }

                // GỌI BUS ĐỂ HỦY ĐƠN (Chỉ đổi trạng thái thành DA_HUY, KHÔNG trừ kho)
                int maHD_Int = Integer.parseInt(maHD.replaceAll("[^0-9]", ""));
                String result = banHangBUS.huyDonOnline(maHD_Int, lyDo);
                JOptionPane.showMessageDialog(this, result);
                loadDonOnline();
            }
        });

        JButton btnDuyet = new JButton("Duyệt Đơn & Trừ Kho");
        styleButton(btnDuyet, CLR_ACTIVE);
        btnDuyet.addActionListener(e -> handlingDuyetDonOnline());
        bottomPanel.add(btnDuyet);
        onlinePanel.add(bottomPanel, BorderLayout.SOUTH);

        return onlinePanel;
    }

    // ========================================================
    // LOGIC NGHIỆP VỤ (GIỮ NGUYÊN TỪ BẢN 1)
    // ========================================================

    private void loadDanhSachSach() {
        bookModel.setRowCount(0);
        List<SachDTO> list = sachBUS.getAll();
        for (SachDTO s : list) {
            if (s.getTrangThai() == TrangThaiSach.DANG_BAN)
                bookModel.addRow(new Object[]{s.getMaSachCode(), s.getTenSach(), df.format(s.getGiaBan()), s.getSoLuongTon()});
        }
    }

    private void loadDonOnline() {
        onlineModel.setRowCount(0);
        List<Object[]> list = banHangBUS.getDonOnlinePending();
        for (Object[] obj : list) onlineModel.addRow(obj);
    }

    private void handlingDuyetDonOnline() {
        int row = tblOnline.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần duyệt!");
            return;
        }

        // 1. Lấy mã dạng chuỗi hiển thị trên bảng (VD: "HD105")
        String maHD_String = tblOnline.getValueAt(row, 0).toString();

        // 2. Cắt bỏ chữ "HD", chỉ giữ lại phần số và ép kiểu sang int
        int maHD_Int = Integer.parseInt(maHD_String.replaceAll("[^0-9]", ""));

        // 3. Truyền mã int xuống BUS
        if (banHangBUS.duyetDonOnline(maHD_Int, currentUser.getTenDangNhap())) {
            JOptionPane.showMessageDialog(this, "Đã duyệt đơn hàng " + maHD_String);
            loadDonOnline();
        }
    }

    private void handlingAddToCart() {
        int row = tblBooks.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sách!"); return; }
        int modelRow = tblBooks.convertRowIndexToModel(row);
        String maSach = bookModel.getValueAt(modelRow, 0).toString();

        try {
            int sl = Integer.parseInt(txtSoLuong.getText().trim());
            if (sl <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                return;
            }
            String maClean = maSach.replaceAll("[^0-9]", "");
            if (maClean.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã sách không hợp lệ!");
                return;
            }
            if (banHangBUS.themVaoGioHang(maClean, sl, dsGioHang)) {
                capNhatBangGioHang();
                txtSoLuong.setText("1");
            } else {
                JOptionPane.showMessageDialog(this, "Mã sách không hợp lệ hoặc số lượng tồn kho không đủ!");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng hợp lệ (chỉ nhập số)!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private void checkCustomer() {
        KhachHangDTO kh = khachHangBUS.getKhachHangByPhone(txtSDT.getText().trim());
        if (kh != null) {
            currentCustomerId = kh.getMaKH();
            lblTenKhach.setText("Khách: " + kh.getHoTen());
            lblTenKhach.setForeground(CLR_ACTIVE);
        } else {
            currentCustomerId = null;
            lblTenKhach.setText("Khách: Vãng lai");
            lblTenKhach.setForeground(Color.RED);
        }
    }

    private void handlingApplyPromo() {
        String code = txtMaGiamGia.getText().trim();
        if (code.isEmpty()) { kmApDung = null; soTienDuocGiam = BigDecimal.ZERO; capNhatBangGioHang(); return; }
        List<KhuyenMaiDTO> dsKM = kmBUS.getAll(); KhuyenMaiDTO found = null;
        for (KhuyenMaiDTO km : dsKM) { if (km.getMaCode().equalsIgnoreCase(code) && km.getTrangThai().name().equals("HoatDong")) { found = km; break; } }
        if (found == null) { JOptionPane.showMessageDialog(this, "Mã không hợp lệ hoặc hết hạn!"); return; }
        BigDecimal tongHang = BigDecimal.ZERO;
        for (ChiTietHoaDonDTO ct : dsGioHang) tongHang = tongHang.add(ct.getThanhTien());
        if (tongHang.compareTo(found.getDonHangToiThieu()) < 0) { JOptionPane.showMessageDialog(this, "Đơn hàng tối thiểu phải từ " + df.format(found.getDonHangToiThieu()) + " VNĐ"); return; }
        kmApDung = found; JOptionPane.showMessageDialog(this, "Áp dụng thành công: " + found.getTenKM()); capNhatBangGioHang();
    }

    private void capNhatBangGioHang() {
        cartModel.setRowCount(0); BigDecimal tongHang = BigDecimal.ZERO;
        for (ChiTietHoaDonDTO ct : dsGioHang) {
            cartModel.addRow(new Object[]{ ct.getTenSach(), ct.getSoLuong(), df.format(ct.getDonGia()), df.format(ct.getThanhTien()) });
            tongHang = tongHang.add(ct.getThanhTien());
        }
        soTienDuocGiam = BigDecimal.ZERO;
        if (kmApDung != null) {
            if (kmApDung.getSoTienGiam().compareTo(BigDecimal.ZERO) > 0) soTienDuocGiam = kmApDung.getSoTienGiam();
            else soTienDuocGiam = tongHang.multiply(kmApDung.getPhanTramGiam()).divide(new BigDecimal("100"));
        }
        BigDecimal thanhToanCuoi = tongHang.subtract(soTienDuocGiam);
        if (thanhToanCuoi.compareTo(BigDecimal.ZERO) < 0) thanhToanCuoi = BigDecimal.ZERO;

        lblTongTien.setText("Tổng hàng: " + df.format(tongHang) + " VNĐ");
        lblGiamGia.setText("Khuyến mãi: " + df.format(soTienDuocGiam) + " VNĐ");
        lblThanhToan.setText("CẦN TRẢ: " + df.format(thanhToanCuoi) + " VNĐ");
    }

    private void handlingPayment() {
        if (dsGioHang.isEmpty()) { JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!"); return; }
        BigDecimal tong = BigDecimal.ZERO;
        for(ChiTietHoaDonDTO ct : dsGioHang) tong = tong.add(ct.getThanhTien());
        BigDecimal thucThu = tong.subtract(soTienDuocGiam);
        if (thucThu.compareTo(BigDecimal.ZERO) < 0) thucThu = BigDecimal.ZERO;

        String ptttStr = cbxPhuongThucThanhToan.getSelectedItem().toString();
        PhuongThucThanhToan phuongThucEnum = ptttStr.contains("Tiền mặt") ? PhuongThucThanhToan.TIEN_MAT : PhuongThucThanhToan.CHUYEN_KHOAN;

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán " + df.format(thucThu) + " VNĐ\nHình thức: " + ptttStr + "?", "Xác nhận đơn hàng", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer maKM = (kmApDung != null) ? kmApDung.getMaKM() : null;
            // Chú ý: Cần chắc chắn user.getMaNhanVien() không bị null
            boolean thanhCong = banHangBUS.thanhToanHoaDon(currentUser.getMaNhanVien(), currentCustomerId, maKM, dsGioHang, tong.doubleValue(), soTienDuocGiam.doubleValue(), thucThu.doubleValue(), phuongThucEnum);

            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                int inPhieu = JOptionPane.showConfirmDialog(this, "Bạn có muốn in hóa đơn không?", "In Hóa Đơn", JOptionPane.YES_NO_OPTION);
                if (inPhieu == JOptionPane.YES_OPTION) xuatHoaDonPDF(tong, soTienDuocGiam, thucThu, ptttStr);
                resetForm();
            } else JOptionPane.showMessageDialog(this, "Thanh toán thất bại! Vui lòng kiểm tra lại hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuatHoaDonPDF(BigDecimal tong, BigDecimal giam, BigDecimal thucThu, String pttt) {
        try {
            String path = "HoaDon_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(bf, 18, com.itextpdf.text.Font.BOLD, BaseColor.BLACK);
            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN LẺ", fontTitle); title.setAlignment(Element.ALIGN_CENTER); document.add(title);
            document.add(new Paragraph("Nhân viên: " + currentUser.getTenDangNhap(), fontNormal));
            document.add(new Paragraph("Khách hàng: " + lblTenKhach.getText(), fontNormal));
            document.add(new Paragraph("Ngày: " + LocalDate.now().toString(), fontNormal));
            document.add(new Paragraph("PTTT: " + pttt, fontNormal));
            document.add(new Paragraph("---------------------------------------------------------", fontNormal));
            PdfPTable pdfTable = new PdfPTable(4); pdfTable.setWidthPercentage(100);
            pdfTable.addCell(new PdfPCell(new Phrase("Tên Sách", fontHeader)));
            pdfTable.addCell(new PdfPCell(new Phrase("SL", fontHeader)));
            pdfTable.addCell(new PdfPCell(new Phrase("Đơn Giá", fontHeader)));
            pdfTable.addCell(new PdfPCell(new Phrase("Thành Tiền", fontHeader)));
            for (ChiTietHoaDonDTO ct : dsGioHang) {
                pdfTable.addCell(new PdfPCell(new Phrase(ct.getTenSach(), fontNormal)));
                pdfTable.addCell(new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal)));
                pdfTable.addCell(new PdfPCell(new Phrase(df.format(ct.getDonGia()), fontNormal)));
                pdfTable.addCell(new PdfPCell(new Phrase(df.format(ct.getThanhTien()), fontNormal)));
            }
            document.add(pdfTable);
            document.add(new Paragraph("Tổng cộng: " + df.format(tong) + " VNĐ", fontNormal));
            document.add(new Paragraph("Khuyến mãi: " + df.format(giam) + " VNĐ", fontNormal));
            document.add(new Paragraph("THÀNH TIỀN: " + df.format(thucThu) + " VNĐ", fontHeader));
            document.close();
            java.awt.Desktop.getDesktop().open(new java.io.File(path));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void resetForm() {
        dsGioHang.clear(); kmApDung = null; soTienDuocGiam = BigDecimal.ZERO; currentCustomerId = null;
        txtSDT.setText(""); txtMaGiamGia.setText(""); txtSearch.setText(""); txtSoLuong.setText("1");
        lblTenKhach.setText("Khách: Vãng lai"); lblTenKhach.setForeground(Color.BLACK);
        capNhatBangGioHang(); loadDanhSachSach();
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setForeground(Color.BLACK);
        header.setOpaque(false);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 35));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    }
}