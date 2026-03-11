package gui;

import bus.NhaCungCapBUS;
import dto.NhaCungCapDTO;
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

public class NhaCungCapGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private NhaCungCapBUS nccBUS = new NhaCungCapBUS();
    private DefaultTableModel modelNCC;
    private JTable tblNCC;
    private TableRowSorter<DefaultTableModel> sorterNCC;

    private JTextField txtTimKiem;
    private JComboBox<String> cbxLocTrangThai;
    private JButton btnThem, btnSua, btnDoiTrangThai, btnLamMoi;
    private TaiKhoanDTO currentUser;

    public NhaCungCapGUI(TaiKhoanDTO user) {
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

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);

        cbxLocTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "HOẠT ĐỘNG", "NGỪNG HOẠT ĐỘNG"});
        cbxLocTrangThai.setPreferredSize(new Dimension(160, 35));

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(220, 35));
        txtTimKiem.setToolTipText("Tìm theo Tên hoặc SĐT...");

        btnLamMoi = createFlatButton("Làm Mới", COL_SIDEBAR);

        pnlLeft.add(cbxLocTrangThai);
        pnlLeft.add(new JLabel("Tìm kiếm:"));
        pnlLeft.add(txtTimKiem);
        pnlLeft.add(btnLamMoi);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);

        btnThem = createFlatButton("Thêm NCC", COL_PRIMARY);
        btnSua = createFlatButton("Sửa", COL_SIDEBAR);
        btnDoiTrangThai = createFlatButton("Đổi Trạng Thái", new Color(192, 57, 43));

        pnlRight.add(btnDoiTrangThai);
        pnlRight.add(btnSua);
        pnlRight.add(btnThem);

        pnlToolbar.add(pnlLeft, BorderLayout.WEST);
        pnlToolbar.add(pnlRight, BorderLayout.EAST);

        // --- TABLE ---
        String[] columns = {"Mã NCC", "Tên Nhà Cung Cấp", "Số Điện Thoại", "Địa Chỉ", "Trạng Thái"};
        modelNCC = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNCC = new JTable(modelNCC);
        styleTable(tblNCC);

        sorterNCC = new TableRowSorter<>(modelNCC);
        tblNCC.setRowSorter(sorterNCC);

        add(pnlToolbar, BorderLayout.NORTH);
        add(new JScrollPane(tblNCC), BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        modelNCC.setRowCount(0);
        List<NhaCungCapDTO> ds = nccBUS.getAll();
        if (ds != null) {
            for (NhaCungCapDTO ncc : ds) {
                modelNCC.addRow(new Object[]{
                        "NCC" + String.format("%03d", ncc.getMaNCC()),
                        ncc.getTenNCC(),
                        ncc.getSoDienThoai(),
                        ncc.getDiaChi(),
                        ncc.getTrangThai().toString()
                });
            }
        }
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // 1. Live Search (Tên index 1, SĐT index 2)
        String keyword = txtTimKiem.getText().trim();
        if (!keyword.isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + keyword, 1, 2));

        // 2. Lọc Trạng thái (Cột index 4)
        int idx = cbxLocTrangThai.getSelectedIndex();
        if (idx == 1) filters.add(RowFilter.regexFilter("(?i)HOẠT ĐỘNG", 4));
        if (idx == 2) filters.add(RowFilter.regexFilter("(?i)NGỪNG HOẠT ĐỘNG", 4));

        sorterNCC.setRowFilter(RowFilter.andFilter(filters));
    }

    private void initEvents() {
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        cbxLocTrangThai.addActionListener(e -> applyFilters());
        btnLamMoi.addActionListener(e -> { txtTimKiem.setText(""); cbxLocTrangThai.setSelectedIndex(0); loadDataToTable(); });

        // Sửa lại đoạn code nút Thêm và Sửa trong initEvents()
        btnThem.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            // Khởi tạo Dialog với 3 tham số, truyền null cho DTO vì là Thêm mới
            new NhaCungCapDialog(owner, null, nccBUS).setVisible(true);

            // Vì Dialog là dạng Modal (true), code sẽ dừng ở đây chờ Dialog đóng
            // Khi Dialog đóng lại, lệnh loadDataToTable() sẽ tự động chạy để cập nhật bảng
            loadDataToTable();
        });

        btnSua.addActionListener(e -> {
            int row = tblNCC.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần sửa!");
                return;
            }

            int modelRow = tblNCC.convertRowIndexToModel(row);
            // Cắt chữ "NCC" lấy ID
            int maNCC = Integer.parseInt(modelNCC.getValueAt(modelRow, 0).toString().substring(3));

            NhaCungCapDTO nccSua = nccBUS.getById(maNCC);
            if (nccSua != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                // Truyền đúng 3 tham số (bỏ chữ this đi), nccSua mang dữ liệu lên form
                new NhaCungCapDialog(owner, nccSua, nccBUS).setVisible(true);

                // Xóa cái JOptionPane dư thừa đi, cập nhật lại bảng
                loadDataToTable();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu nhà cung cấp này!");
            }
        });

        // LOGIC ĐỔI TRẠNG THÁI LINH HOẠT
        btnDoiTrangThai.addActionListener(e -> {
            int row = tblNCC.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần đổi trạng thái!");
                return;
            }
            int modelRow = tblNCC.convertRowIndexToModel(row);
            int maNCC = Integer.parseInt(modelNCC.getValueAt(modelRow, 0).toString().substring(3));
            String ten = modelNCC.getValueAt(modelRow, 1).toString();
            String trangThaiHienTai = modelNCC.getValueAt(modelRow, 4).toString();

            boolean isHoatDong = trangThaiHienTai.contains("HOẠT ĐỘNG") && !trangThaiHienTai.contains("NGỪNG");
            TrangThaiCoBan ttMoi = isHoatDong ? TrangThaiCoBan.NGUNG_HOAT_DONG : TrangThaiCoBan.HOAT_DONG;
            String hanhDong = isHoatDong ? "Ngừng giao dịch" : "Hợp tác lại";

            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn " + hanhDong + " với [" + ten + "]?") == JOptionPane.YES_OPTION) {
                // Nhớ đảm bảo BUS gọi đúng hàm cập nhật trạng thái
                JOptionPane.showMessageDialog(this, nccBUS.updateTrangThai(maNCC, ttMoi));
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Sử dụng Margin thay vì setPreferredSize cứng để nút tự động co giãn theo text
        btn.setMargin(new Insets(8, 15, 8, 15));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 255));
        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(0, 40));
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1 && i != 3 && i!= 4 && i != 5) table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Renderer màu trạng thái (Cột index 4)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    if (v.toString().contains("HOẠT ĐỘNG") && !v.toString().contains("NGỪNG")) {
                        comp.setForeground(new Color(46, 204, 113));
                    } else {
                        comp.setForeground(new Color(231, 76, 60));
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });
    }
}