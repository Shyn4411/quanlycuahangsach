package gui;

import bus.SachBUS;
import bus.TheLoaiBUS; // IMPORT THÊM BUS NÀY NHA TỦN
import dto.SachDTO;
import dto.TaiKhoanDTO;
import dto.TheLoaiDTO; // IMPORT THÊM DTO NÀY NỮA
import enums.TrangThaiSach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class SachGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private SachBUS sBUS = new SachBUS();
    private TheLoaiBUS tlBUS = new TheLoaiBUS(); // KHỞI TẠO BUS THỂ LOẠI

    private DefaultTableModel modelSach;
    private JTable tblSach;
    private JTextField txtTimKiem;
    private JButton btnThem, btnXemChiTiet, btnXoa, btnLoc;
    private TaiKhoanDTO userLogin;

    public SachGUI(TaiKhoanDTO user) {
        this.userLogin = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. THANH CÔNG CỤ ---
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setBackground(Color.WHITE);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(200, 35));
        btnLoc = createFlatButton("Lọc", "/icons/research.png", COL_SIDEBAR);
        pnlSearch.add(new JLabel("Tìm kiếm:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnLoc);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setBackground(Color.WHITE);
        btnThem = createFlatButton("Thêm Sách", "/icons/plus.png", COL_PRIMARY);
        btnXemChiTiet = createFlatButton("Xem chi tiết", "/icons/view.png", COL_SIDEBAR);
        btnXemChiTiet.setPreferredSize(new Dimension(150,35));
        btnXoa = createFlatButton("Xóa", "/icons/delete.png", COL_SIDEBAR);

        if (userLogin.getMaQuyen() == 2) {
            btnThem.setVisible(false);
            btnXemChiTiet.setVisible(false);
            btnXoa.setVisible(false);
        }

        pnlAction.add(btnXoa);
        pnlAction.add(btnXemChiTiet);
        pnlAction.add(btnThem);

        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlAction, BorderLayout.EAST);
        add(pnlToolbar, BorderLayout.NORTH);

        // --- 2. BẢNG DỮ LIỆU ---
        String[] columns = {"Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Giá Bán", "Kho", "Trạng Thái"};
        modelSach = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSach = new JTable(modelSach);
        tblSach.setRowHeight(40);


        // Bọc JScrollPane và thêm cái viền (Border) xám bao quanh toàn bộ bảng
        JScrollPane scrollPane = new JScrollPane(tblSach);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(pnlToolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // TÚT TÁT GIAO DIỆN BẢNG SÁCH
        // ==========================================
        tblSach.setFocusable(false);
        tblSach.setIntercellSpacing(new Dimension(0, 0));
        tblSach.setSelectionBackground(new Color(232, 240, 255));
        tblSach.setSelectionForeground(Color.BLACK);
        tblSach.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = tblSach.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tblSach.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        tblSach.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Thể Loại (Căn giữa cho đẹp)
        tblSach.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Giá
        tblSach.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Kho

        // Tô màu Trạng Thái
        tblSach.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    if (value.toString().equals("ĐANG BÁN")) {
                        c.setForeground(new Color(46, 204, 113));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(231, 76, 60));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        });

        tblSach.getColumnModel().getColumn(1).setPreferredWidth(250);
    }

    private JButton createFlatButton(String text, String iconPath, Color bg) {
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
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 35));
        return btn;
    }

    // ========================================================
    // ĐÃ FIX HÀM NÀY ĐỂ HIỂN THỊ TÊN THỂ LOẠI THAY VÌ MÃ
    // ========================================================
    public void loadDataToTable() {
        modelSach.setRowCount(0);
        List<SachDTO> ds = sBUS.getAll();
        for (SachDTO s : ds) {
            String ttStr = (s.getTrangThai() == TrangThaiSach.DangBan) ? "ĐANG BÁN" : "NGỪNG BÁN";

            // Dùng TheLoaiBUS móc tên thể loại lên
            TheLoaiDTO tl = tlBUS.getById(s.getMaLoai());
            String tenTheLoai = (tl != null) ? tl.getTenLoai() : "Chưa phân loại";

            modelSach.addRow(new Object[]{
                    s.getMaSachCode(),
                    s.getTenSach(),
                    s.getDanhSachTacGia(),
                    tenTheLoai, // <-- Gắn Tên Thể Loại vào đây
                    String.format("%,.0fđ", s.getGiaBan()),
                    s.getSoLuongTon(),
                    ttStr
            });
        }
    }

    // ========================================================
    // ĐÃ FIX HÀM NÀY ĐỂ LỌC VÀ HIỂN THỊ TÊN THỂ LOẠI LUÔN
    // ========================================================
    private void applyFilters() {
        modelSach.setRowCount(0);
        List<SachDTO> ds = sBUS.getAll();
        String keyword = txtTimKiem.getText().trim().toLowerCase();

        for (SachDTO s : ds) {
            // Lấy tên thể loại trước để hỗ trợ tìm kiếm theo thể loại luôn
            TheLoaiDTO tl = tlBUS.getById(s.getMaLoai());
            String tenTheLoai = (tl != null) ? tl.getTenLoai() : "Chưa phân loại";

            // Lọc theo Tên, Tác giả, Mã Sách hoặc Tên Thể Loại
            boolean matchKey = keyword.isEmpty() ||
                    s.getTenSach().toLowerCase().contains(keyword) ||
                    s.getDanhSachTacGia().toLowerCase().contains(keyword) ||
                    s.getMaSachCode().toLowerCase().contains(keyword) ||
                    tenTheLoai.toLowerCase().contains(keyword); // Nhập "Giáo khoa" là tìm ra sách giáo khoa luôn

            if (matchKey) {
                String ttStr = (s.getTrangThai() == TrangThaiSach.DangBan) ? "ĐANG BÁN" : "NGỪNG BÁN";
                modelSach.addRow(new Object[]{
                        s.getMaSachCode(),
                        s.getTenSach(),
                        s.getDanhSachTacGia(),
                        tenTheLoai, // <-- Gắn Tên Thể Loại vào đây
                        String.format("%,.0fđ", s.getGiaBan()),
                        s.getSoLuongTon(),
                        ttStr
                });
            }
        }
    }

    private void initEvents() {

        btnLoc.addActionListener(e -> applyFilters());
        txtTimKiem.addActionListener(e -> applyFilters());

        btnThem.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            SachDialog dialog = new SachDialog(owner, this, null, sBUS);
            dialog.setVisible(true);

            loadDataToTable();
        });

        btnXemChiTiet.addActionListener(e -> {
            int row = tblSach.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 quyển sách trên bảng!");
                return;
            }

            int id = Integer.parseInt(tblSach.getValueAt(row, 0).toString().substring(1));
            SachDTO s = sBUS.getById(id);

            if (s != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                SachDialog dialog = new SachDialog(owner, this, s, sBUS);
                dialog.setReadOnlyMode(true, userLogin);
                dialog.setVisible(true);

                loadDataToTable();
            }
        });

        btnXoa.addActionListener(e -> {
            int row = tblSach.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!");
                return;
            }

            String trangThaiHienTai = tblSach.getValueAt(row, 6).toString();
            if (trangThaiHienTai.equals("NGỪNG BÁN")) {
                JOptionPane.showMessageDialog(this, "Sách này đã ngừng bán từ trước rồi!");
                return;
            }

            String tenSach = tblSach.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn NGỪNG BÁN sách:\n" + tenSach + " ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(tblSach.getValueAt(row, 0).toString().substring(1));

                String msg = sBUS.deleteSach(id);
                JOptionPane.showMessageDialog(this, msg);

                loadDataToTable();
            }
        });
    }
}