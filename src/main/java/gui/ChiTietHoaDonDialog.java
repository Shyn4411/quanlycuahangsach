package gui;

import dto.ChiTietHoaDonDTO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

// Import thư viện iTextPDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class ChiTietHoaDonDialog extends JDialog {

    private String maHD;
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    private JButton btnInPDF, btnDong;
    private JLabel lblTongTien; // Thêm Label hiển thị tổng tiền

    // Khai báo BUS và Format tiền tệ
    private bus.HoaDonBUS hoaDonBUS = new bus.HoaDonBUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    public ChiTietHoaDonDialog(Frame owner, String maHD) {
        super(owner, "CHI TIẾT HÓA ĐƠN - " + maHD, true);
        this.maHD = maHD;
        initUI();
        loadChiTietHoaDon();
        initEvents();
    }

    private void initUI() {
        setSize(850, 600); // Kích thước bằng với Dialog Phiếu Nhập
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().setBackground(Color.WHITE);

        // --- 1. THÔNG TIN CHUNG (TOP) ---
        // Giả lập Panel thông tin chung cho giống Phiếu Nhập
        JPanel pnlHeader = new JPanel(new GridLayout(2, 2, 20, 15));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                " Thông tin chung ",
                0, 0,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13),
                COL_SIDEBAR));

        // Tạm thời hiển thị mã hóa đơn, ngày in. Nếu Tủn có HoaDonDTO thì bổ sung thêm Nhân Viên, Khách Hàng vào đây
        pnlHeader.add(createLabel("Mã hóa đơn: ", maHD));
        pnlHeader.add(createLabel("Ngày in: ", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        pnlHeader.add(createLabel("Nhân viên: ", "N/A")); // Thay thế bằng dữ liệu thật
        pnlHeader.add(createLabel("Khách hàng: ", "N/A")); // Thay thế bằng dữ liệu thật

        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. BẢNG CHI TIẾT SÁCH (CENTER) ---
        String[] cols = {"Mã Sách", "Tên Sách", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        modelChiTiet = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);
        styleTable(tblChiTiet);

        JScrollPane scrollPane = new JScrollPane(tblChiTiet);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. TỔNG TIỀN & NÚT ĐÓNG (SOUTH) ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblTongTien = new JLabel("TỔNG TIỀN: 0 VNĐ");
        lblTongTien.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        lblTongTien.setForeground(COL_PRIMARY);
        pnlBottom.add(lblTongTien, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        btnInPDF = new JButton("In Hóa Đơn PDF");
        btnInPDF.setBackground(new Color(46, 204, 113));
        btnInPDF.setForeground(Color.WHITE);
        btnInPDF.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnInPDF.setPreferredSize(new Dimension(150, 40));
        btnInPDF.setFocusPainted(false);
        btnInPDF.setBorderPainted(false);
        btnInPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDong = new JButton("Đóng cửa sổ");
        btnDong.setBackground(COL_SIDEBAR);
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnDong.setPreferredSize(new Dimension(130, 40));
        btnDong.setFocusPainted(false);
        btnDong.setBorderPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlActions.add(btnInPDF);
        pnlActions.add(btnDong);

        pnlBottom.add(pnlActions, BorderLayout.EAST);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + "</b> " + value + "</html>");
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        return label;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        header.setDefaultRenderer(headerRenderer);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        // Căn giữa tất cả trừ cột Tên Sách
        for(int i=0; i<table.getColumnCount(); i++) {
            if(i != 1) table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        table.getColumnModel().getColumn(1).setPreferredWidth(250);
    }

    // ==========================================
    // HÀM NẠP DỮ LIỆU TỪ DATABASE
    // ==========================================
    private void loadChiTietHoaDon() {
        modelChiTiet.setRowCount(0);
        double tongTienHD = 0; // Biến tính tổng tiền

        try {
            // Lọc lấy số để truyền xuống BUS (VD: "HD001" -> 1)
            String soHD = maHD.replaceAll("[^0-9]", "");
            if (soHD.isEmpty()) return;

            int idHD = Integer.parseInt(soHD);

            // Đã gọi hàm getChiTietByMaHD từ hoaDonBUS
            List<ChiTietHoaDonDTO> list = hoaDonBUS.getChiTietByMaHD(idHD);

            if (list != null) {
                for (ChiTietHoaDonDTO ct : list) {
                    tongTienHD += ct.getThanhTien().doubleValue();

                    modelChiTiet.addRow(new Object[]{
                            "S" + String.format("%03d", ct.getMaSach()),
                            ct.getTenSach() != null ? ct.getTenSach() : "Không tìm thấy", // Hiển thị tên sách
                            ct.getSoLuong(),
                            df.format(ct.getDonGia()),
                            df.format(ct.getThanhTien())
                    });
                }
            }
            lblTongTien.setText("TỔNG TIỀN: " + df.format(tongTienHD));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu chi tiết: " + e.getMessage());
        }
    }

    private void initEvents() {
        btnDong.addActionListener(e -> dispose());

        btnInPDF.addActionListener(e -> {
            if(tblChiTiet.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu để in!");
                return;
            }
            xuatHoaDonPDF(maHD);
        });
    }

    private void xuatHoaDonPDF(String maHD) {
        try {
            String path = "HoaDon_" + maHD + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(bf, 20, com.itextpdf.text.Font.BOLD, BaseColor.BLUE);
            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);

            Paragraph title = new Paragraph("BOOKSTORE MANAGEMENT", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("HÓA ĐƠN MUA HÀNG", new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD)));
            document.add(new Paragraph("Mã hóa đơn: " + maHD, fontNormal));
            document.add(new Paragraph("Ngày in: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontNormal));
            document.add(new Paragraph("---------------------------------------------------------", fontNormal));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(tblChiTiet.getColumnCount());
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1.5f, 4f, 1.5f, 2f, 2f});

            for (int i = 0; i < tblChiTiet.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(tblChiTiet.getColumnName(i), fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            double tongTien = 0;
            for (int rows = 0; rows < tblChiTiet.getRowCount(); rows++) {
                for (int cols = 0; cols < tblChiTiet.getColumnCount(); cols++) {
                    String val = tblChiTiet.getModel().getValueAt(rows, cols).toString();
                    PdfPCell cell = new PdfPCell(new Phrase(val, fontNormal));
                    cell.setPadding(5);
                    pdfTable.addCell(cell);

                    if(cols == tblChiTiet.getColumnCount() - 1) {
                        try {
                            tongTien += Double.parseDouble(val.replace(" VNĐ", "").replace(",", ""));
                        } catch(Exception ex) {}
                    }
                }
            }
            document.add(pdfTable);

            document.add(new Paragraph(" "));
            Paragraph tongTienPara = new Paragraph("TỔNG CỘNG: " + String.format("%,.0f VNĐ", tongTien), new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD, BaseColor.RED));
            tongTienPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(tongTienPara);

            document.add(new Paragraph(" "));
            Paragraph thankyou = new Paragraph("Cảm ơn quý khách và hẹn gặp lại!", new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.ITALIC));
            thankyou.setAlignment(Element.ALIGN_CENTER);
            document.add(thankyou);

            document.close();

            JOptionPane.showMessageDialog(this, "Xuất hóa đơn thành công!\nFile được lưu tại: " + path);
            Desktop.getDesktop().open(new java.io.File(path));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn: " + ex.getMessage());
        }
    }
}