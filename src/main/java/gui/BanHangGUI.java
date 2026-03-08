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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
    private JComboBox<String> cbxPhuongThucThanhToan;

    private DecimalFormat df = new DecimalFormat("#,###");

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);
    final Color COL_WHITE = Color.WHITE;

    public BanHangGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDanhSachSach();
        loadDonOnline();
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

        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(COL_WHITE);
        searchPanel.setBorder(new LineBorder(new Color(230, 230, 230)));
        searchPanel.add(new JLabel("Tìm sách:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm Kiếm");
        styleButton(btnSearch, COL_SIDEBAR, 110);
        searchPanel.add(btnSearch);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        bookModel = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Giá Bán", "Kho"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBooks = new JTable(bookModel);
        styleTable(tblBooks, new int[]{1}, -1);
        tblBooks.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { txtSoLuong.setText("1"); handlingAddToCart(); }
            }
        });
        leftPanel.add(new JScrollPane(tblBooks), BorderLayout.CENTER);

        JPanel pnlAddAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlAddAction.setOpaque(false);
        pnlAddAction.add(new JLabel("Số lượng:"));
        txtSoLuong = new JTextField("1", 5);
        txtSoLuong.setHorizontalAlignment(JTextField.CENTER);
        pnlAddAction.add(txtSoLuong);
        JButton btnAddCart = new JButton("THÊM VÀO GIỎ");
        styleButton(btnAddCart, COL_PRIMARY, 150);
        pnlAddAction.add(btnAddCart);
        leftPanel.add(pnlAddAction, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setPreferredSize(new Dimension(450, 0));
        rightPanel.setOpaque(false);

        JPanel pnlCartWrap = new JPanel(new BorderLayout());
        pnlCartWrap.setBackground(COL_WHITE);
        pnlCartWrap.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(230, 230, 230)), "GIỎ HÀNG"));
        cartModel = new DefaultTableModel(new String[]{"Sách", "SL", "Đơn Giá", "Thành Tiền"}, 0);
        tblCart = new JTable(cartModel);
        styleTable(tblCart, new int[]{0}, -1);
        pnlCartWrap.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        JButton btnXoa = new JButton("Xóa món đã chọn");
        btnXoa.setForeground(Color.RED);
        btnXoa.setContentAreaFilled(false);
        pnlCartWrap.add(btnXoa, BorderLayout.SOUTH);
        rightPanel.add(pnlCartWrap, BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setBackground(COL_WHITE);
        checkoutPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230)), new EmptyBorder(15, 20, 15, 20)));

        JPanel pnlKH = new JPanel(new BorderLayout(10, 0));
        pnlKH.setOpaque(false);
        txtSDT = new JTextField();
        JButton btnCheck = new JButton("Check");
        styleButton(btnCheck, COL_SIDEBAR, 80);
        pnlKH.add(new JLabel("SĐT: "), BorderLayout.WEST);
        pnlKH.add(txtSDT, BorderLayout.CENTER);
        pnlKH.add(btnCheck, BorderLayout.EAST);

        lblTenKhach = new JLabel("Khách: Vãng lai");

        JPanel pnlPromo = new JPanel(new BorderLayout(10, 0));
        pnlPromo.setOpaque(false);
        txtMaGiamGia = new JTextField();
        JButton btnApply = new JButton("Áp dụng");
        styleButton(btnApply, COL_SIDEBAR, 80);
        pnlPromo.add(new JLabel("Mã KM: "), BorderLayout.WEST);
        pnlPromo.add(txtMaGiamGia, BorderLayout.CENTER);
        pnlPromo.add(btnApply, BorderLayout.EAST);

        lblTongTien = new JLabel("Tổng cộng: 0 VNĐ");
        lblGiamGia = new JLabel("Giảm giá: 0 VNĐ");
        lblGiamGia.setForeground(new Color(46, 204, 113));
        lblThanhToan = new JLabel("CẦN TRẢ: 0 VNĐ");
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblThanhToan.setForeground(COL_PRIMARY);

        cbxPhuongThucThanhToan = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản / Quẹt thẻ"});

        JButton btnPay = new JButton("XÁC NHẬN THANH TOÁN");
        btnPay.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnPay.setBackground(COL_PRIMARY);
        btnPay.setForeground(COL_WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnPay.setAlignmentX(Component.CENTER_ALIGNMENT);

        checkoutPanel.add(pnlKH);
        checkoutPanel.add(lblTenKhach);
        checkoutPanel.add(Box.createVerticalStrut(10));
        checkoutPanel.add(pnlPromo);
        checkoutPanel.add(Box.createVerticalStrut(10));
        checkoutPanel.add(new JSeparator());
        checkoutPanel.add(lblTongTien);
        checkoutPanel.add(lblGiamGia);
        checkoutPanel.add(lblThanhToan);
        checkoutPanel.add(Box.createVerticalStrut(10));
        checkoutPanel.add(new JLabel("PTTT:"));
        checkoutPanel.add(cbxPhuongThucThanhToan);
        checkoutPanel.add(Box.createVerticalStrut(20));
        checkoutPanel.add(btnPay);

        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);
        posPanel.add(leftPanel, BorderLayout.CENTER);
        posPanel.add(rightPanel, BorderLayout.EAST);

        btnAddCart.addActionListener(e -> handlingAddToCart());
        btnCheck.addActionListener(e -> checkCustomer());
        btnApply.addActionListener(e -> handlingApplyPromo());
        btnPay.addActionListener(e -> handlingPayment());
        btnXoa.addActionListener(e -> {
            int r = tblCart.getSelectedRow();
            if(r != -1) { dsGioHang.remove(r); capNhatBangGioHang(); }
        });

        return posPanel;
    }

    private JPanel createOnlineOrderPanel() {
        JPanel onlinePanel = new JPanel(new BorderLayout(15, 15));
        onlinePanel.setBackground(COL_BG_MAIN);
        onlinePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] onlineCols = {"Mã HĐ", "Khách Hàng", "Ngày Đặt", "Tổng Tiền", "Trạng Thái"};
        onlineModel = new DefaultTableModel(onlineCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblOnline = new JTable(onlineModel);
        styleTable(tblOnline, new int[]{1}, 4);

        JScrollPane scroll = new JScrollPane(tblOnline);
        scroll.setBorder(new LineBorder(new Color(230, 230, 230)));

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        JLabel lblTitle = new JLabel("DANH SÁCH ĐƠN HÀNG ONLINE CHỜ DUYỆT", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlTop.add(lblTitle, BorderLayout.WEST);

        JButton btnDuyet = new JButton("DUYỆT ĐƠN ĐÃ CHỌN");
        styleButton(btnDuyet, COL_PRIMARY, 180);
        pnlTop.add(btnDuyet, BorderLayout.EAST);

        onlinePanel.add(pnlTop, BorderLayout.NORTH);
        onlinePanel.add(scroll, BorderLayout.CENTER);

        btnDuyet.addActionListener(e -> handlingDuyetDonOnline());

        return onlinePanel;
    }

    private void loadDonOnline() {
        onlineModel.setRowCount(0);
        List<Object[]> list = banHangBUS.getDonOnlinePending();
        for (Object[] obj : list) {
            onlineModel.addRow(obj);
        }
    }

    private void handlingDuyetDonOnline() {
        int row = tblOnline.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần duyệt!");
            return;
        }
        String maHD = tblOnline.getValueAt(row, 0).toString();
        if (banHangBUS.duyetDonOnline(maHD, currentUser.getTenDangNhap())) {
            JOptionPane.showMessageDialog(this, "Đã duyệt đơn hàng " + maHD);
            loadDonOnline();
        }
    }

    private void handlingPayment() {
        if (dsGioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!");
            return;
        }

        BigDecimal tong = BigDecimal.ZERO;
        for(ChiTietHoaDonDTO ct : dsGioHang) tong = tong.add(ct.getThanhTien());

        BigDecimal thucThu = tong.subtract(soTienDuocGiam);
        if (thucThu.compareTo(BigDecimal.ZERO) < 0) thucThu = BigDecimal.ZERO;

        String ptttStr = cbxPhuongThucThanhToan.getSelectedItem().toString();
        PhuongThucThanhToan phuongThucEnum = ptttStr.contains("Tiền mặt") ? PhuongThucThanhToan.TienMat : PhuongThucThanhToan.ChuyenKhoan;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận thanh toán " + df.format(thucThu) + " VNĐ\nHình thức: " + ptttStr + "?",
                "Xác nhận đơn hàng", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Integer maKM = (kmApDung != null) ? kmApDung.getMaKM() : null;

            boolean thanhCong = banHangBUS.thanhToanHoaDon(currentUser.getMaNhanVien(), currentCustomerId, maKM, dsGioHang, tong.doubleValue(), soTienDuocGiam.doubleValue(), thucThu.doubleValue(), phuongThucEnum);

            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                int inPhieu = JOptionPane.showConfirmDialog(this, "Bạn có muốn in hóa đơn không?", "In Hóa Đơn", JOptionPane.YES_NO_OPTION);
                if (inPhieu == JOptionPane.YES_OPTION) {
                    xuatHoaDonPDF(tong, soTienDuocGiam, thucThu, ptttStr);
                }
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại! Vui lòng kiểm tra lại hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuatHoaDonPDF(BigDecimal tong, BigDecimal giam, BigDecimal thucThu, String pttt) {
        try {
            String path = "HoaDon_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(bf, 18, com.itextpdf.text.Font.BOLD, BaseColor.MAGENTA);
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
            document.add(new Paragraph("Khuyến mãi: -" + df.format(giam) + " VNĐ", fontNormal));
            document.add(new Paragraph("THÀNH TIỀN: " + df.format(thucThu) + " VNĐ", fontHeader));
            document.close();
            java.awt.Desktop.getDesktop().open(new java.io.File(path));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void resetForm() {
        dsGioHang.clear(); kmApDung = null; soTienDuocGiam = BigDecimal.ZERO; currentCustomerId = null;
        txtSDT.setText(""); txtMaGiamGia.setText(""); lblTenKhach.setText("Khách: Vãng lai");
        capNhatBangGioHang(); loadDanhSachSach();
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
        lblTongTien.setText("Tổng hàng: " + df.format(tongHang) + " VNĐ");
        lblGiamGia.setText("Khuyến mãi: -" + df.format(soTienDuocGiam) + " VNĐ");
        lblThanhToan.setText("CẦN TRẢ: " + df.format(thanhToanCuoi) + " VNĐ");
    }

    private void styleButton(JButton btn, Color bgColor, int width) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(bgColor); btn.setForeground(COL_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(true); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 35));
    }

    private void styleTable(JTable table, int[] leftCols, int statusCol) {
        table.setRowHeight(40); table.setSelectionBackground(new Color(232, 240, 255));
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadDanhSachSach() {
        bookModel.setRowCount(0);
        List<SachDTO> list = sachBUS.getAll();
        for (SachDTO s : list) {
            if (s.getTrangThai() == TrangThaiSach.DangBan)
                bookModel.addRow(new Object[]{s.getMaSachCode(), s.getTenSach(), df.format(s.getGiaBan()), s.getSoLuongTon()});
        }
    }

    private void checkCustomer() {
        KhachHangDTO kh = khachHangBUS.getKhachHangByPhone(txtSDT.getText().trim());
        if (kh != null) {
            currentCustomerId = kh.getMaKH();
            lblTenKhach.setText("Khách: " + kh.getHoTen());
            lblTenKhach.setForeground(COL_PRIMARY);
        } else {
            currentCustomerId = null;
            lblTenKhach.setText("Khách: Vãng lai");
            lblTenKhach.setForeground(Color.RED);
        }
    }

    private void handlingAddToCart() {
        int row = tblBooks.getSelectedRow();
        if (row == -1) return;
        String maSach = tblBooks.getValueAt(row, 0).toString();
        try {
            int sl = Integer.parseInt(txtSoLuong.getText());
            if (banHangBUS.themVaoGioHang(maSach, sl, dsGioHang)) capNhatBangGioHang();
            else JOptionPane.showMessageDialog(this, "Không đủ kho!");
        } catch (Exception e) {}
    }
}