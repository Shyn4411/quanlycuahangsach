package gui;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import enums.TrangThaiCoBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class KhachHangGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private KhachHangBUS khBUS = new KhachHangBUS();

    private DefaultTableModel modelKH;
    private JTable tblKhachHang;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnTimKiem;
    private JComboBox<String> cbxLocDiem;
    private JComboBox<String> cbxLocTrangThai;

    public KhachHangGUI() {
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setBackground(Color.WHITE);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);

        String[] locOptions = {"Tất cả điểm", "Khách VIP (Điểm >= 100)", "Khách mới (Điểm = 0)"};
        cbxLocDiem = new JComboBox<>(locOptions);
        cbxLocDiem.setPreferredSize(new Dimension(160, 35));

        String[] ttOptions = {"Tất cả trạng thái", "Đang hoạt động", "Đã xóa"};
        cbxLocTrangThai = new JComboBox<>(ttOptions);
        cbxLocTrangThai.setPreferredSize(new Dimension(140, 35));

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(180, 35));

        btnTimKiem = createFlatButton("Tìm", "/icons/research.png", COL_SIDEBAR);
        btnTimKiem.setPreferredSize(new Dimension(90, 35));

        pnlSearch.add(cbxLocTrangThai);
        pnlSearch.add(cbxLocDiem);
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setBackground(Color.WHITE);

        btnThem = createFlatButton("Thêm Mới", "/icons/plus.png", COL_PRIMARY);
        btnThem.setPreferredSize(new Dimension(130, 35));

        btnSua = createFlatButton("Sửa", "/icons/pencil.png", COL_SIDEBAR);
        btnSua.setPreferredSize(new Dimension(100, 35));

        btnXoa = createFlatButton("Xóa", "/icons/delete.png", COL_SIDEBAR);
        btnXoa.setPreferredSize(new Dimension(90, 35));

        pnlAction.add(btnXoa);
        pnlAction.add(btnSua);
        pnlAction.add(btnThem);

        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlAction, BorderLayout.EAST);

        String[] columns = {"Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm Tích Lũy", "Ngày Tạo", "Trạng Thái"};
        modelKH = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblKhachHang = new JTable(modelKH);
        tblKhachHang.setRowHeight(40);

        add(pnlToolbar, BorderLayout.NORTH);
        add(new JScrollPane(tblKhachHang), BorderLayout.CENTER);

        // ==========================================
        // TÚT TÁT GIAO DIỆN BẢNG KHÁCH HÀNG (GIỐNG BẢNG SÁCH)
        // ==========================================
        tblKhachHang.setFocusable(false);
        tblKhachHang.setIntercellSpacing(new Dimension(0, 0));
        tblKhachHang.setSelectionBackground(new Color(232, 240, 255));
        tblKhachHang.setSelectionForeground(Color.BLACK);
        tblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Chỉnh Header và CĂN GIỮA TIÊU ĐỀ
        JTableHeader header = tblKhachHang.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Căn giữa cho các cột (Trừ cột Họ Tên để mặc định căn trái cho dễ đọc)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tblKhachHang.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã KH
        tblKhachHang.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // SĐT
        tblKhachHang.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Điểm
        tblKhachHang.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Ngày tạo

        // Renderer riêng cho cột Trạng Thái để tô màu chữ
        tblKhachHang.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    if (value.toString().equals("HOẠT ĐỘNG")) {
                        c.setForeground(new Color(46, 204, 113)); // Xanh lá
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(231, 76, 60)); // Đỏ
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        });

        // Tăng độ rộng cột Họ Tên cho thoải mái
        tblKhachHang.getColumnModel().getColumn(1).setPreferredWidth(200);
    }

    private JButton createFlatButton(String text, String iconPath, Color bgColor) {
        JButton btn = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            btn.setIconTextGap(8);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh: " + iconPath);
        }

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void loadDataToTable() {
        cbxLocTrangThai.setSelectedIndex(0);
        cbxLocDiem.setSelectedIndex(0);
        txtTimKiem.setText("");
        applyFilters();
    }

    private void applyFilters() {
        modelKH.setRowCount(0);
        List<KhachHangDTO> ds = khBUS.getAll();

        String keyword = txtTimKiem.getText().trim().toLowerCase();
        int filterDiem = cbxLocDiem.getSelectedIndex();
        int filterTT = cbxLocTrangThai.getSelectedIndex();

        for (KhachHangDTO kh : ds) {

            boolean matchKey = keyword.isEmpty() ||
                    kh.getHoTen().toLowerCase().contains(keyword) ||
                    kh.getSoDienThoai().contains(keyword) ||
                    kh.getMaKHCode().toLowerCase().contains(keyword);

            boolean matchDiem = (filterDiem == 0) ||
                    (filterDiem == 1 && kh.getDiemTichLuy() >= 100) ||
                    (filterDiem == 2 && kh.getDiemTichLuy() == 0);

            boolean isHoatDong = (kh.getTrangThai() != null && kh.getTrangThai() == TrangThaiCoBan.HoatDong);
            boolean matchTT = (filterTT == 0) ||
                    (filterTT == 1 && isHoatDong) ||
                    (filterTT == 2 && !isHoatDong);

            if (matchKey && matchDiem && matchTT) {
                String strTrangThai = isHoatDong ? "HOẠT ĐỘNG" : "NGỪNG HOẠT ĐỘNG";
                modelKH.addRow(new Object[]{
                        kh.getMaKHCode(),
                        kh.getHoTen(),
                        kh.getSoDienThoai(),
                        kh.getDiemTichLuy(),
                        kh.getNgayTaoFormat(),
                        strTrangThai
                });
            }
        }
    }

    private void initEvents() {

        btnThem.addActionListener(e -> {
            Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            new KhachHangDialog(mainFrame, this, null, khBUS).setVisible(true);
        });

        btnSua.addActionListener(e -> {
            int row = tblKhachHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khách hàng trên bảng để sửa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maCode = modelKH.getValueAt(row, 0).toString();
            int maKhachHang = Integer.parseInt(maCode.substring(2));

            KhachHangDTO khSua = null;
            List<KhachHangDTO> ds = khBUS.getAll();
            for (KhachHangDTO kh : ds) {
                if (kh.getMaKH() == maKhachHang) {
                    khSua = kh;
                    break;
                }
            }

            if (khSua != null) {
                Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                new KhachHangDialog(mainFrame, this, khSua, khBUS).setVisible(true);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = tblKhachHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khách hàng trên bảng để xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String trangThaiHienTai = modelKH.getValueAt(row, 5).toString();
            if (trangThaiHienTai.equals("NGỪNG HOẠT ĐỘNG")) {
                JOptionPane.showMessageDialog(this, "Khách hàng này đã bị xóa từ trước!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String tenKH = modelKH.getValueAt(row, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn ngừng hoạt động khách hàng '" + tenKH + "' không?\n" + tenKH + " sẽ được cập nhật trạng thái NGỪNG HOẠT ĐỘNG!",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                String maCode = modelKH.getValueAt(row, 0).toString();
                int maKhachHang = Integer.parseInt(maCode.substring(2));

                String msg = khBUS.deleteKhachHang(maKhachHang);

                JOptionPane.showMessageDialog(this, msg);

                if (msg.toLowerCase().contains("thành công")) {
                    applyFilters();
                }
            }
        });

        btnTimKiem.addActionListener(e -> applyFilters());
        txtTimKiem.addActionListener(e -> applyFilters());
        cbxLocDiem.addActionListener(e -> applyFilters());
        cbxLocTrangThai.addActionListener(e -> applyFilters());
    }
}