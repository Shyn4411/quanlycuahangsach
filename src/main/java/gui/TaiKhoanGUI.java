package gui;

import bus.TaiKhoanBUS; // Nhớ tạo BUS này nhé
import dto.TaiKhoanDTO;
import enums.Role;
import enums.TrangThaiTaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private TaiKhoanBUS tkBUS = new TaiKhoanBUS();
    private DefaultTableModel modelTK;
    private JTable tblTaiKhoan;
    private TableRowSorter<DefaultTableModel> sorterTK;

    private JTextField txtTimKiem;
    private JComboBox<String> cbxLocQuyen, cbxLocTrangThai;
    private JButton btnKhoa, btnLamMoi, btnDoiMatKhau;

    private TaiKhoanDTO currentUser;
    public TaiKhoanGUI(TaiKhoanDTO user) {
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

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setOpaque(false);

        cbxLocTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "HOẠT ĐỘNG", "KHOA"});
        cbxLocTrangThai.setPreferredSize(new Dimension(150, 35));

        cbxLocQuyen = new JComboBox<>(new String[]{"Tất cả chức vụ", "Quản trị viên", "Nhân viên bán hàng", "Nhân viên kho", "Khách hàng"});
        cbxLocQuyen.setPreferredSize(new Dimension(160, 35));

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(180, 35));
        txtTimKiem.setToolTipText("Tìm theo tên đăng nhập...");

        btnLamMoi = createFlatButton("Làm Mới", COL_SIDEBAR);

        pnlSearch.add(cbxLocTrangThai);
        pnlSearch.add(cbxLocQuyen);
        pnlSearch.add(new JLabel("Tìm kiếm:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnLamMoi);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setOpaque(false);

        btnDoiMatKhau = createFlatButton("Đổi Mật Khẩu", new Color(243, 156, 18));
        btnKhoa = createFlatButton("Khóa/Mở Khóa", new Color(192, 57, 43));

        pnlAction.add(btnDoiMatKhau);
        pnlAction.add(btnKhoa);

        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlAction, BorderLayout.EAST);

        // --- TABLE ---
        String[] columns = {"ID", "Tên Đăng Nhập", "Chức Vụ", "Ngày Tạo", "Cập Nhật Cuối", "Trạng Thái"};
        modelTK = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTaiKhoan = new JTable(modelTK);
        styleTable(tblTaiKhoan);

        sorterTK = new TableRowSorter<>(modelTK);
        tblTaiKhoan.setRowSorter(sorterTK);

        add(pnlToolbar, BorderLayout.NORTH);
        add(new JScrollPane(tblTaiKhoan), BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        modelTK.setRowCount(0);
        List<TaiKhoanDTO> ds = tkBUS.getAll();
        if (ds != null) {
            for (TaiKhoanDTO tk : ds) {
                String tenChucVu = Role.fromRole(tk.getMaQuyen()).getTenChucVu();
                String customID = String.format("TK%03d", tk.getMaTaiKhoan());
                modelTK.addRow(new Object[]{
                        customID,
                        tk.getTenDangNhap(),
                        tenChucVu,
                        tk.getNgayTao(),
                        tk.getUpdatedAt(),
                        tk.getTrangThai().toString()
                });
            }
        }
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // 1. Tìm theo Username
        String keyword = txtTimKiem.getText().trim();
        if (!keyword.isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + keyword, 1));

        // 2. Lọc Trạng thái
        int idxTT = cbxLocTrangThai.getSelectedIndex();
        if (idxTT == 1) filters.add(RowFilter.regexFilter("^HOẠT ĐỘNG$", 5));
        if (idxTT == 2) filters.add(RowFilter.regexFilter("^KHOA$", 5));

        // 3. Lọc Quyền
        int idxQ = cbxLocQuyen.getSelectedIndex();
        if (idxQ > 0) filters.add(RowFilter.regexFilter("^" + cbxLocQuyen.getSelectedItem().toString() + "$", 2));

        sorterTK.setRowFilter(RowFilter.andFilter(filters));
    }

    private void initEvents() {
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        cbxLocTrangThai.addActionListener(e -> applyFilters());
        cbxLocQuyen.addActionListener(e -> applyFilters());
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbxLocTrangThai.setSelectedIndex(0);
            cbxLocQuyen.setSelectedIndex(0);
            loadDataToTable();
        });

        btnKhoa.addActionListener(e -> {
            int row = tblTaiKhoan.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản!");
                return;
            }
            int modelRow = tblTaiKhoan.convertRowIndexToModel(row);

            // --- FIX LỖI ÉP KIỂU Ở ĐÂY ---
            // Lấy chuỗi "TK001", cắt bỏ "TK" và đổi sang số int
            String idStr = modelTK.getValueAt(modelRow, 0).toString();
            int maTK = Integer.parseInt(idStr.substring(2));

            // Kiểm tra không cho tự khóa bản thân
            if (maTK == currentUser.getMaTaiKhoan()) {
                JOptionPane.showMessageDialog(this, "Bạn không thể tự khóa tài khoản của chính mình!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String currentTT = modelTK.getValueAt(modelRow, 5).toString();
            TrangThaiTaiKhoan moi = currentTT.equals("HOẠT ĐỘNG") ? TrangThaiTaiKhoan.KHOA : TrangThaiTaiKhoan.HOAT_DONG;

            String message = moi == TrangThaiTaiKhoan.KHOA ? "Xác nhận KHÓA tài khoản này?" : "Xác nhận MỞ KHÓA tài khoản này?";
            if (JOptionPane.showConfirmDialog(this, message, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, tkBUS.updateTrangThai(maTK, moi));
                loadDataToTable();
            }
        });

        btnDoiMatKhau.addActionListener(e -> {
            int row = tblTaiKhoan.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản!");
                return;
            }
            int modelRow = tblTaiKhoan.convertRowIndexToModel(row);

            // --- FIX LỖI ÉP KIỂU Ở ĐÂY ---
            String idStr = modelTK.getValueAt(modelRow, 0).toString();
            int maTK = Integer.parseInt(idStr.substring(2));

            String user = modelTK.getValueAt(modelRow, 1).toString();

            String newPass = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới cho [" + user + "]:");
            if (newPass != null && !newPass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, tkBUS.updatePassword(maTK, newPass));
                loadDataToTable();
            }
        });
    }

    private JButton createFlatButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(0, 40));
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Render màu trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                if (v != null) {
                    comp.setForeground(v.toString().equals("HOẠT ĐỘNG") ? new Color(46, 204, 113) : Color.RED);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });
    }
}