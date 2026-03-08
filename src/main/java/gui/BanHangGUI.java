package gui;

import bus.BanHangBUS;
import bus.KhachHangBUS;
import bus.SachBUS;
import dto.ChiTietHoaDonDTO;
import dto.KhachHangDTO;
import dto.SachDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiSach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BanHangGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private BanHangBUS banHangBUS = new BanHangBUS();
    private KhachHangBUS khachHangBUS = new KhachHangBUS();
    private SachBUS sachBUS = new SachBUS();
    private List<ChiTietHoaDonDTO> dsGioHang = new ArrayList<>();
    private Integer currentCustomerId = null;

    private JTable tblCart, tblBooks, tblOnline;
    private DefaultTableModel cartModel, bookModel, onlineModel;
    private JLabel lblTongTien, lblThanhToan, lblTenKhach;
    private JTextField txtSearch, txtSoLuong, txtSDT;

    private DecimalFormat df = new DecimalFormat("#,###");

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);
    final Color COL_WHITE = Color.WHITE;

    public BanHangGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDanhSachSach();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(COL_BG_MAIN);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Bán hàng Tại quầy (POS)", createPOSPanel());
        tabbedPane.addTab("Duyệt đơn Online", createOnlineOrderPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createPOSPanel() {
        JPanel posPanel = new JPanel(new BorderLayout(15, 0));
        posPanel.setBackground(COL_BG_MAIN);
        posPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- BÊN TRÁI: DANH MỤC SÁCH ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(COL_WHITE);
        searchPanel.setBorder(new LineBorder(new Color(230, 230, 230)));
        searchPanel.add(new JLabel("Tìm sách:"));
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm Kiếm");
        styleButton(btnSearch, COL_SIDEBAR, 110);
        searchPanel.add(btnSearch);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Khởi tạo bảng Sách
        String[] bookCols = {"Mã Sách", "Tên Sách", "Giá Bán", "Kho"};
        bookModel = new DefaultTableModel(bookCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBooks = new JTable(bookModel);

        // Cập nhật Style: Cột 1 (Tên sách) nằm trái
        styleTable(tblBooks, new int[]{1}, -1);

        // --- TÍNH NĂNG DOUBLE CLICK ĐỂ THÊM VÀO GIỎ ---
        tblBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    txtSoLuong.setText("1"); // Mặc định số lượng là 1 khi double click
                    handlingAddToCart();
                }
            }
        });

        JScrollPane scrollBooks = new JScrollPane(tblBooks);
        scrollBooks.setBorder(new LineBorder(new Color(230, 230, 230)));
        leftPanel.add(scrollBooks, BorderLayout.CENTER);

        JPanel pnlAddAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlAddAction.setOpaque(false);
        pnlAddAction.add(new JLabel("Số lượng:"));
        txtSoLuong = new JTextField("1", 5);
        txtSoLuong.setPreferredSize(new Dimension(60, 35));
        txtSoLuong.setHorizontalAlignment(JTextField.CENTER);
        pnlAddAction.add(txtSoLuong);
        JButton btnAddCart = new JButton("THÊM VÀO GIỎ");
        styleButton(btnAddCart, COL_PRIMARY, 150);
        pnlAddAction.add(btnAddCart);
        leftPanel.add(pnlAddAction, BorderLayout.SOUTH);

        // --- BÊN PHẢI: GIỎ HÀNG & THANH TOÁN ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setPreferredSize(new Dimension(450, 0));
        rightPanel.setOpaque(false);

        JPanel pnlCartWrap = new JPanel(new BorderLayout());
        pnlCartWrap.setBackground(COL_WHITE);
        pnlCartWrap.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(230, 230, 230)), "CHI TIẾT GIỎ HÀNG",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), COL_SIDEBAR));

        String[] cartCols = {"Sách", "SL", "Đơn Giá", "Thành Tiền"};
        cartModel = new DefaultTableModel(cartCols, 0);
        tblCart = new JTable(cartModel);

        // Cập nhật Style: Cột 0 (Tên sách trong giỏ) nằm trái
        styleTable(tblCart, new int[]{0}, -1);

        pnlCartWrap.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        JButton btnXoa = new JButton("Xóa món đã chọn");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoa.setForeground(Color.RED);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setBorder(new EmptyBorder(5, 0, 5, 0));
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlCartWrap.add(btnXoa, BorderLayout.SOUTH);
        rightPanel.add(pnlCartWrap, BorderLayout.CENTER);

        // KHU VỰC THANH TOÁN
        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setBackground(COL_WHITE);
        checkoutPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230)), new EmptyBorder(15, 20, 15, 20)));

        JPanel pnlKH = new JPanel(new BorderLayout(10, 0));
        pnlKH.setOpaque(false);
        txtSDT = new JTextField();
        txtSDT.setPreferredSize(new Dimension(0, 35));
        JButton btnCheck = new JButton("Check");
        styleButton(btnCheck, COL_SIDEBAR, 80);
        pnlKH.add(new JLabel("SĐT: "), BorderLayout.WEST);
        pnlKH.add(txtSDT, BorderLayout.CENTER);
        pnlKH.add(btnCheck, BorderLayout.EAST);

        lblTenKhach = new JLabel("Khách: Vãng lai");
        lblTenKhach.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        lblTongTien = new JLabel("Tổng cộng: 0 VNĐ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblThanhToan = new JLabel("CẦN TRẢ: 0 VNĐ");
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblThanhToan.setForeground(COL_PRIMARY);

        JButton btnPay = new JButton("XÁC NHẬN THANH TOÁN");
        btnPay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnPay.setBackground(COL_PRIMARY);
        btnPay.setForeground(COL_WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.setFocusPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.setAlignmentX(Component.CENTER_ALIGNMENT);

        checkoutPanel.add(pnlKH);
        checkoutPanel.add(Box.createVerticalStrut(5));
        checkoutPanel.add(lblTenKhach);
        checkoutPanel.add(Box.createVerticalStrut(15));
        checkoutPanel.add(new JSeparator());
        checkoutPanel.add(Box.createVerticalStrut(15));
        checkoutPanel.add(lblTongTien);
        checkoutPanel.add(Box.createVerticalStrut(10));
        checkoutPanel.add(lblThanhToan);
        checkoutPanel.add(Box.createVerticalStrut(20));
        checkoutPanel.add(btnPay);

        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);
        posPanel.add(leftPanel, BorderLayout.CENTER);
        posPanel.add(rightPanel, BorderLayout.EAST);

        btnXoa.addActionListener(e -> {
            int row = tblCart.getSelectedRow();
            if (row != -1) { dsGioHang.remove(row); capNhatBangGioHang(); }
        });
        btnAddCart.addActionListener(e -> handlingAddToCart());
        btnPay.addActionListener(e -> handlingPayment());
        btnCheck.addActionListener(e -> checkCustomer());

        return posPanel;
    }

    private JPanel createOnlineOrderPanel() {
        JPanel onlinePanel = new JPanel(new BorderLayout(15, 15));
        onlinePanel.setBackground(COL_BG_MAIN);
        onlinePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] onlineCols = {"Mã HĐ", "Khách Hàng", "Ngày Đặt", "Tổng Tiền", "Trạng Thái"};
        onlineModel = new DefaultTableModel(onlineCols, 0);
        tblOnline = new JTable(onlineModel);

        // Cập nhật Style: Cột 1 (Khách hàng) nằm trái, Cột 4 (Trạng thái) tô màu
        styleTable(tblOnline, new int[]{1}, 4);

        JScrollPane scroll = new JScrollPane(tblOnline);
        scroll.setBorder(new LineBorder(new Color(230, 230, 230)));
        onlinePanel.add(new JLabel("Danh sách đơn Online chờ duyệt", JLabel.CENTER), BorderLayout.NORTH);
        onlinePanel.add(scroll, BorderLayout.CENTER);
        return onlinePanel;
    }

    // ========================================================
    // HÀM STYLE BẢNG CHUẨN (Fix căn lề & Tô màu trạng thái)
    // ========================================================
    private void styleTable(JTable table, int[] leftCols, int statusCol) {
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));

        // Style Header: Luôn ở giữa
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Renderer căn giữa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Renderer căn trái (có thụt lề 10px)
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new javax.swing.border.EmptyBorder(0, 10, 0, 0));

        // Renderer tô màu trạng thái
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    String val = value.toString().toUpperCase();
                    if (val.contains("ĐANG BÁN") || val.contains("HOÀN THÀNH") || val.contains("ĐÃ DUYỆT") || val.equals("HOATDONG")) {
                        c.setForeground(new Color(46, 204, 113)); // Xanh lá
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (val.contains("CHỜ") || val.contains("PENDING")) {
                        c.setForeground(new Color(241, 196, 15)); // Vàng
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(231, 76, 60)); // Đỏ
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        };

        // Áp dụng renderer cho từng cột
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == statusCol) {
                table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
            } else {
                boolean isLeft = false;
                for (int col : leftCols) { if (i == col) { isLeft = true; break; } }
                if (isLeft) table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
                else table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private void styleButton(JButton btn, Color bgColor, int width) {
        btn.setBackground(bgColor);
        btn.setForeground(COL_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 35));
    }

    // ========================================================
    // LOGIC XỬ LÝ (BigDecimal & DTO)
    // ========================================================
    private void loadDanhSachSach() {
        bookModel.setRowCount(0);
        List<SachDTO> listSach = sachBUS.getAll();
        for (SachDTO sach : listSach) {
            if (sach.getTrangThai() == TrangThaiSach.DangBan) {
                bookModel.addRow(new Object[]{
                        sach.getMaSachCode(), sach.getTenSach(), df.format(sach.getGiaBan()), sach.getSoLuongTon()
                });
            }
        }
    }

    private void checkCustomer() {
        String phone = txtSDT.getText().trim();
        KhachHangDTO kh = khachHangBUS.getKhachHangByPhone(phone);
        if (kh != null) {
            currentCustomerId = kh.getMaKH();
            lblTenKhach.setText("Khách: " + kh.getHoTen());
            lblTenKhach.setForeground(COL_PRIMARY);
        } else {
            currentCustomerId = null;
            lblTenKhach.setText("Khách: Vãng lai / Không tìm thấy");
            lblTenKhach.setForeground(Color.RED);
        }
    }

    private void handlingAddToCart() {
        int row = tblBooks.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sách trên bảng trước!"); return; }
        String maSach = tblBooks.getValueAt(row, 0).toString();
        try {
            int sl = Integer.parseInt(txtSoLuong.getText());
            if (banHangBUS.themVaoGioHang(maSach, sl, dsGioHang)) {
                capNhatBangGioHang();
                txtSoLuong.setText("1");
            } else { JOptionPane.showMessageDialog(this, "Không đủ kho!"); }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!"); }
    }

    private void handlingPayment() {
        if (dsGioHang.isEmpty()) return;
        double tong = 0;
        for(ChiTietHoaDonDTO ct : dsGioHang) tong += ct.getThanhTien().doubleValue();

        if (banHangBUS.thanhToanHoaDon(currentUser.getMaNhanVien(), currentCustomerId, null, dsGioHang, tong, 0, tong)) {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            dsGioHang.clear();
            capNhatBangGioHang();
            loadDanhSachSach();
        }
    }

    private void capNhatBangGioHang() {
        cartModel.setRowCount(0);
        java.math.BigDecimal tong = java.math.BigDecimal.ZERO;
        for (ChiTietHoaDonDTO ct : dsGioHang) {
            java.math.BigDecimal thanhTienRow = ct.getThanhTien();
            if (thanhTienRow == null) {
                thanhTienRow = ct.getDonGia().multiply(new java.math.BigDecimal(ct.getSoLuong()));
            }
            cartModel.addRow(new Object[]{ ct.getTenSach(), ct.getSoLuong(), df.format(ct.getDonGia()), df.format(thanhTienRow) });
            tong = tong.add(thanhTienRow);
        }
        lblTongTien.setText("Tổng cộng: " + df.format(tong) + " VNĐ");
        lblThanhToan.setText("CẦN TRẢ: " + df.format(tong) + " VNĐ");
    }
}