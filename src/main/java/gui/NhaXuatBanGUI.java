package gui;

import bus.NhaXuatBanBUS;
import dto.NhaXuatBanDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiCoBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class NhaXuatBanGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    private bus.NhaXuatBanBUS nxbBUS = new bus.NhaXuatBanBUS();
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private TaiKhoanDTO userLogin;

    public NhaXuatBanGUI(TaiKhoanDTO user) {
        this.userLogin = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(COL_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. THANH CÔNG CỤ ---
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setBackground(Color.WHITE);
        pnlToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlSearch.setOpaque(false);
        JLabel lblTitle = new JLabel("NHÀ XUẤT BẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COL_SIDEBAR);
        pnlSearch.add(lblTitle);

        pnlSearch.add(new JLabel("|  Tìm kiếm:"));
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(200, 32));
        pnlSearch.add(txtTimKiem);

        btnLamMoi = new JButton("Làm Mới");
        styleButton(btnLamMoi, new Color(46, 204, 113));
        pnlSearch.add(btnLamMoi);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setOpaque(false);
        btnThem = new JButton("Thêm Mới");
        styleButton(btnThem, COL_PRIMARY);
        btnSua = new JButton("Sửa");
        styleButton(btnSua, COL_SIDEBAR);
        btnXoa = new JButton("Xóa");
        styleButton(btnXoa, new Color(231, 76, 60));

        if (userLogin.getMaQuyen() == 2) {
            btnThem.setVisible(false); btnSua.setVisible(false); btnXoa.setVisible(false);
        }

        pnlAction.add(btnThem); pnlAction.add(btnSua); pnlAction.add(btnXoa);
        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlAction, BorderLayout.EAST);
        add(pnlToolbar, BorderLayout.NORTH);

        // --- 2. BẢNG DỮ LIỆU (Đã rút gọn còn 3 cột) ---
        String[] columns = {"Mã NXB", "Tên Nhà Xuất Bản", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleTable(JTable tbl) {
        tbl.setRowHeight(40);
        tbl.setSelectionBackground(new Color(232, 240, 255));
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // --- STYLE HEADER ---
        JTableHeader header = tbl.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // --- CĂN GIỮA CÁC CỘT ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tbl.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã NXB

        // --- TÔ MÀU TRẠNG THÁI (Cột 2) ---
        tbl.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    if (value.toString().equals("HoatDong")) {
                        c.setForeground(new Color(46, 204, 113));
                        setText("Hoạt Động");
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(231, 76, 60));
                        setText("Ngừng Hoạt Động");
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        });

        tbl.getColumnModel().getColumn(1).setPreferredWidth(350); // Tên NXB rộng rãi
    }

    public void loadDataToTable() {
        model.setRowCount(0);
        List<NhaXuatBanDTO> ds = nxbBUS.getAll();
        if (ds != null) {
            for (NhaXuatBanDTO nxb : ds) {
                model.addRow(new Object[]{
                        "NXB" + String.format("%03d", nxb.getMaNXB()),
                        nxb.getTenNXB(),
                        nxb.getTrangThai()
                });
            }
        }
    }

    private void initEvents() {
        // Tìm kiếm nhanh (Đã bỏ lọc theo SDT)
        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String key = txtTimKiem.getText().trim().toLowerCase();
                model.setRowCount(0);
                for (NhaXuatBanDTO nxb : nxbBUS.getAll()) {
                    String maStr = "nxb" + String.format("%03d", nxb.getMaNXB());
                    if (nxb.getTenNXB().toLowerCase().contains(key) || maStr.contains(key)) {
                        model.addRow(new Object[]{
                                "NXB" + String.format("%03d", nxb.getMaNXB()),
                                nxb.getTenNXB(),
                                nxb.getTrangThai()
                        });
                    }
                }
            }
        });

        btnLamMoi.addActionListener(e -> { txtTimKiem.setText(""); loadDataToTable(); });

        btnThem.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            NhaXuatBanDialog dialog = new NhaXuatBanDialog(owner, null);
            dialog.setVisible(true);
            loadDataToTable();
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn NXB cần sửa!"); return; }
            int id = Integer.parseInt(table.getValueAt(row, 0).toString().substring(3));
            NhaXuatBanDTO nxb = nxbBUS.getById(id);
            if (nxb != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                NhaXuatBanDialog dialog = new NhaXuatBanDialog(owner, nxb);
                dialog.setVisible(true);
                loadDataToTable();
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Ngừng hoạt động NXB này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(table.getValueAt(row, 0).toString().substring(3));
                JOptionPane.showMessageDialog(this, nxbBUS.deleteNhaXuatBan(id));
                loadDataToTable();
            }
        });
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
    }
}