package gui;

import dto.TaiKhoanDTO;
import dto.PhieuNhapDTO;
import bus.PhieuNhapBUS;
import enums.TrangThaiGiaoDich;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class PhieuNhapGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTable tblPhieuNhap;
    private DefaultTableModel modelPhieuNhap;
    private JTextField txtTimKiem;
    private JComboBox<String> cbxTrangThai;
    private JButton btnLoc, btnXemChiTiet, btnTaoPhieu, btnHuyPhieu;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);
    final Color COL_WHITE = Color.WHITE;

    public PhieuNhapGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBackground(COL_BG_MAIN);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setBackground(COL_WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));

        pnlFilter.add(new JLabel("Tìm kiếm (Mã PN):"));
        txtTimKiem = new JTextField(15);
        txtTimKiem.setPreferredSize(new Dimension(150, 32));
        pnlFilter.add(txtTimKiem);

        pnlFilter.add(new JLabel("Trạng thái:"));
        // Cập nhật text hiển thị trong ComboBox cho đồng bộ
        cbxTrangThai = new JComboBox<>(new String[]{"Tất cả", "HOÀN THÀNH", "CHỜ XỬ LÝ", "ĐÃ HỦY"});
        cbxTrangThai.setPreferredSize(new Dimension(130, 32));
        pnlFilter.add(cbxTrangThai);

        btnLoc = new JButton("Lọc");
        styleButton(btnLoc, COL_SIDEBAR);
        btnLoc.setPreferredSize(new Dimension(90, 32));
        pnlFilter.add(btnLoc);

        pnlTop.add(pnlFilter, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlActions.setBackground(COL_BG_MAIN);

        btnHuyPhieu = new JButton("Hủy Phiếu");
        styleButton(btnHuyPhieu, new Color(231, 76, 60));

        btnXemChiTiet = new JButton("Xem Chi Tiết");
        styleButton(btnXemChiTiet, COL_SIDEBAR);

        btnTaoPhieu = new JButton("+ Tạo Phiếu Nhập");
        styleButton(btnTaoPhieu, new Color(46, 204, 113));
        btnTaoPhieu.setPreferredSize(new Dimension(145, 32));

        pnlActions.add(btnHuyPhieu);
        pnlActions.add(btnXemChiTiet);
        pnlActions.add(btnTaoPhieu);

        pnlTop.add(pnlActions, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Mã PN", "Nhà Cung Cấp", "Nhân Viên", "Tổng Tiền", "Ngày Tạo", "Trạng Thái"};
        modelPhieuNhap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPhieuNhap = new JTable(modelPhieuNhap);
        styleTable(tblPhieuNhap);

        add(new JScrollPane(tblPhieuNhap), BorderLayout.CENTER);
    }

    // HÀM DỊCH TỪ ENUM SANG TIẾNG VIỆT ĐỂ HIỆN LÊN BẢNG
    private String parseTrangThai(TrangThaiGiaoDich tt) {
        if (tt == null) return "";
        switch (tt) {
            case HoanThanh: return "HOÀN THÀNH";
            case ChoXuLy: return "CHỜ XỬ LÝ";
            case DaHuy: return "ĐÃ HỦY";
            default: return tt.name();
        }
    }

    public void loadDataToTable() {
        modelPhieuNhap.setRowCount(0);
        List<PhieuNhapDTO> list = pnBUS.getAll();
        if (list != null) {
            for (PhieuNhapDTO pn : list) {
                modelPhieuNhap.addRow(new Object[]{
                        "PN" + String.format("%03d", pn.getMaPN()),
                        pn.getTenNCC(),
                        pn.getTenNV(),
                        df.format(pn.getTongTien()),
                        pn.getNgayTao() != null ? pn.getNgayTao().format(dtf) : "",
                        parseTrangThai(pn.getTrangThai()) // Gọi hàm dịch ở đây
                });
            }
        }
    }

    private void initEvents() {
        btnLoc.addActionListener(e -> {
            String search = txtTimKiem.getText().trim().toLowerCase();
            String status = cbxTrangThai.getSelectedItem().toString();
            JOptionPane.showMessageDialog(this, "Đang lọc dữ liệu theo: " + search + " - " + status);
        });

        btnTaoPhieu.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            TaoPhieuNhapDialog dialog = new TaoPhieuNhapDialog(owner, currentUser);
            dialog.setVisible(true);
            loadDataToTable();
        });

        btnXemChiTiet.addActionListener(e -> {
            int row = tblPhieuNhap.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phiếu nhập!");
                return;
            }
            int maPN = Integer.parseInt(tblPhieuNhap.getValueAt(row, 0).toString().substring(2));
            JOptionPane.showMessageDialog(this, "Mở ChiTietPhieuNhapDialog cho ID: " + maPN);
        });
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(COL_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(115, 32));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // RENDERER MÀU TRẠNG THÁI THEO TIẾNG VIỆT
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isSel, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    String status = v.toString();
                    if (status.equals("HOÀN THÀNH")) {
                        comp.setForeground(new Color(46, 204, 113)); // Xanh lá
                    } else if (status.equals("ĐÃ HỦY")) {
                        comp.setForeground(new Color(231, 76, 60)); // Đỏ
                    } else if (status.equals("CHỜ XỬ LÝ")) {
                        comp.setForeground(new Color(243, 156, 18)); // Cam
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                if (isSel) comp.setForeground(t.getSelectionForeground());
                return comp;
            }
        });
    }
}