package gui;

import bus.SachBUS;
import dto.SachDTO;
import dto.TaiKhoanDTO;
import session.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class StoreGUI extends JPanel {

    private final Color COL_PRIMARY = new Color(232, 60, 145); // Hồng
    private final Color COL_DARK = new Color(67, 51, 76);      // Tím xám
    private final Color COL_CREAM = new Color(248, 244, 236);  // Kem nền

    private TaiKhoanDTO currentUser;
    private JLabel lblCartCount;
    private JPanel pnlProductGrid;
    private DecimalFormat df = new DecimalFormat("#,### đ");

    // Lớp BUS để lấy dữ liệu thật
    private SachBUS sachBUS = new SachBUS();

    // Biến đếm giỏ hàng (Sau này thay bằng size của List Giỏ Hàng)
    private int cartItems = 0;

    public StoreGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initHeader();
        initMainContent();
    }

    // ==========================================
    // 1. THANH ĐIỀU HƯỚNG (HEADER)
    // ==========================================
    private void initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout(20, 0));
        pnlHeader.setBackground(COL_DARK);
        pnlHeader.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel lblLogo = new JLabel("📚 BOOKSTORE");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlSearch.setOpaque(false);

        JTextField txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(300, 35));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.WHITE, 1),
                new EmptyBorder(5, 10, 5, 10)));

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(COL_PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setPreferredSize(new Dimension(100, 35));
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlSearch.add(txtSearch);
        pnlSearch.add(btnSearch);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        pnlRight.setOpaque(false);

        JButton btnCart = new JButton("🛒 Giỏ hàng (0)");
        btnCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCart.setForeground(Color.WHITE);
        btnCart.setContentAreaFilled(false);
        btnCart.setBorderPainted(false);
        btnCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCart.addActionListener(e -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);

            CartGUI cartPopup = new CartGUI(parentFrame);
            cartPopup.setVisible(true);

            int totalItems = UserSession.getTongSoMonTrongGio();
            btnCart.setText("🛒 Giỏ hàng (" + totalItems + ")");
        });

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(COL_PRIMARY);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            UserSession.logout();
            SwingUtilities.getWindowAncestor(this).dispose();
            new LoginGUI().setVisible(true);
        });

        pnlRight.add(btnCart);
        pnlRight.add(btnLogout);

        pnlHeader.add(lblLogo, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.CENTER);
        pnlHeader.add(pnlRight, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);
    }

    // ==========================================
    // 2. KHU VỰC HIỂN THỊ SÁCH (DỮ LIỆU THẬT)
    // ==========================================
    private void initMainContent() {
        pnlProductGrid = new JPanel(new GridLayout(0, 4, 20, 20));
        pnlProductGrid.setBackground(COL_CREAM);
        pnlProductGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        List<SachDTO> listSach = sachBUS.getAll();

        if (listSach != null && !listSach.isEmpty()) {
            for (SachDTO sach : listSach) {
                if (sach.getTrangThai() == enums.TrangThaiSach.DANG_BAN && sach.getSoLuongTon() > 0) {
                    pnlProductGrid.add(createBookCard(sach));
                }
            }
        } else {
            JLabel lblEmpty = new JLabel("Hiện chưa có sản phẩm nào được bày bán.");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            pnlProductGrid.add(lblEmpty);
        }

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(COL_CREAM);
        wrapperPanel.add(pnlProductGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        pnlProductGrid.revalidate();
        pnlProductGrid.repaint();
    }

    // ==========================================
    // 3. THIẾT KẾ THẺ SÁCH (Nhận vào 1 đối tượng SachDTO)
    // ==========================================
    private JPanel createBookCard(SachDTO sach) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Xử lý hình ảnh sách (Nếu có link ảnh thì load, không thì để placeholder)
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(150, 200));
        lblImage.setOpaque(true);
        lblImage.setBackground(new Color(245, 245, 245));

        if (sach.getHinhAnh() != null && !sach.getHinhAnh().isEmpty()) {
            try {
                // Đọc ảnh từ thư mục (Ví dụ: src/img/books/...)
                ImageIcon icon = new ImageIcon(sach.getHinhAnh());
                Image img = icon.getImage().getScaledInstance(140, 190, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                lblImage.setText("Lỗi Ảnh");
            }
        } else {
            lblImage.setText("Chưa có ảnh");
            lblImage.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblImage.setForeground(Color.GRAY);
        }

        // Thông tin sách
        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBackground(Color.WHITE);

        // Tên sách (Cắt chuỗi nếu quá dài để không vỡ layout)
        String tenSach = sach.getTenSach();
        if (tenSach.length() > 22) tenSach = tenSach.substring(0, 20) + "...";
        JLabel lblTitle = new JLabel(tenSach);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(COL_DARK);
        lblTitle.setToolTipText(sach.getTenSach()); // Hover để xem full tên

        JLabel lblCode = new JLabel("Mã: S" + String.format("%03d", sach.getMaSach()));
        lblCode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCode.setForeground(Color.GRAY);

        // Chú ý: Thay getDonGia() bằng tên hàm get Giá Bán thực tế trong DTO của ông
        JLabel lblPrice = new JLabel(df.format(sach.getGiaBan()));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrice.setForeground(COL_PRIMARY);

        pnlInfo.add(lblTitle);
        pnlInfo.add(Box.createRigidArea(new Dimension(0, 3)));
        pnlInfo.add(lblCode);
        pnlInfo.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlInfo.add(lblPrice);

        // Nút Thêm vào giỏ
        JButton btnAddCart = new JButton("Thêm vào giỏ");
        btnAddCart.setBackground(COL_PRIMARY);
        btnAddCart.setForeground(Color.WHITE);
        btnAddCart.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddCart.setFocusPainted(false);
        btnAddCart.setBorderPainted(false);
        btnAddCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddCart.setPreferredSize(new Dimension(0, 35));

        // Logic thêm vào giỏ hàng
        // Xử lý nút Thêm vào giỏ
        btnAddCart.addActionListener(e -> {
            // Gọi session lưu vào RAM (1 cuốn)
            UserSession.themVaoGio(sach.getMaSach(), 1);

            // Lấy tổng số lượng để update lên thanh Navbar
            int totalItems = UserSession.getTongSoMonTrongGio();

            // Cập nhật lại Text của nút Giỏ Hàng trên Header
            Component[] comps = ((JPanel)((JPanel)getComponent(0)).getComponent(2)).getComponents();
            if(comps[0] instanceof JButton) {
                ((JButton)comps[0]).setText("🛒 Giỏ hàng (" + totalItems + ")");
            }

            // Hiệu ứng chớp tắt
            btnAddCart.setText("Đã thêm ✔");
            btnAddCart.setBackground(new Color(46, 204, 113));

            Timer timer = new Timer(1000, evt -> {
                btnAddCart.setText("Thêm vào giỏ");
                btnAddCart.setBackground(COL_PRIMARY);
            });
            timer.setRepeats(false);
            timer.start();
        });

        card.add(lblImage, BorderLayout.NORTH);
        card.add(pnlInfo, BorderLayout.CENTER);
        card.add(btnAddCart, BorderLayout.SOUTH);

        return card;
    }
}