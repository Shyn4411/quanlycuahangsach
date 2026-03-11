package gui;

import bus.KhuyenMaiBUS;
import dto.KhuyenMaiDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiKhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class KhuyenMaiGUI extends JPanel {

    private final java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private KhuyenMaiBUS kmBUS = new KhuyenMaiBUS();
    private List<KhuyenMaiDTO> dsKhuyenMai;

    private JTable tblKM;
    private DefaultTableModel modelKM;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    private final Color CLR_BG_MAIN = new Color(248, 244, 236);
    private final Color CLR_SIDEBAR = new Color(67, 51, 76);
    private final Color CLR_ACTIVE  = new Color(232, 60, 145);
    private final Color CLR_WHITE   = Color.WHITE;

    private TaiKhoanDTO currentUser;

    public KhuyenMaiGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(CLR_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ==================== THANH CÔNG CỤ (TOP) ====================
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        // Ô tìm kiếm (Trái)
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSearch.setOpaque(false);
        pnlSearch.add(new JLabel("Tìm kiếm:  "));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5)));
        pnlSearch.add(txtSearch);
        pnlToolbar.add(pnlSearch, BorderLayout.WEST);

        // Các nút hành động (Phải)
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);
        JButton btnAdd = new JButton("Thêm Mới"); styleButton(btnAdd, CLR_ACTIVE, "/gui/icons/plus.png");
        JButton btnView = new JButton("Xem chi tiết / Sửa"); styleButton(btnView, CLR_SIDEBAR, "/gui/icons/view.png");
        JButton btnDelete = new JButton("Ngừng Khuyến Mãi"); styleButton(btnDelete, Color.RED, "/gui/icons/delete.png");

        pnlActions.add(btnDelete);
        pnlActions.add(btnView);
        pnlActions.add(btnAdd);
        pnlToolbar.add(pnlActions, BorderLayout.EAST);

        // ==================== BẢNG DỮ LIỆU (CENTER) ====================
        String[] cols = {"Mã ID", "Mã Code", "Tên Chương Trình", "% Giảm", "Tiền Giảm", "Đơn Tối Thiểu", "Hạn Cuối", "Trạng Thái"};
        modelKM = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKM = new JTable(modelKM);
        styleTable(tblKM);

        sorter = new TableRowSorter<>(modelKM);
        tblKM.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tblKM);
        scrollPane.getViewport().setBackground(CLR_WHITE);
        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));

        add(pnlToolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ==================== EVENTS ====================
        btnAdd.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            new KhuyenMaiDialog(owner, this, null, kmBUS).setVisible(true);
        });

        btnView.addActionListener(e -> {
            int row = tblKM.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khuyến mãi trên bảng để xem!"); return; }
            int modelRow = tblKM.convertRowIndexToModel(row);
            KhuyenMaiDTO km = dsKhuyenMai.get(modelRow);

            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            new KhuyenMaiDialog(owner, this, km, kmBUS).setVisible(true);
        });

        btnDelete.addActionListener(e -> handlingDelete());

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            private void filterTable() {
                String text = txtSearch.getText().trim();
                if (text.length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
    }

    public void loadData() {
        modelKM.setRowCount(0);
        dsKhuyenMai = kmBUS.getAll();
        for (KhuyenMaiDTO km : dsKhuyenMai) {
            modelKM.addRow(new Object[]{
                    km.getMaKM(),
                    km.getMaCode(),
                    km.getTenKM(),
                    km.getPhanTramGiam().intValue() + "%",
                    String.format("%,.0f đ", km.getSoTienGiam()),
                    String.format("%,.0f đ", km.getDonHangToiThieu()),
                    km.getNgayKetThuc().format(dateFormatter),
                    km.getTrangThai()
            });
        }
    }

    private void handlingDelete() {
        int row = tblKM.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa/ngừng!"); return; }

        int modelRow = tblKM.convertRowIndexToModel(row);
        KhuyenMaiDTO km = dsKhuyenMai.get(modelRow);

        if (km.getTrangThai() == TrangThaiKhuyenMai.HET_HAN) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi này đã ngừng hoạt động từ trước rồi!"); return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn NGỪNG khuyến mãi: " + km.getTenKM() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            km.setTrangThai(TrangThaiKhuyenMai.HET_HAN);
            kmBUS.updateKhuyenMai(km);
            JOptionPane.showMessageDialog(this, "Đã ngừng khuyến mãi thành công!");
            loadData();
        }
    }

    private void styleButton(JButton btn, Color bg, String iconPath) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(160, 35));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img)); btn.setIconTextGap(8);
        } catch (Exception ignored) {}
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            if (i != 2) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250)); header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14)); header.setPreferredSize(new Dimension(100, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
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
                        setText("HẾT HẠN");
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        });
    }
}