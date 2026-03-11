package gui;

import bus.TheLoaiBUS;
import dto.TaiKhoanDTO;
import dto.TheLoaiDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TheLoaiGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    private TheLoaiBUS tlBUS = new TheLoaiBUS();
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private TaiKhoanDTO userLogin;

    public TheLoaiGUI(TaiKhoanDTO user) {
        this.userLogin = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(COL_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. THANH CÔNG CỤ (Tối ưu lại layout) ---
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setBackground(Color.WHITE);
        pnlToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Cụm tìm kiếm bên trái
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlSearch.setOpaque(false);

        JLabel lblTitle = new JLabel("THỂ LOẠI SÁCH");
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

        // Cụm nút bấm bên phải
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setOpaque(false);

        btnThem = new JButton("Thêm Mới");
        styleButton(btnThem, COL_PRIMARY);

        btnSua = new JButton("Chỉnh Sửa");
        styleButton(btnSua, COL_SIDEBAR);

        btnXoa = new JButton("Xóa");
        styleButton(btnXoa, new Color(231, 76, 60));

        if (userLogin.getMaQuyen() == 2) {
            btnThem.setVisible(false);
            btnSua.setVisible(false);
            btnXoa.setVisible(false);
        }

        pnlAction.add(btnThem);
        pnlAction.add(btnSua);
        pnlAction.add(btnXoa);

        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlAction, BorderLayout.EAST);
        add(pnlToolbar, BorderLayout.NORTH);

        String[] columns = {"Mã Thể Loại", "Tên Thể Loại", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
    }

    private void styleTable(JTable tbl) {
        tbl.setRowHeight(40);
        tbl.setFocusable(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(new Color(232, 240, 255));
        tbl.setSelectionForeground(Color.BLACK);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = tbl.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tbl.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        tbl.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    if (v.toString().equals("HOẠT ĐỘNG")) {
                        setText("HOẠT ĐỘNG");
                        setForeground(new Color(46, 204, 113));
                    } else {
                        setText("NGỪNG HOẠT ĐỘNG");
                        setForeground(new Color(231, 76, 60));
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                if (isS) comp.setForeground(t.getSelectionForeground());
                return comp;
            }
        });
    }

    // ==========================================
    // LOGIC DỮ LIỆU & SỰ KIỆN
    // ==========================================

    public void loadDataToTable() {
        model.setRowCount(0);
        List<TheLoaiDTO> ds = tlBUS.getAll();
        if (ds != null) {
            for (TheLoaiDTO tl : ds) {
                model.addRow(new Object[]{
                        "TL" + String.format("%03d", tl.getMaLoai()),
                        tl.getTenLoai(),
                        tl.getTrangThai()
                });
            }
        }
    }

    private void applyFilters() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        model.setRowCount(0);
        List<TheLoaiDTO> ds = tlBUS.getAll();
        if (ds != null) {
            for (TheLoaiDTO tl : ds) {
                String maStr = "tl" + String.format("%03d", tl.getMaLoai());
                if (maStr.contains(keyword) || tl.getTenLoai().toLowerCase().contains(keyword)) {
                    model.addRow(new Object[]{
                            "TL" + String.format("%03d", tl.getMaLoai()),
                            tl.getTenLoai(),
                            tl.getTrangThai()
                    });
                }
            }
        }
    }

    private void initEvents() {
        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilters();
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadDataToTable();
        });

        btnThem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Mở Dialog thêm Thể Loại!");
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần sửa!");
                return;
            }
            int id = Integer.parseInt(table.getValueAt(row, 0).toString().substring(2));
            JOptionPane.showMessageDialog(this, "Mở Dialog sửa Thể Loại ID: " + id);
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa thể loại này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(table.getValueAt(row, 0).toString().substring(2));
                loadDataToTable();
            }
        });
    }
}