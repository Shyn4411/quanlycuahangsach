package gui;

import bus.PhieuNhapBUS; // Đổi sang dùng BUS cho đúng chuẩn 3 lớp
import bus.SachBUS;
import dto.ChiTietPhieuNhapDTO;
import dto.PhieuNhapDTO;
import dto.SachDTO;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ChiTietPhieuNhapDialog extends JDialog {

    private PhieuNhapDTO phieuNhap;
    // Sử dụng BUS thay vì gọi trực tiếp DAO
    private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
    private SachBUS sachBUS = new SachBUS();

    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    public ChiTietPhieuNhapDialog(Window owner, PhieuNhapDTO pn) {
        super(owner, "CHI TIẾT PHIẾU NHẬP - PN" + String.format("%03d", pn.getMaPN()), ModalityType.APPLICATION_MODAL);
        this.phieuNhap = pn;
        initUI();
        loadData();

        setSize(850, 600); // Tăng chiều rộng một chút cho thoáng
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().setBackground(Color.WHITE);

        // --- 1. THÔNG TIN CHUNG (TOP) ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 2, 20, 15));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                " Thông tin chung ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                COL_SIDEBAR));

        pnlHeader.add(createLabel("Mã phiếu: ", "PN" + String.format("%03d", phieuNhap.getMaPN())));
        pnlHeader.add(createLabel("Nhà cung cấp: ", phieuNhap.getTenNCC()));
        pnlHeader.add(createLabel("Nhân viên: ", phieuNhap.getTenNV()));
        pnlHeader.add(createLabel("Ngày tạo: ", (phieuNhap.getNgayTao() != null ? phieuNhap.getNgayTao().format(dtf) : "N/A")));

        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. BẢNG CHI TIẾT SÁCH (CENTER) ---
        String[] cols = {"Mã Sách", "Tên Sách", "Số Lượng", "Giá Nhập", "Thành Tiền"};
        modelChiTiet = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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

        JLabel lblTongTien = new JLabel("TỔNG TIỀN: " + df.format(phieuNhap.getTongTien()));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongTien.setForeground(COL_PRIMARY);
        pnlBottom.add(lblTongTien, BorderLayout.WEST);

        JButton btnDong = new JButton("Đóng cửa sổ");
        btnDong.setBackground(COL_SIDEBAR);
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDong.setFocusPainted(false);
        btnDong.setBorderPainted(false);
        btnDong.setOpaque(true);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDong.setPreferredSize(new Dimension(150, 40));
        btnDong.addActionListener(e -> dispose());
        pnlBottom.add(btnDong, BorderLayout.EAST);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + "</b> " + value + "</html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

    private void loadData() {
        modelChiTiet.setRowCount(0);
        // GỌI QUA BUS CHO ĐÚNG CHUẨN (Nhớ thêm hàm này vào PhieuNhapBUS nhé Tủn)
        List<ChiTietPhieuNhapDTO> list = pnBUS.getChiTietByMaPN(phieuNhap.getMaPN());

        if (list != null) {
            for (ChiTietPhieuNhapDTO ct : list) {
                SachDTO s = sachBUS.getById(ct.getMaSach());
                String tenSach = (s != null) ? s.getTenSach() : "N/A";

                modelChiTiet.addRow(new Object[]{
                        ct.getMaSach(),
                        tenSach,
                        ct.getSoLuong(),
                        df.format(ct.getGiaNhap()),
                        df.format(ct.getThanhTien())
                });
            }
        }
    }
}