package gui;

import dto.HoaDonDTO;
import dto.TaiKhoanDTO;
import enums.TrangThaiGiaoDich;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

// Import thư viện JCalendar
import com.toedter.calendar.JDateChooser;

public class HoaDonGUI extends JPanel {

    private TaiKhoanDTO currentUser;

    private bus.HoaDonBUS hdBUS = new bus.HoaDonBUS();

    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;

    // UI Lọc & Thao tác mới
    private JTextField txtTimKiem;
    private JComboBox<String> cbxTrangThai;
    private JDateChooser txtTuNgay, txtDenNgay;
    private JButton btnLoc, btnLamMoi, btnXemChiTiet, btnHuyDon;

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

        // ==========================================
        // 1. KHU VỰC TOP (BỘ LỌC + NÚT THAO TÁC)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);

        // Container chứa 2 dòng lọc
        JPanel pnlFilterContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlFilterContainer.setBackground(COL_WHITE);
        pnlFilterContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // --- Dòng 1: Tìm kiếm (Mã/Tên) và Trạng Thái ---
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

        // --- Dòng 2: Khoảng ngày và Các nút bấm ---
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

        // Đẩy các nút Lọc, Làm mới, Xem chi tiết, Hủy về bên phải của dòng 2
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setBackground(COL_WHITE);

        btnLoc = new JButton("Lọc");
        styleButton(btnLoc, COL_SIDEBAR, 80);

        btnLamMoi = new JButton("Làm Mới");
        styleButton(btnLamMoi, new Color(46, 204, 113), 100);

        btnXemChiTiet = new JButton("Xem Chi Tiết");
        styleButton(btnXemChiTiet, new Color(41, 128, 185), 120);

        btnHuyDon = new JButton("Hủy Đơn");
        styleButton(btnHuyDon, new Color(231, 76, 60), 100);

        pnlButtons.add(btnLoc);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(new JLabel(" | ")); // Dấu vạch ngăn cách cho đẹp
        pnlButtons.add(btnXemChiTiet);
        pnlButtons.add(btnHuyDon);

        row2.add(pnlButtons);

        // Add 2 dòng vào container
        pnlFilterContainer.add(row1);
        pnlFilterContainer.add(row2);

        pnlTop.add(pnlFilterContainer, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // ==========================================
        // 2. KHU VỰC BẢNG HIỂN THỊ
        // ==========================================
        String[] cols = {"Mã HD", "Nhân Viên", "Khách Hàng", "Ngày Tạo", "Tổng Tiền", "Tiền Giảm", "Thành Tiền", "Trạng Thái"};
        modelHoaDon = new DefaultTableModel(new String[0][0], cols) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblHoaDon = new JTable(modelHoaDon);
        styleTable(tblHoaDon);

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
        String strTrangThai = "";
        if (hd.getTrangThai() == TrangThaiGiaoDich.HoanThanh) strTrangThai = "Hoàn Thành";
        else if (hd.getTrangThai() == TrangThaiGiaoDich.ChoXuLy) strTrangThai = "Chờ Xử Lý";
        else if (hd.getTrangThai() == TrangThaiGiaoDich.DaHuy) strTrangThai = "Đã Hủy";

        modelHoaDon.addRow(new Object[]{
                "HD" + String.format("%03d", hd.getMaHD()),
                hd.getMaNV(),
                hd.getMaKH(), // Tạm hiện Mã KH, nếu có JOIN thì thay bằng Tên KH
                hd.getNgayTao() != null ? hd.getNgayTao().format(dtf) : "",
                df.format(hd.getTongTien()),
                df.format(hd.getTienGiam()),
                df.format(hd.getThanhTien()),
                strTrangThai
        });
    }

    // ==========================================
    // LOGIC LỌC TỔNG HỢP VÀ SỰ KIỆN CLICK
    // ==========================================
    private void initEvents() {
        // 1. Lọc Dữ Liệu
        btnLoc.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            String trangThaiLoc = cbxTrangThai.getSelectedItem().toString();

            Date tuNgay = txtTuNgay.getDate();
            Date denNgay = txtDenNgay.getDate();

            LocalDate fromDate = (tuNgay != null) ? tuNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            LocalDate toDate = (denNgay != null) ? denNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

            modelHoaDon.setRowCount(0);
            List<HoaDonDTO> list = hdBUS.getAll();
            if (list != null) {
                for (HoaDonDTO hd : list) {
                    String strHD = "hd" + String.format("%03d", hd.getMaHD());
                    // Giả sử có Tên KH thì nối vào đây để tìm: String tenKH = getTenKHTuMa(hd.getMaKH()).toLowerCase();
                    String searchTarget = strHD; // + " " + tenKH;

                    String strTrangThai = "";
                    if (hd.getTrangThai() == TrangThaiGiaoDich.HoanThanh) strTrangThai = "Hoàn Thành";
                    else if (hd.getTrangThai() == TrangThaiGiaoDich.ChoXuLy) strTrangThai = "Chờ Xử Lý";
                    else if (hd.getTrangThai() == TrangThaiGiaoDich.DaHuy) strTrangThai = "Đã Hủy";

                    // Kiểm tra chuỗi tìm kiếm
                    if (!keyword.isEmpty() && !searchTarget.contains(keyword)) continue;
                    // Kiểm tra trạng thái
                    if (!trangThaiLoc.equals("Tất cả") && !strTrangThai.equals(trangThaiLoc)) continue;
                    // Kiểm tra ngày
                    LocalDate logDate = hd.getNgayTao() != null ? hd.getNgayTao().toLocalDate() : null;
                    if (fromDate != null && logDate != null && logDate.isBefore(fromDate)) continue;
                    if (toDate != null && logDate != null && logDate.isAfter(toDate)) continue;

                    addDtoToTable(hd);
                }
            }
        });

        // 2. Làm mới
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbxTrangThai.setSelectedIndex(0);
            txtTuNgay.setDate(null);
            txtDenNgay.setDate(null);
            loadDataToTable();
        });

        // 3. Xem chi tiết
        btnXemChiTiet.addActionListener(e -> {
            int row = tblHoaDon.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn để xem!");
                return;
            }
            String maHD = tblHoaDon.getValueAt(row, 0).toString();
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(owner, maHD);
            dialog.setVisible(true);
        });

        // 4. Hủy đơn
        btnHuyDon.addActionListener(e -> {
            int row = tblHoaDon.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần hủy!");
                return;
            }
            String maHDStr = tblHoaDon.getValueAt(row, 0).toString();
            String trangThaiHienTai = tblHoaDon.getValueAt(row, 7).toString();

            if (trangThaiHienTai.equals("Đã Hủy")) {
                JOptionPane.showMessageDialog(this, "Hóa đơn này đã được hủy trước đó!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn hủy hóa đơn " + maHDStr + " này?\nSách sẽ được hoàn lại vào kho.",
                    "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                int maHD = Integer.parseInt(maHDStr.replaceAll("[^0-9]", ""));
                String result = hdBUS.deleteHoaDon(maHD);
                JOptionPane.showMessageDialog(this, result);
                loadDataToTable();
            }
        });
    }

    // --- HÀM TIỆN ÍCH TÚT GIAO DIỆN ---
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

        // Tô màu cột Trạng Thái (Bây giờ là Cột số 7)
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (v != null) {
                    String status = v.toString();
                    if (status.equals("Hoàn Thành")) {
                        comp.setForeground(new Color(46, 204, 113));
                    } else if (status.equals("Đã Hủy")) {
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