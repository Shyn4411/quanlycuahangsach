package gui;

import bus.NhanVienBUS;
import dto.NhanVienDTO;
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
import java.util.regex.Pattern;

public class NhanVienGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private NhanVienBUS nvBUS = new NhanVienBUS();
    private DefaultTableModel modelNV;
    private JTable tblNhanVien;
    private TableRowSorter<DefaultTableModel> sorterNV;

    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnKhoa, btnLamMoi, btnResetPass;
    private JComboBox<String> cbxLocQuyen, cbxLocTrangThai;

    private TaiKhoanDTO userLogin;

    // Timer để làm mượt tìm kiếm
    private Timer searchTimer;

    public NhanVienGUI(TaiKhoanDTO user) {
        this.userLogin = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlToolbar = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlToolbar.setBackground(Color.WHITE);

        JPanel pnlRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlRow1.setBackground(Color.WHITE);

        cbxLocTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "HOẠT ĐỘNG", "KHÓA"});
        cbxLocTrangThai.setPreferredSize(new Dimension(150, 35));

        cbxLocQuyen = new JComboBox<>(new String[]{"Tất cả chức vụ", "Quản trị viên", "Nhân viên bán hàng", "Nhân viên kho"});
        cbxLocQuyen.setPreferredSize(new Dimension(160, 35));

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(280, 35));
        // Cập nhật lại Tooltip
        txtTimKiem.setToolTipText("Tìm theo Mã NV, Tên, SĐT hoặc Username...");

        pnlRow1.add(new JLabel("Bộ lọc:"));
        pnlRow1.add(cbxLocTrangThai);
        pnlRow1.add(cbxLocQuyen);
        pnlRow1.add(new JLabel("Tìm kiếm:"));
        pnlRow1.add(txtTimKiem);

        JPanel pnlRow2 = new JPanel(new BorderLayout());
        pnlRow2.setBackground(Color.WHITE);

        JPanel pnlLeftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeftActions.setBackground(Color.WHITE);
        btnLamMoi = createFlatButton("Làm Mới", COL_SIDEBAR);
        pnlLeftActions.add(btnLamMoi);

        JPanel pnlRightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRightActions.setBackground(Color.WHITE);

        btnResetPass = createFlatButton("Đổi Pass", new Color(243, 156, 18));
        btnKhoa = createFlatButton("Khóa/Mở Khóa", new Color(192, 57, 43));
        btnSua = createFlatButton("Sửa", COL_SIDEBAR);
        btnThem = createFlatButton("Thêm NV", COL_PRIMARY);

        pnlRightActions.add(btnResetPass);
        pnlRightActions.add(btnKhoa);
        pnlRightActions.add(btnSua);
        pnlRightActions.add(btnThem);

        pnlRow2.add(pnlLeftActions, BorderLayout.WEST);
        pnlRow2.add(pnlRightActions, BorderLayout.EAST);

        pnlToolbar.add(pnlRow1);
        pnlToolbar.add(pnlRow2);

        String[] columns = {"Mã NV", "Họ Tên", "Số Điện Thoại", "Tên Đăng Nhập", "Chức Vụ", "Trạng Thái", "MaTK"};
        modelNV = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblNhanVien = new JTable(modelNV);
        tblNhanVien.removeColumn(tblNhanVien.getColumnModel().getColumn(6));
        styleTable(tblNhanVien);

        sorterNV = new TableRowSorter<>(modelNV);
        tblNhanVien.setRowSorter(sorterNV);

        add(pnlToolbar, BorderLayout.NORTH);
        add(new JScrollPane(tblNhanVien), BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        modelNV.setRowCount(0);
        List<NhanVienDTO> ds = nvBUS.getAll();
        if (ds != null) {
            for (NhanVienDTO nv : ds) {
                String tenChucVu = Role.fromRole(nv.getMaQuyen()).getTenChucVu();
                modelNV.addRow(new Object[]{
                        "NV" + String.format("%03d", nv.getMaNV()),
                        nv.getHoTen(),
                        nv.getSoDienThoai(),
                        nv.getTenDangNhap(),
                        tenChucVu,
                        nv.getTrangThai().toString(),
                        nv.getMaTaiKhoan()
                });
            }
        }
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String keyword = txtTimKiem.getText().trim();
        if (!keyword.isEmpty()) {
            // ĐÃ FIX: Thêm cột 0 (Mã NV) vào danh sách quét tìm kiếm và dùng Pattern.quote chống crash
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 0, 1, 2, 3));
        }

        int filterTT = cbxLocTrangThai.getSelectedIndex();
        if (filterTT == 1) filters.add(RowFilter.regexFilter("(?i)HOẠT ĐỘNG", 5));
        if (filterTT == 2) filters.add(RowFilter.regexFilter("(?i)KHÓA", 5));

        int filterQuyen = cbxLocQuyen.getSelectedIndex();
        if (filterQuyen > 0) {
            String quyenStr = cbxLocQuyen.getSelectedItem().toString();
            filters.add(RowFilter.regexFilter("(?i)^" + Pattern.quote(quyenStr) + "$", 4));
        }

        try {
            sorterNV.setRowFilter(RowFilter.andFilter(filters));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initEvents() {
        // --- FIX TÌM KIẾM MƯỢT ---
        searchTimer = new Timer(300, e -> applyFilters());
        searchTimer.setRepeats(false);

        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { restartTimer(); }
            public void removeUpdate(DocumentEvent e) { restartTimer(); }
            public void changedUpdate(DocumentEvent e) { restartTimer(); }

            private void restartTimer() {
                if (searchTimer.isRunning()) searchTimer.restart();
                else searchTimer.start();
            }
        });

        cbxLocTrangThai.addActionListener(e -> applyFilters());
        cbxLocQuyen.addActionListener(e -> applyFilters());

        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbxLocTrangThai.setSelectedIndex(0);
            cbxLocQuyen.setSelectedIndex(0);
            loadDataToTable();
        });

        btnThem.addActionListener(e -> {
            Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            new NhanVienDialog(mainFrame, null, nvBUS).setVisible(true);
            loadDataToTable();
        });

        btnSua.addActionListener(e -> {
            int row = tblNhanVien.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!");
                return;
            }
            int modelRow = tblNhanVien.convertRowIndexToModel(row);
            int maNV = Integer.parseInt(modelNV.getValueAt(modelRow, 0).toString().substring(2));

            NhanVienDTO nvSua = nvBUS.getById(maNV);
            if (nvSua != null) {
                Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                new NhanVienDialog(mainFrame, nvSua, nvBUS).setVisible(true);
                loadDataToTable();
            }
        });

        btnKhoa.addActionListener(e -> {
            int row = tblNhanVien.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần thao tác!");
                return;
            }

            int modelRow = tblNhanVien.convertRowIndexToModel(row);
            int maTK = (int) modelNV.getValueAt(modelRow, 6);
            String tenNV = modelNV.getValueAt(modelRow, 1).toString();
            String trangThaiHienTai = modelNV.getValueAt(modelRow, 5).toString();

            if (userLogin != null && userLogin.getMaTaiKhoan() == maTK) {
                JOptionPane.showMessageDialog(this, "CẢNH BÁO: Bạn không thể tự khóa tài khoản của chính mình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isHoatDong = trangThaiHienTai.equals("HOẠT ĐỘNG");
            TrangThaiTaiKhoan moi = isHoatDong ? TrangThaiTaiKhoan.KHOA : TrangThaiTaiKhoan.HOAT_DONG;
            String hanhDong = isHoatDong ? "KHÓA" : "MỞ KHÓA";

            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận " + hanhDong + " tài khoản của [" + tenNV + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String msg = nvBUS.updateTrangThai(maTK, moi);
                JOptionPane.showMessageDialog(this, msg);
                loadDataToTable();
            }
        });

        btnResetPass.addActionListener(e -> {
            int row = tblNhanVien.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần đổi mật khẩu!");
                return;
            }

            int modelRow = tblNhanVien.convertRowIndexToModel(row);
            int maTK = (int) modelNV.getValueAt(modelRow, 6);
            String tenNV = modelNV.getValueAt(modelRow, 1).toString();

            JPasswordField pf = new JPasswordField();
            int action = JOptionPane.showConfirmDialog(this, pf, "Nhập mật khẩu mới cho [" + tenNV + "]:", JOptionPane.OK_CANCEL_OPTION);

            if (action == JOptionPane.OK_OPTION) {
                String pass = new String(pf.getPassword());
                JOptionPane.showMessageDialog(this, nvBUS.updatePassword(maTK, pass));
            }
        });
    }

    private JButton createFlatButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 15, 8, 15));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(0, 40));
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1 && i != 3) table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    if (v.toString().equals("HOẠT ĐỘNG")) comp.setForeground(new Color(46, 204, 113));
                    else comp.setForeground(new Color(231, 76, 60));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });
    }
}