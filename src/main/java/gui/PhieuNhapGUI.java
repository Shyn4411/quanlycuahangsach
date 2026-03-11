package gui;

import dto.TaiKhoanDTO;
import dto.PhieuNhapDTO;
import bus.PhieuNhapBUS;
import enums.TrangThaiGiaoDich;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.List;

public class PhieuNhapGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTable tblPhieuNhap;
    private DefaultTableModel modelPhieuNhap;
    private TableRowSorter<DefaultTableModel> sorterPN;

    private JTextField txtTimKiem;
    private JComboBox<String> cbxTrangThai;
    private JButton btnLamMoi, btnXemChiTiet, btnTaoPhieu, btnHuyPhieu, btnDuyetPhieu;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);
    final Color COL_WHITE = Color.WHITE;

    public PhieuNhapGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // CHỐNG ĐÈ: Dùng BoxLayout xếp dọc, để đảm bảo 2 phần trên/dưới tách biệt hoàn toàn
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setBackground(COL_BG_MAIN);

        // --- HÀNG 1: KHU VỰC TÌM KIẾM ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlFilter.setBackground(COL_WHITE);
        pnlFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Cố định chiều cao của pnlFilter để không bị bè
        pnlFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        pnlFilter.add(new JLabel("Tìm (Mã PN / NCC):"));
        txtTimKiem = new JTextField(15);
        txtTimKiem.setPreferredSize(new Dimension(160, 32));
        pnlFilter.add(txtTimKiem);

        pnlFilter.add(new JLabel("Trạng thái:"));
        cbxTrangThai = new JComboBox<>(new String[]{"Tất cả", TrangThaiGiaoDich.HOAN_THANH.toString(), TrangThaiGiaoDich.CHO_XU_LY.toString(), TrangThaiGiaoDich.DA_HUY.toString()});
        cbxTrangThai.setPreferredSize(new Dimension(130, 32));
        pnlFilter.add(cbxTrangThai);

        btnLamMoi = new JButton("Làm Mới");
        styleButton(btnLamMoi, new Color(149, 165, 166), 100);
        pnlFilter.add(btnLamMoi);

        pnlTop.add(pnlFilter);
        pnlTop.add(Box.createVerticalStrut(10)); // Khoảng trống 10px giữa 2 hàng

        // --- HÀNG 2: KHU VỰC NÚT THAO TÁC ---
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setBackground(COL_BG_MAIN);

        // Cố định chiều cao của pnlActions
        pnlActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Nút đã được thu nhỏ kích thước (width)
        btnDuyetPhieu = new JButton("Duyệt (Nhập Kho)");
        styleButton(btnDuyetPhieu, new Color(41, 128, 185), 140);

        btnHuyPhieu = new JButton("Hủy Phiếu");
        styleButton(btnHuyPhieu, new Color(231, 76, 60), 100);

        btnXemChiTiet = new JButton("Xem Chi Tiết");
        styleButton(btnXemChiTiet, COL_SIDEBAR, 110);

        btnTaoPhieu = new JButton("Tạo Phiếu Nhập");
        styleButton(btnTaoPhieu, new Color(46, 204, 113), 140);

        pnlActions.add(btnDuyetPhieu);
        pnlActions.add(btnHuyPhieu);
        pnlActions.add(new JLabel(" | "));
        pnlActions.add(btnXemChiTiet);
        pnlActions.add(btnTaoPhieu);

        pnlTop.add(pnlActions);

        add(pnlTop, BorderLayout.NORTH);

        // --- BẢNG DỮ LIỆU ---
        String[] cols = {"Mã PN", "Nhà Cung Cấp", "Nhân Viên", "Tổng Tiền", "Ngày Tạo", "Trạng Thái"};
        modelPhieuNhap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPhieuNhap = new JTable(modelPhieuNhap);
        styleTable(tblPhieuNhap);

        sorterPN = new TableRowSorter<>(modelPhieuNhap);
        tblPhieuNhap.setRowSorter(sorterPN);

        add(new JScrollPane(tblPhieuNhap), BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        modelPhieuNhap.setRowCount(0);
        List<PhieuNhapDTO> list = pnBUS.getAll();
        if (list != null) {
            for (PhieuNhapDTO pn : list) {
                modelPhieuNhap.addRow(new Object[]{
                        "PN" + String.format("%03d", pn.getMaPN()),
                        pn.getTenNCC(),
                        pn.getTenNV(),
                        df.format(pn.getTongTien()),
                        pn.getNgayTao() != null ? pn.getNgayTao().format(dtf) : "",
                        pn.getTrangThai().toString()
                });
            }
        }
    }

    private void initEvents() {
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterLive(); }
            public void removeUpdate(DocumentEvent e) { filterLive(); }
            public void changedUpdate(DocumentEvent e) { filterLive(); }
        });

        cbxTrangThai.addActionListener(e -> filterLive());

        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbxTrangThai.setSelectedIndex(0);
            loadDataToTable();
        });

        btnTaoPhieu.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            TaoPhieuNhapDialog dialog = new TaoPhieuNhapDialog(owner, currentUser);
            dialog.setVisible(true);
            loadDataToTable();
        });

        btnXemChiTiet.addActionListener(e -> {
            int row = tblPhieuNhap.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phiếu nhập trên bảng để xem chi tiết!");
                return;
            }
            int modelRow = tblPhieuNhap.convertRowIndexToModel(row);
            int maPN = Integer.parseInt(tblPhieuNhap.getValueAt(modelRow, 0).toString().substring(2));
            PhieuNhapDTO pnDTO = pnBUS.getById(maPN);
            if (pnDTO == null) return;

            Window owner = SwingUtilities.getWindowAncestor(this);
            ChiTietPhieuNhapDialog dialog = new ChiTietPhieuNhapDialog(owner, pnDTO);
            dialog.setVisible(true);
        });

        btnDuyetPhieu.addActionListener(e -> {
            int row = tblPhieuNhap.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần duyệt!");
                return;
            }

            int modelRow = tblPhieuNhap.convertRowIndexToModel(row);
            String trangThai = tblPhieuNhap.getValueAt(modelRow, 5).toString();

            if (trangThai.equals(TrangThaiGiaoDich.HOAN_THANH.toString())) {
                JOptionPane.showMessageDialog(this, "Phiếu này ĐÃ ĐƯỢC DUYỆT và cộng vào kho rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (trangThai.equals(TrangThaiGiaoDich.DA_HUY.toString())) {
                JOptionPane.showMessageDialog(this, "Không thể duyệt phiếu đã bị hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String maPNStr = tblPhieuNhap.getValueAt(modelRow, 0).toString();
            int maPN = Integer.parseInt(maPNStr.substring(2));

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận duyệt " + maPNStr + " ?\nSách sẽ chính thức được CỘNG VÀO KHO và không thể hoàn tác.",
                    "Duyệt Nhập Kho", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = pnBUS.hoanThanhPhieuNhap(maPN);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) loadDataToTable();
            }
        });

        btnHuyPhieu.addActionListener(e -> {
            int row = tblPhieuNhap.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần hủy!");
                return;
            }

            int modelRow = tblPhieuNhap.convertRowIndexToModel(row);
            String trangThai = tblPhieuNhap.getValueAt(modelRow, 5).toString();

            if (trangThai.equals(TrangThaiGiaoDich.DA_HUY.toString())) {
                JOptionPane.showMessageDialog(this, "Phiếu này đã bị hủy trước đó rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (trangThai.equals(TrangThaiGiaoDich.HOAN_THANH.toString())) {
                JOptionPane.showMessageDialog(this, "Phiếu này đã duyệt nhập kho, KHÔNG THỂ HỦY!\nNếu có sai sót, vui lòng làm phiếu Xuất Kho/Trả Hàng NCC.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maPNStr = tblPhieuNhap.getValueAt(modelRow, 0).toString();
            int maPN = Integer.parseInt(maPNStr.substring(2));

            String[] options = {"Nhà Cung Cấp hết hàng", "Sai thông tin đơn giá", "Đổi NCC khác", "Lý do khác..."};
            String lyDo = (String) JOptionPane.showInputDialog(this,
                    "Bạn đang hủy " + maPNStr + ".\nVui lòng chọn lý do hủy phiếu:",
                    "Hủy Phiếu Nhập", JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (lyDo != null && !lyDo.trim().isEmpty()) {
                if (lyDo.equals("Lý do khác...")) {
                    lyDo = JOptionPane.showInputDialog(this, "Nhập lý do hủy:");
                    if (lyDo == null || lyDo.trim().isEmpty()) return;
                }

                String result = pnBUS.huyPhieuNhap(maPN, lyDo);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) loadDataToTable();
            }
        });
    }

    private void filterLive() {
        String keyword = txtTimKiem.getText().trim();
        String trangThaiLoc = cbxTrangThai.getSelectedItem().toString();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        try {
            if (!keyword.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + keyword, 0, 1));
            }
            if (!trangThaiLoc.equals("Tất cả")) {
                filters.add(RowFilter.regexFilter("(?i)^" + trangThaiLoc + "$", 5));
            }

            if (filters.isEmpty()) {
                sorterPN.setRowFilter(null);
            } else {
                sorterPN.setRowFilter(RowFilter.andFilter(filters));
            }
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
    }

    private void styleButton(JButton btn, Color bgColor, int width) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(bgColor);
        btn.setForeground(COL_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Giảm font xuống chút xíu cho gọn
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 32));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isSel, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    String status = v.toString();
                    if (status.equals(TrangThaiGiaoDich.HOAN_THANH.toString())) {
                        comp.setForeground(new Color(46, 204, 113));
                    } else if (status.equals(TrangThaiGiaoDich.DA_HUY.toString())) {
                        comp.setForeground(new Color(231, 76, 60));
                    } else if (status.equals(TrangThaiGiaoDich.CHO_XU_LY.toString())) {
                        comp.setForeground(new Color(243, 156, 18));
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                if (isSel) comp.setForeground(t.getSelectionForeground());
                return comp;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
    }
}