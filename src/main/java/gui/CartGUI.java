package gui;

import bus.SachBUS;
import dto.SachDTO;
import session.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

public class CartGUI extends JDialog {

    private final Color COL_PRIMARY = new Color(232, 60, 145);
    private final Color COL_DARK = new Color(67, 51, 76);
    private final Color COL_CREAM = new Color(248, 244, 236);

    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private DecimalFormat df = new DecimalFormat("#,### đ");

    private SachBUS sachBUS = new SachBUS();
    private BigDecimal tongTienTruocThue = BigDecimal.ZERO;

    public CartGUI(Frame owner) {
        super(owner, "Giỏ Hàng Của Bạn", true); // true = Modal (Bắt buộc thao tác xong mới quay lại Store được)
        setSize(700, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        initUI();
        loadCartData();
    }

    private void initUI() {
        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(COL_DARK);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblTitle = new JLabel("🛒 CHI TIẾT GIỎ HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);

        // --- BẢNG GIỎ HÀNG ---
        String[] cols = {"STT", "Tên Sách", "Đơn Giá", "Số Lượng", "Thành Tiền"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        tblCart = new JTable(cartModel);
        setupTable(tblCart);

        JScrollPane scrollPane = new JScrollPane(tblCart);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // --- BOTTOM (TỔNG TIỀN & NÚT BẤM) ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(COL_CREAM);
        pnlBottom.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblTotal = new JLabel("Tổng cộng: 0 đ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(COL_PRIMARY);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlButtons.setOpaque(false);

        JButton btnClear = new JButton("Xóa Giỏ Hàng");
        styleButton(btnClear, new Color(220, 53, 69)); // Màu đỏ cảnh báo
        btnClear.addActionListener(e -> clearCart());

        JButton btnCheckout = new JButton("THANH TOÁN");
        styleButton(btnCheckout, new Color(46, 204, 113)); // Màu xanh lá uy tín
        btnCheckout.setPreferredSize(new Dimension(150, 40));
        btnCheckout.addActionListener(e -> handleCheckout());

        pnlButtons.add(btnClear);
        pnlButtons.add(btnCheckout);

        pnlBottom.add(lblTotal, BorderLayout.WEST);
        pnlBottom.add(pnlButtons, BorderLayout.EAST);

        // --- GẮN VÀO DIALOG ---
        add(pnlHeader, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadCartData() {
        cartModel.setRowCount(0);
        tongTienTruocThue = BigDecimal.ZERO;

        Map<Integer, Integer> gioHang = UserSession.getGioHang();

        if (gioHang == null || gioHang.isEmpty()) {
            lblTotal.setText("Giỏ hàng trống!");
            return;
        }

        int stt = 1;
        for (Map.Entry<Integer, Integer> entry : gioHang.entrySet()) {
            int maSach = entry.getKey();
            int soLuong = entry.getValue();

            // Gọi BUS để lấy thông tin chi tiết của cuốn sách
            SachDTO sach = sachBUS.getById(maSach);
            if (sach != null) {
                BigDecimal donGia = sach.getGiaBan();
                BigDecimal thanhTien = donGia.multiply(new BigDecimal(soLuong));
                tongTienTruocThue = tongTienTruocThue.add(thanhTien);

                cartModel.addRow(new Object[]{
                        stt++,
                        sach.getTenSach(),
                        df.format(donGia),
                        soLuong,
                        df.format(thanhTien)
                });
            }
        }

        lblTotal.setText("Tổng thanh toán: " + df.format(tongTienTruocThue));
    }

    private void clearCart() {
        if (UserSession.getGioHang().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa toàn bộ sản phẩm trong giỏ?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.xoaGioHang();
            loadCartData();
        }
    }

    private void handleCheckout() {
        if (UserSession.getGioHang().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống! Vui lòng chọn sách trước khi thanh toán.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận thanh toán hóa đơn trị giá: " + df.format(tongTienTruocThue) + " ?",
                "Xác Nhận Đặt Hàng", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            // 🔥 GỌI BUS ĐỂ LƯU VÀO DATABASE
            bus.HoaDonBUS hdBus = new bus.HoaDonBUS();
            int maKhachHang = UserSession.getCurrentUser().getMaTaiKhoan();

            // Truyền Mã KH, Giỏ hàng (Map<MaSach, SoLuong>) và Tổng tiền xuống BUS
            String msg = hdBus.taoDonHangOnline(maKhachHang, UserSession.getGioHang(), tongTienTruocThue);

            if (msg.contains("Thành công")) {
                JOptionPane.showMessageDialog(this, "🎉 Đặt hàng thành công!\nĐơn hàng của bạn đang chờ nhân viên duyệt.");
                UserSession.xoaGioHang(); // Xóa RAM sau khi lưu DB thành công
                this.dispose(); // Đóng popup Giỏ hàng
            } else {
                JOptionPane.showMessageDialog(this, msg, "Lỗi Đặt Hàng", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(255, 143, 183));
        table.setSelectionForeground(COL_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(COL_CREAM);
        header.setForeground(COL_DARK);
        header.setPreferredSize(new Dimension(0, 35));

        // Căn phải cho Đơn giá, Số lượng, Thành tiền
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(40);  // STT
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên sách
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35));
    }
}