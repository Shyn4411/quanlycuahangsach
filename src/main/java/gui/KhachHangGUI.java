package gui;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiCoBan;

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

public class KhachHangGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private KhachHangBUS khBUS = new KhachHangBUS();
    private DefaultTableModel modelKH;
    private JTable tblKhachHang;
    private TableRowSorter<DefaultTableModel> sorterKH;

    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private JComboBox<String> cbxLocDiem, cbxLocTrangThai;

    private TaiKhoanDTO userLogin;

    public KhachHangGUI(TaiKhoanDTO user) {
        this.userLogin = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- TOOLBAR (Đã FIX lỗi đè nút bằng GridLayout 2 hàng) ---
        JPanel pnlToolbar = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlToolbar.setBackground(Color.WHITE);

        // Hàng 1: Tìm kiếm + Lọc
        JPanel pnlRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlRow1.setBackground(Color.WHITE);

        cbxLocTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "Hoạt động", "Ngừng hoạt động"});
        cbxLocTrangThai.setPreferredSize(new Dimension(150, 35));

        cbxLocDiem = new JComboBox<>(new String[]{"Tất cả mức điểm", "Khách VIP (>= 100)", "Khách mới (= 0)"});
        cbxLocDiem.setPreferredSize(new Dimension(160, 35));

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(250, 35));
        txtTimKiem.setToolTipText("Tìm theo Mã, Tên hoặc SĐT...");

        pnlRow1.add(new JLabel("Tìm kiếm:"));
        pnlRow1.add(txtTimKiem);
        pnlRow1.add(cbxLocTrangThai);
        pnlRow1.add(cbxLocDiem);

        // Hàng 2: Các nút chức năng
        JPanel pnlRow2 = new JPanel(new BorderLayout());
        pnlRow2.setBackground(Color.WHITE);

        JPanel pnlLeftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeftActions.setBackground(Color.WHITE);
        btnLamMoi = createFlatButton("Làm Mới", null, COL_SIDEBAR);
        pnlLeftActions.add(btnLamMoi);

        JPanel pnlRightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRightActions.setBackground(Color.WHITE);

        btnThem = createFlatButton("Thêm Mới", null, COL_PRIMARY);
        btnSua = createFlatButton("Sửa", null, COL_SIDEBAR);
        btnXoa = createFlatButton("Đổi Trạng Thái", null, new Color(192, 57, 43));

        pnlRightActions.add(btnXoa);
        pnlRightActions.add(btnSua);
        pnlRightActions.add(btnThem);

        pnlRow2.add(pnlLeftActions, BorderLayout.WEST);
        pnlRow2.add(pnlRightActions, BorderLayout.EAST);

        pnlToolbar.add(pnlRow1);
        pnlToolbar.add(pnlRow2);

        String[] columns = {"Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm", "Ngày Tạo", "Trạng Thái"};
        modelKH = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblKhachHang = new JTable(modelKH);
        styleTable(tblKhachHang);

        sorterKH = new TableRowSorter<>(modelKH);
        tblKhachHang.setRowSorter(sorterKH);

        add(pnlToolbar, BorderLayout.NORTH);
        add(new JScrollPane(tblKhachHang), BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        modelKH.setRowCount(0);
        List<KhachHangDTO> ds = khBUS.getAll();
        if (ds != null) {
            for (KhachHangDTO kh : ds) {
                modelKH.addRow(new Object[]{
                        "KH" + String.format("%03d", kh.getMaKH()),
                        kh.getHoTen(),
                        kh.getSoDienThoai(),
                        kh.getDiemTichLuy(),
                        kh.getNgayTaoFormat(),
                        kh.getTrangThai().toString()
                });
            }
        }
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // 1. Keyword (Mã index 0, Tên index 1, SĐT index 2)
        String keyword = txtTimKiem.getText().trim();
        if (!keyword.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + keyword, 0, 1, 2));
        }

        // 2. Trạng thái (Cột index 5)
        int filterTT = cbxLocTrangThai.getSelectedIndex();
        if (filterTT == 1) filters.add(RowFilter.regexFilter("(?i)HOẠT ĐỘNG", 5));
        if (filterTT == 2) filters.add(RowFilter.regexFilter("(?i)NGỪNG HOẠT ĐỘNG", 5));

        // 3. Điểm (Cột index 3)
        int filterDiem = cbxLocDiem.getSelectedIndex();
        if (filterDiem > 0) {
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    try {
                        int diem = Integer.parseInt(entry.getValue(3).toString());
                        if (filterDiem == 1) return diem >= 100;
                        if (filterDiem == 2) return diem == 0;
                    } catch (Exception e) {}
                    return true;
                }
            });
        }

        sorterKH.setRowFilter(RowFilter.andFilter(filters));
    }

    private void initEvents() {
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        cbxLocTrangThai.addActionListener(e -> applyFilters());
        cbxLocDiem.addActionListener(e -> applyFilters());

        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbxLocTrangThai.setSelectedIndex(0);
            cbxLocDiem.setSelectedIndex(0);
            loadDataToTable();
        });

        // Sửa lại sự kiện nút THÊM
        btnThem.addActionListener(e -> {
            Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            // Gọi KhachHangDialog, truyền null vào chỗ KhachHangDTO để báo là Thêm mới
            new KhachHangDialog(mainFrame, this, null, khBUS).setVisible(true);
            // Không cần gọi loadDataToTable ở đây vì trong Dialog ông đã gọi parentGUI.loadDataToTable() rồi
        });
        btnSua.addActionListener(e -> {
            int row = tblKhachHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
                return;
            }
            int modelRow = tblKhachHang.convertRowIndexToModel(row);
            // Cắt chữ KH lấy số
            int maKH = Integer.parseInt(modelKH.getValueAt(modelRow, 0).toString().substring(2));

            KhachHangDTO khSua = khBUS.getKhachHangById(maKH);
            if (khSua != null) {
                Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                // Gọi KhachHangDialog, truyền đối tượng khSua vào để load dữ liệu lên form
                new KhachHangDialog(mainFrame, this, khSua, khBUS).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu khách hàng này!");
            }
        });

        btnXoa.addActionListener(e -> {
            int row = tblKhachHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần đổi trạng thái!");
                return;
            }

            int modelRow = tblKhachHang.convertRowIndexToModel(row);
            // Cắt "KH" lấy số ID
            int maKH = Integer.parseInt(modelKH.getValueAt(modelRow, 0).toString().substring(2));
            String trangThaiHienTai = modelKH.getValueAt(modelRow, 5).toString();

            // Xác định trạng thái mới dựa trên trạng thái hiện tại
            boolean isDangHoatDong = trangThaiHienTai.contains("HOẠT ĐỘNG") && !trangThaiHienTai.contains("NGỪNG");
            TrangThaiCoBan trangThaiMoi = isDangHoatDong ? TrangThaiCoBan.NGUNG_HOAT_DONG : TrangThaiCoBan.HOAT_DONG;
            String tenTrangThaiMoi = isDangHoatDong ? "NGỪNG HOẠT ĐỘNG" : "HOẠT ĐỘNG";

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận chuyển khách hàng này sang trạng thái: " + tenTrangThaiMoi + "?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Gọi hàm updateTrangThai thay vì deleteKhachHang
                String msg = khBUS.updateTrangThai(maKH, trangThaiMoi);
                JOptionPane.showMessageDialog(this, msg);
                loadDataToTable();
            }
        });
    }

    private JButton createFlatButton(String text, String iconPath, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));

        if(iconPath != null) {
            try {
                btn.setIcon(new ImageIcon(getClass().getResource(iconPath)));
            } catch (Exception e) {}
        }

        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1) table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    if (v.toString().contains("HOẠT ĐỘNG") && !v.toString().contains("NGỪNG")) {
                        comp.setForeground(new Color(46, 204, 113)); // Xanh
                    } else {
                        comp.setForeground(new Color(231, 76, 60)); // Đỏ
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });
    }
}