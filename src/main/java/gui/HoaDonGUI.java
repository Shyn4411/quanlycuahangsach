package gui;

import bus.KhachHangBUS;
import bus.NhanVienBUS;
import dto.HoaDonDTO;
import dto.KhachHangDTO;
import dto.NhanVienDTO;
import dto.TaiKhoanDTO;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import com.toedter.calendar.JDateChooser;

public class HoaDonGUI extends JPanel {

    private TaiKhoanDTO currentUser;
    private NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private KhachHangBUS  khachHangBUS = new KhachHangBUS();

    private bus.HoaDonBUS hdBUS = new bus.HoaDonBUS();

    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;

    // NÂNG CẤP: Thêm Sorter để lọc Live Search
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField txtTimKiem;
    private JComboBox<String> cbxTrangThai;
    private JDateChooser txtTuNgay, txtDenNgay;
    private JButton btnLoc, btnLamMoi, btnXemChiTiet;

    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);
    final Color COL_WHITE = Color.WHITE;

    public HoaDonGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);

        JPanel pnlFilterContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlFilterContainer.setBackground(COL_WHITE);
        pnlFilterContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row1.setBackground(COL_WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ HÓA ĐƠN  |");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COL_SIDEBAR);
        row1.add(lblTitle);

        row1.add(new JLabel("Tìm kiếm (Mã HD/Tên KH):"));
        txtTimKiem = new JTextField(15);
        txtTimKiem.setPreferredSize(new Dimension(180, 32));
        row1.add(txtTimKiem);

        row1.add(new JLabel("Trạng thái:"));
        cbxTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoàn Thành", "Chờ Xử Lý", "Đã Hủy"});
        cbxTrangThai.setPreferredSize(new Dimension(120, 32));
        row1.add(cbxTrangThai);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row2.setBackground(COL_WHITE);

        row2.add(new JLabel("Từ ngày:"));
        txtTuNgay = new JDateChooser();
        txtTuNgay.setDateFormatString("dd/MM/yyyy");
        txtTuNgay.setPreferredSize(new Dimension(130, 32));
        row2.add(txtTuNgay);

        row2.add(new JLabel("Đến ngày:"));
        txtDenNgay = new JDateChooser();
        txtDenNgay.setDateFormatString("dd/MM/yyyy");
        txtDenNgay.setPreferredSize(new Dimension(130, 32));
        row2.add(txtDenNgay);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setBackground(COL_WHITE);

        btnLoc = new JButton("Lọc");
        styleButton(btnLoc, COL_SIDEBAR, 130);

        btnLamMoi = new JButton("Làm Mới");
        styleButton(btnLamMoi, new Color(46, 204, 113), 100);

        btnXemChiTiet = new JButton("Xem Chi Tiết");
        styleButton(btnXemChiTiet, new Color(41, 128, 185), 120);

        pnlButtons.add(btnLoc);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(new JLabel(" | "));
        pnlButtons.add(btnXemChiTiet);

        row2.add(pnlButtons);

        pnlFilterContainer.add(row1);
        pnlFilterContainer.add(row2);

        pnlTop.add(pnlFilterContainer, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Mã HD", "Nhân Viên", "Khách Hàng", "Ngày Tạo", "Tổng Tiền", "Tiền Giảm", "Thành Tiền", "Trạng Thái"};
        modelHoaDon = new DefaultTableModel(new String[0][0], cols) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblHoaDon = new JTable(modelHoaDon);
        styleTable(tblHoaDon);

        // NÂNG CẤP: Gắn Sorter vào JTable để chuẩn bị lọc Live
        sorter = new TableRowSorter<>(modelHoaDon);
        tblHoaDon.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.getViewport().setBackground(COL_WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadDataToTable() {
        modelHoaDon.setRowCount(0);
        List<HoaDonDTO> list = hdBUS.getAll();
        if(list != null) {
            for (HoaDonDTO hd : list) {
                addDtoToTable(hd);
            }
        }
    }

    private void addDtoToTable(HoaDonDTO hd) {
        String strTrangThai = hd.getTrangThai().toString();

        String tenNhanVien = "ADMIN";
        if (hd.getMaNV() != null && hd.getMaNV() > 0) {
            NhanVienDTO nv = nhanVienBUS.getById(hd.getMaNV());
            if (nv != null) {
                tenNhanVien = nv.getHoTen();
            }
        }

        String tenKhachHang = "Khách vãng lai";
        if (hd.getMaKH() != null && hd.getMaKH() > 0) {
            KhachHangDTO kh = khachHangBUS.getKhachHangById(hd.getMaKH());
            if (kh != null) {
                tenKhachHang = kh.getHoTen();
            }
        }

        modelHoaDon.addRow(new Object[]{
                "HD" + String.format("%03d", hd.getMaHD()),
                tenNhanVien,
                tenKhachHang,
                hd.getNgayTao() != null ? hd.getNgayTao().format(dtf) : "",
                df.format(hd.getTongTien()),
                df.format(hd.getTienGiam()),
                df.format(hd.getThanhTien()),
                strTrangThai
        });
    }

    private void initEvents() {

        // ==========================================
        // 1. TÌM KIẾM LIVE GÕ TỚI ĐÂU LỌC TỚI ĐÓ
        // ==========================================
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterLive(); }
            public void removeUpdate(DocumentEvent e) { filterLive(); }
            public void changedUpdate(DocumentEvent e) { filterLive(); }
        });

        // Lọc khi chọn Trạng Thái trên ComboBox
        cbxTrangThai.addActionListener(e -> filterLive());

        // ==========================================
        // 2. LỌC THEO NGÀY THÁNG (Phải bấm nút do DatePicker không có sự kiện gõ)
        // ==========================================
        btnLoc.addActionListener(e -> {
            Date tuNgay = txtTuNgay.getDate();
            Date denNgay = txtDenNgay.getDate();

            LocalDate fromDate = (tuNgay != null) ? tuNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            LocalDate toDate = (denNgay != null) ? denNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

            modelHoaDon.setRowCount(0); // Xóa bảng để đổ lại dữ liệu
            List<HoaDonDTO> list = hdBUS.getAll();
            if (list != null) {
                for (HoaDonDTO hd : list) {
                    LocalDate logDate = hd.getNgayTao() != null ? hd.getNgayTao().toLocalDate() : null;

                    // Kiểm tra điều kiện ngày
                    if (fromDate != null && logDate != null && logDate.isBefore(fromDate)) continue;
                    if (toDate != null && logDate != null && logDate.isAfter(toDate)) continue;

                    addDtoToTable(hd);
                }
            }
            // Gọi lại Filter để áp dụng luôn cái text đang gõ trên thanh tìm kiếm
            filterLive();
        });

        // ==========================================
        // 3. LÀM MỚI VÀ XEM CHI TIẾT
        // ==========================================
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbxTrangThai.setSelectedIndex(0);
            txtTuNgay.setDate(null);
            txtDenNgay.setDate(null);
            loadDataToTable(); // Trả lại bảng nguyên thủy
            sorter.setRowFilter(null); // Xóa bộ lọc live
        });

        btnXemChiTiet.addActionListener(e -> {
            int row = tblHoaDon.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn để xem!");
                return;
            }
            // Dùng hàm convertRowIndexToModel để lấy đúng dòng thật dù bảng có bị Sort hay Filter
            int modelRow = tblHoaDon.convertRowIndexToModel(row);
            String maHD = modelHoaDon.getValueAt(modelRow, 0).toString();

            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(owner, maHD);
            dialog.setVisible(true);
        });
    }

    // Hàm thực thi bộ lọc RowSorter
    private void filterLive() {
        String keyword = txtTimKiem.getText().trim();
        String trangThaiLoc = cbxTrangThai.getSelectedItem().toString();

        RowFilter<DefaultTableModel, Object> rfText = null;
        RowFilter<DefaultTableModel, Object> rfStatus = null;

        try {
            // Lọc theo chữ gõ vào (So sánh cột Mã HD và Khách Hàng - cột 0 và 2)
            if (!keyword.isEmpty()) {
                rfText = RowFilter.regexFilter("(?i)" + keyword, 0, 2);
            }

            // Lọc theo Trạng thái (Cột số 7)
            if (!trangThaiLoc.equals("Tất cả")) {
                rfStatus = RowFilter.regexFilter("(?i)^" + trangThaiLoc + "$", 7);
            }

            // Gộp 2 bộ lọc lại
            if (rfText != null && rfStatus != null) {
                sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(rfText, rfStatus)));
            } else if (rfText != null) {
                sorter.setRowFilter(rfText);
            } else if (rfStatus != null) {
                sorter.setRowFilter(rfStatus);
            } else {
                sorter.setRowFilter(null); // Trả lại ban đầu
            }
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
    }

    private void styleButton(JButton btn, Color bgColor, int width) {
        btn.setBackground(bgColor);
        btn.setForeground(COL_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 32));
    }

    private void styleTable(JTable table) {
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    String status = v.toString();
                    if (status.equals("HOÀN THÀNH")) {
                        comp.setForeground(new Color(46, 204, 113));
                    } else if (status.equals("ĐÃ HỦY")) {
                        comp.setForeground(new Color(231, 76, 60));
                    } else {
                        comp.setForeground(new Color(241, 196, 15));
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                if (isS) comp.setForeground(t.getSelectionForeground());
                return comp;
            }
        });
    }
}