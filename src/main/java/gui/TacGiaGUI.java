package gui;

import bus.TacGiaBUS;
import dto.TacGiaDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiCoBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TacGiaGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    private TacGiaBUS tgBUS = new TacGiaBUS();
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private TaiKhoanDTO userLogin;

    public TacGiaGUI(TaiKhoanDTO user) {
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
        JLabel lblTitle = new JLabel("QUẢN LÝ TÁC GIẢ");
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
        btnSua = new JButton("Chỉnh Sửa");
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

        String[] columns = {"Mã Tác Giả", "Tên Tác Giả", "Trạng Thái"};
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
        tbl.setSelectionForeground(Color.BLACK);

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
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    if (value.toString().equals("HOẠT ĐỘNG")) {
                        c.setForeground(new Color(46, 204, 113));
                        setText("HOẠT ĐỘNG");
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(231, 76, 60));
                        setText("NGỪNG HOẠT ĐỘNG");
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        });

        tbl.getColumnModel().getColumn(1).setPreferredWidth(300); // Tên tác giả cho rộng ra
    }

    public void loadDataToTable() {
        model.setRowCount(0);
        List<TacGiaDTO> ds = tgBUS.getAll();
        if (ds != null) {
            for (TacGiaDTO tg : ds) {
                model.addRow(new Object[]{
                        "TG" + String.format("%03d", tg.getMaTacGia()),
                        tg.getTenTacGia(),
                        tg.getTrangThai()
                });
            }
        }
    }

    private void initEvents() {
        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String key = txtTimKiem.getText().trim().toLowerCase();
                model.setRowCount(0);
                for (TacGiaDTO tg : tgBUS.getAll()) {
                    if (tg.getTenTacGia().toLowerCase().contains(key) ||
                            ("tg" + String.format("%03d", tg.getMaTacGia())).contains(key)) {
                        model.addRow(new Object[]{"TG" + String.format("%03d", tg.getMaTacGia()), tg.getTenTacGia(), tg.getTrangThai()});
                    }
                }
            }
        });

        btnLamMoi.addActionListener(e -> { txtTimKiem.setText(""); loadDataToTable(); });

        btnThem.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            TacGiaDialog dialog = new TacGiaDialog(owner, null);
            dialog.setVisible(true);
            loadDataToTable();
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả cần sửa!");
                return;
            }
            int id = Integer.parseInt(table.getValueAt(row, 0).toString().substring(2));
            TacGiaDTO tgSelect = tgBUS.getById(id);

            if (tgSelect != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                TacGiaDialog dialog = new TacGiaDialog(owner, tgSelect);
                dialog.setVisible(true);
                loadDataToTable();
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Chuyển sang Ngừng hoạt động?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(table.getValueAt(row, 0).toString().substring(2));
                JOptionPane.showMessageDialog(this, tgBUS.deleteTacGia(id));
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