package gui;

import bus.PhanQuyenBUS;
import dto.PhanQuyenDTO;
import dto.TaiKhoanDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class PhanQuyenGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private PhanQuyenBUS pqBUS = new PhanQuyenBUS();
    private DefaultTableModel modelPQ;
    private JTable tblPhanQuyen;
    private TableRowSorter<DefaultTableModel> sorterPQ;

    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnLamMoi;

    private TaiKhoanDTO currentUser;

    public PhanQuyenGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- TOOLBAR ---
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        // Bên trái: Ô tìm kiếm
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(300, 35));
        txtTimKiem.setToolTipText("Tìm theo Mã Code hoặc Tên Quyền...");

        btnLamMoi = createFlatButton("Làm Mới", COL_SIDEBAR);

        pnlLeft.add(new JLabel("Tìm kiếm:"));
        pnlLeft.add(txtTimKiem);
        pnlLeft.add(btnLamMoi);

        // Bên phải: Các nút chức năng
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);

        btnThem = createFlatButton("Thêm Nhóm Quyền", COL_PRIMARY);
        btnSua = createFlatButton("Sửa Quyền", COL_SIDEBAR);

        pnlRight.add(btnSua);
        pnlRight.add(btnThem);

        pnlToolbar.add(pnlLeft, BorderLayout.WEST);
        pnlToolbar.add(pnlRight, BorderLayout.EAST);

        // --- TABLE ---
        String[] columns = {"ID", "Mã Code", "Tên Nhóm Quyền", "Mô Tả"};
        modelPQ = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPhanQuyen = new JTable(modelPQ);
        styleTable(tblPhanQuyen);

        sorterPQ = new TableRowSorter<>(modelPQ);
        tblPhanQuyen.setRowSorter(sorterPQ);

        add(pnlToolbar, BorderLayout.NORTH);
        add(new JScrollPane(tblPhanQuyen), BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        modelPQ.setRowCount(0);
        List<PhanQuyenDTO> ds = pqBUS.getAll();
        if (ds != null) {
            for (PhanQuyenDTO pq : ds) {
                modelPQ.addRow(new Object[]{
                        // Format ID cho chuyên nghiệp: PQ001
                        String.format("PQ%03d", pq.getMaQuyen()),
                        pq.getMaCode(),
                        pq.getTenQuyen(),
                        pq.getMoTa()
                });
            }
        }
    }

    private void initEvents() {
        // Tìm kiếm tức thời (Live Search)
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        // Nút làm mới
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadDataToTable();
        });

        // Nút thêm mới
        btnThem.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            new PhanQuyenDialog(owner, null, pqBUS).setVisible(true);
            loadDataToTable();
        });

        // Nút sửa
        btnSua.addActionListener(e -> {
            int row = tblPhanQuyen.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhóm quyền từ bảng!");
                return;
            }
            int modelRow = tblPhanQuyen.convertRowIndexToModel(row);

            // Logic lấy ID thật từ chuỗi format PQ001
            String idFormatted = modelPQ.getValueAt(modelRow, 0).toString();
            int id = Integer.parseInt(idFormatted.substring(2));

            // Ngăn chặn sửa quyền ADMIN tối cao nếu cần thiết (Tùy logic đồ án của ông)
            String maCode = modelPQ.getValueAt(modelRow, 1).toString();
            if (maCode.equalsIgnoreCase("ADMIN")) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Đây là nhóm quyền hệ thống tối cao. Bạn có chắc chắn muốn sửa không?",
                        "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            PhanQuyenDTO selected = pqBUS.getById(id);
            if (selected != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                new PhanQuyenDialog(owner, selected, pqBUS).setVisible(true);
                loadDataToTable();
            }
        });
    }

    private void filter() {
        String k = txtTimKiem.getText().trim();
        if (k.isEmpty()) {
            sorterPQ.setRowFilter(null);
        } else {
            // Chống crash regex và tìm theo cột Mã Code(1) và Tên Nhóm Quyền(2)
            sorterPQ.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(k), 1, 2));
        }
    }

    private JButton createFlatButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Margin tự động giãn theo chữ
        btn.setMargin(new Insets(8, 15, 8, 15));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(0, 40));
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(new Color(245, 245, 250));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        // Căn giữa cột ID và Mã Code
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);

        // Căn lề trái cột Tên và Mô tả cho dễ đọc
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(JLabel.LEFT);
        left.setBorder(new EmptyBorder(0, 10, 0, 0));
        table.getColumnModel().getColumn(2).setCellRenderer(left);
        table.getColumnModel().getColumn(3).setCellRenderer(left);
    }
}