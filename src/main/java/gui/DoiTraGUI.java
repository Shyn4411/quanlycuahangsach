package gui;

import bus.HoaDonBUS;
import bus.PhieuTraKhachHangBUS;
import dto.ChiTietHoaDonDTO;
import dto.ChiTietTraKhachHangDTO;
import dto.HoaDonDTO;
import dto.PhieuTraKhachHangDTO;
import dto.TaiKhoanDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class DoiTraGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private PhieuTraKhachHangBUS phieuTraBUS = new PhieuTraKhachHangBUS();

    // Dữ liệu tạm
    private HoaDonDTO currentHoaDon = null;
    private List<ChiTietHoaDonDTO> listChiTietHD = new ArrayList<>();
    private List<ChiTietTraKhachHangDTO> dsTraHang = new ArrayList<>();

    // UI Components
    private JTextField txtSearchHD, txtLyDo;
    private JTable tblHoaDonCu, tblTraHang;
    private DefaultTableModel modelHDCu, modelTraHang;
    private JLabel lblTongTienHoan, lblThongTinHD;
    private JComboBox<String> cbxTinhTrang;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    public DoiTraGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ==========================================
        // BÊN TRÁI: TÌM KIẾM HÓA ĐƠN GỐC
        // ==========================================
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new Dimension(500, 0));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.setBorder(new LineBorder(new Color(230, 230, 230)));
        pnlSearch.add(new JLabel("Nhập Mã HĐ (VD: 1, 2...):"));

        txtSearchHD = new JTextField(15);
        txtSearchHD.setPreferredSize(new Dimension(150, 32));
        pnlSearch.add(txtSearchHD);

        JButton btnSearch = new JButton("Tìm Kiếm");
        styleButton(btnSearch, COL_SIDEBAR, 100);
        pnlSearch.add(btnSearch);

        pnlLeft.add(pnlSearch, BorderLayout.NORTH);

        // Bảng Chi Tiết Hóa Đơn Cũ
        String[] colsHD = {"Mã Sách", "Tên Sách", "Giá Mua", "SL Đã Mua"};
        modelHDCu = new DefaultTableModel(colsHD, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHoaDonCu = new JTable(modelHDCu);
        styleTable(tblHoaDonCu);

        JPanel pnlTableLeft = new JPanel(new BorderLayout());
        pnlTableLeft.setBackground(Color.WHITE);
        pnlTableLeft.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "SẢN PHẨM TRONG HÓA ĐƠN",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), COL_SIDEBAR));

        lblThongTinHD = new JLabel("Chưa chọn hóa đơn");
        lblThongTinHD.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblThongTinHD.setBorder(new EmptyBorder(5, 10, 10, 10));
        pnlTableLeft.add(lblThongTinHD, BorderLayout.NORTH);
        pnlTableLeft.add(new JScrollPane(tblHoaDonCu), BorderLayout.CENTER);

        // Hướng dẫn
        JLabel lblHint = new JLabel("<html><i>* Nhấp đúp (Double-click) vào sản phẩm bên trên để đưa vào danh sách trả</i></html>");
        lblHint.setForeground(Color.RED);
        pnlTableLeft.add(lblHint, BorderLayout.SOUTH);

        pnlLeft.add(pnlTableLeft, BorderLayout.CENTER);

        // ==========================================
        // BÊN PHẢI: GIỎ HÀNG TRẢ & XỬ LÝ HOÀN TIỀN
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setOpaque(false);

        JPanel pnlTableRight = new JPanel(new BorderLayout());
        pnlTableRight.setBackground(Color.WHITE);
        pnlTableRight.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "DANH SÁCH SÁCH TRẢ LẠI",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13), Color.RED));

        String[] colsTra = {"Sách", "SL Trả", "Tình Trạng", "Tiền Hoàn"};
        modelTraHang = new DefaultTableModel(colsTra, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTraHang = new JTable(modelTraHang);
        styleTable(tblTraHang);
        pnlTableRight.add(new JScrollPane(tblTraHang), BorderLayout.CENTER);

        JButton btnXoaMon = new JButton("Xóa món trả");
        btnXoaMon.setForeground(Color.RED);
        btnXoaMon.setContentAreaFilled(false);
        btnXoaMon.setBorderPainted(false);
        btnXoaMon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlTableRight.add(btnXoaMon, BorderLayout.SOUTH);

        pnlRight.add(pnlTableRight, BorderLayout.CENTER);

        // Khu vực chốt phiếu
        JPanel pnlCheckout = new JPanel();
        pnlCheckout.setLayout(new BoxLayout(pnlCheckout, BoxLayout.Y_AXIS));
        pnlCheckout.setBackground(Color.WHITE);
        pnlCheckout.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(15, 20, 15, 20)));

        JPanel pnlLyDo = new JPanel(new BorderLayout(10, 0));
        pnlLyDo.setOpaque(false);
        pnlLyDo.add(new JLabel("Lý do trả tổng quát:"), BorderLayout.WEST);
        txtLyDo = new JTextField("Khách không ưng ý");
        pnlLyDo.add(txtLyDo, BorderLayout.CENTER);

        lblTongTienHoan = new JLabel("CẦN HOÀN TRẢ: 0 VNĐ");
        lblTongTienHoan.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongTienHoan.setForeground(COL_PRIMARY);


        JButton btnXacNhan = new JButton("XÁC NHẬN TRẢ HÀNG & HOÀN TIỀN");
        btnXacNhan.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnXacNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnXacNhan.setBackground(COL_PRIMARY);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXacNhan.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlCheckout.add(pnlLyDo);
        pnlCheckout.add(Box.createVerticalStrut(20));
        pnlCheckout.add(lblTongTienHoan);
        pnlCheckout.add(Box.createVerticalStrut(20));
        pnlCheckout.add(btnXacNhan);

        pnlRight.add(pnlCheckout, BorderLayout.SOUTH);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        // ==========================================
        // SỰ KIỆN (EVENTS)
        // ==========================================
        btnSearch.addActionListener(e -> timKiemHoaDon());

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

    // --- CÁC HÀM XỬ LÝ LOGIC ---

    private void timKiemHoaDon() {
        try {
            // Lấy chuỗi người dùng gõ
            String input = txtSearchHD.getText().trim();

            // XỬ LÝ CHUỖI: Nếu có chữ "HD" hoặc "hd" ở đầu thì cắt bỏ, chỉ giữ lại số
            if (input.toUpperCase().startsWith("HD")) {
                input = input.substring(2); // Cắt 2 ký tự đầu
            }

            int maHD = Integer.parseInt(input);
            currentHoaDon = hoaDonBUS.getHoaDonById(maHD);

            if (currentHoaDon == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy Hóa đơn mã: " + maHD);
                return;
            }
            if (currentHoaDon.getTrangThai() == enums.TrangThaiGiaoDich.DaHuy) {
                JOptionPane.showMessageDialog(this, "Hóa đơn này đã bị hủy toàn bộ, không thể đổi trả!");
                return;
            }

            lblThongTinHD.setText("Đang xử lý Hóa Đơn: HD" + String.format("%03d", maHD) + " - Tổng tiền gốc: " + String.format("%,.0f", currentHoaDon.getThanhTien()) + " VNĐ");

            // Lấy chi tiết hóa đơn
            listChiTietHD = hoaDonBUS.getChiTietByMaHD(maHD);
            modelHDCu.setRowCount(0);

            for (ChiTietHoaDonDTO ct : listChiTietHD) {
                modelHDCu.addRow(new Object[]{
                        "S" + String.format("%03d", ct.getMaSach()),
                        ct.getTenSach() != null ? ct.getTenSach() : "ID: " + ct.getMaSach(),
                        String.format("%,.0f", ct.getDonGia()),
                        ct.getSoLuong()
                });
            }

            // Reset giỏ trả hàng
            dsTraHang.clear();
            capNhatBangTraHang();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ! Vui lòng gõ số (VD: 1) hoặc mã (VD: HD001)");
        }
    }

    private void chonSachDeTra() {
        int row = tblHoaDonCu.getSelectedRow();
        if(row < 0) return;

        ChiTietHoaDonDTO ctGoc = listChiTietHD.get(row);

        // 1. Hỏi số lượng muốn trả
        String inputSL = JOptionPane.showInputDialog(this,
                "Nhập số lượng muốn trả (Tối đa " + ctGoc.getSoLuong() + "):", "1");
        if (inputSL == null || inputSL.trim().isEmpty()) return;

        try {
            int slTra = Integer.parseInt(inputSL);
            if (slTra <= 0 || slTra > ctGoc.getSoLuong()) {
                JOptionPane.showMessageDialog(this, "Số lượng trả không hợp lệ!");
                return;
            }

            // 2. Hỏi tình trạng sách
            String[] options = {"Lỗi NSX", "Nguyên vẹn (Khách đổi ý)"};
            String tinhTrang = (String) JOptionPane.showInputDialog(this,
                    "Tình trạng sách trả lại:", "Đánh giá tình trạng",
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (tinhTrang == null) return;

            // 3. Đưa vào giỏ trả hàng
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

            // GỌI HÀM IN PDF Ở ĐÂY SAU KHI LƯU THÀNH CÔNG
            if (ketQua.contains("Thành công")) {
                int inPhieu = JOptionPane.showConfirmDialog(this, "Bạn có muốn in biên lai hoàn tiền không?", "In Biên Lai", JOptionPane.YES_NO_OPTION);
                if (inPhieu == JOptionPane.YES_OPTION) {
                    xuatPhieuTraPDF(ptk, dsTraHang);
                }

                // Reset giao diện
                dsTraHang.clear();
                capNhatBangTraHang();
                modelHDCu.setRowCount(0);
                txtSearchHD.setText("");
                lblThongTinHD.setText("Chưa chọn hóa đơn");
                currentHoaDon = null;
            }
        }
    }

    // --- HÀM TÚT GIAO DIỆN ---
    private void styleButton(JButton btn, Color bgColor, int width) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 32));
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
            if (i != 1 && i != 0) { // Căn giữa các cột số
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private void xuatPhieuTraPDF(dto.PhieuTraKhachHangDTO ptk, java.util.List<dto.ChiTietTraKhachHangDTO> dsTra) {
        try {
            String path = "PhieuTraHang_HD" + ptk.getMaHD() + "_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // SỬ DỤNG ĐƯỜNG DẪN TUYỆT ĐỐI CHO FONT PDF
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

            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{4f, 1.5f, 2.5f, 2f});

            String[] headers = {"Tên Sách", "SL Trả", "Tình Trạng", "Tiền Hoàn"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            for (dto.ChiTietTraKhachHangDTO ct : dsTra) {
                pdfTable.addCell(new PdfPCell(new Phrase(ct.getTenSach(), fontNormal)));

                PdfPCell cellSL = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                cellSL.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cellSL);

                pdfTable.addCell(new PdfPCell(new Phrase(ct.getTinhTrangSach(), fontNormal)));

                PdfPCell cellTien = new PdfPCell(new Phrase(String.format("%,.0f", ct.getThanhTienHoan()), fontNormal));
                cellTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfTable.addCell(cellTien);
            }
            document.add(pdfTable);

            document.add(new Paragraph(" "));

            // Sửa luôn cả 2 cái Font nằm lồng bên trong này
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