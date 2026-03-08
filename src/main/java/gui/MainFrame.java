package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_CONTENT = new Color(248, 244, 236);
    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_WHITE = Color.WHITE;

    final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 22);
    final Font FONT_MENU = new Font("Segoe UI", Font.BOLD, 14);
    final Font FONT_CARD_VAL = new Font("Segoe UI", Font.BOLD, 24);

    private JPanel mainContentPanel;
    private dto.TaiKhoanDTO tkDangNhap;
    private String tenChucVu;

    public MainFrame(dto.TaiKhoanDTO tk) {
        this.tkDangNhap = tk;

        if (tk.getMaQuyen() == 1) {
            this.tenChucVu = "Quản trị viên";
        } else if (tk.getMaQuyen() == 2) {
            this.tenChucVu = "Nhân viên bán hàng";
        } else if (tk.getMaQuyen() == 3) {
            this.tenChucVu = "Nhân viên kho";
        } else {
            this.tenChucVu = "Chưa xác định";
        }

        initUI();
    }

    private void initUI() {
        setTitle("BOOKSTORE MANAGEMENT SYSTEM");
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        JPanel rightArea = new JPanel(new BorderLayout());
        rightArea.add(createTopBar(), BorderLayout.NORTH);

        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(createDashboardBody(), BorderLayout.CENTER);

        rightArea.add(mainContentPanel, BorderLayout.CENTER);
        add(rightArea, BorderLayout.CENTER);
    }

    private void switchPanel(JPanel newPanel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // ==========================================
    // KHU VỰC CHỈNH SỬA SIDEBAR
    // ==========================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COL_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0)); // Cho rộng ra chút để hiển thị menu con cho đẹp

        JLabel lblLogo = new JLabel("BOOKSTORE", SwingConstants.LEFT);
        lblLogo.setFont(FONT_LOGO);
        lblLogo.setForeground(COL_WHITE);
        lblLogo.setBorder(new EmptyBorder(15, 20, 15, 20));
        sidebar.add(lblLogo);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(separator);
        sidebar.add(Box.createVerticalStrut(10));

        // --- CÁC MENU CHÍNH ---
        sidebar.add(createMenuBtn("Dashboard", true));

        if (tkDangNhap.getMaQuyen() == 1 || tkDangNhap.getMaQuyen() == 2) {
            sidebar.add(createMenuBtn("Khách hàng", false));
            sidebar.add(createMenuBtn("Bán hàng", false));
            sidebar.add(createMenuBtn("Đơn hàng", false));
        }

        if (tkDangNhap.getMaQuyen() == 1 || tkDangNhap.getMaQuyen() == 3) {
            // Nút Mẹ: Quản lý sách
            JButton btnQuanLySach = createMenuBtn("Quản lý sách ▾", false);

            // Container chứa 4 nút Menu con
            JPanel pnlSubMenu = new JPanel();
            pnlSubMenu.setLayout(new BoxLayout(pnlSubMenu, BoxLayout.Y_AXIS));
            pnlSubMenu.setBackground(COL_SIDEBAR); // Cùng màu nền với sidebar
            pnlSubMenu.setVisible(false); // Mặc định ẩn đi

            // 4 Nút con
            JButton btnSach = createSubMenuBtn("Danh sách Sách");
            JButton btnTheLoai = createSubMenuBtn("Thể Loại");
            JButton btnTacGia = createSubMenuBtn("Tác Giả");
            JButton btnNXB = createSubMenuBtn("Nhà Xuất Bản");

            // Bắt sự kiện cho 4 nút con mở Panel tương ứng
            btnSach.addActionListener(e -> switchPanel(new SachGUI(tkDangNhap)));
            btnTheLoai.addActionListener(e -> switchPanel(new TheLoaiGUI(tkDangNhap)));
            btnTacGia.addActionListener(e -> switchPanel(new TacGiaGUI(tkDangNhap)));
            btnNXB.addActionListener(e -> switchPanel(new NhaXuatBanGUI(tkDangNhap)));

            // Add các nút con vào container
            pnlSubMenu.add(btnSach);
            pnlSubMenu.add(btnTheLoai);
            pnlSubMenu.add(btnTacGia);
            pnlSubMenu.add(btnNXB);

            // Bắt sự kiện cho Nút Mẹ (Click để xổ ra / thu vào)
            btnQuanLySach.addActionListener(e -> {
                boolean isVisible = pnlSubMenu.isVisible();
                pnlSubMenu.setVisible(!isVisible); // Đảo ngược trạng thái ẩn/hiện

                // Đổi icon mũi tên
                if (!isVisible) {
                    btnQuanLySach.setText("Quản lý sách ▴");
                } else {
                    btnQuanLySach.setText("Quản lý sách ▾");
                }

                // Bắt buộc giao diện vẽ lại
                sidebar.revalidate();
                sidebar.repaint();
            });

            // Add cả nút mẹ và container con vào Sidebar
            sidebar.add(btnQuanLySach);
            sidebar.add(pnlSubMenu);

            sidebar.add(createMenuBtn("Nhập hàng", false));
            sidebar.add(createMenuBtn("Lịch sử kho", false));
        }

        if (tkDangNhap.getMaQuyen() == 1) {
            sidebar.add(createMenuBtn("Thống kê", false));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = createMenuBtn("Đăng xuất", false);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginGUI().setVisible(true);
            }
        });
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    // NÚT MENU CHÍNH
    private JButton createMenuBtn(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 55));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btn.setFont(FONT_MENU);
        btn.setForeground(COL_WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 30, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Bỏ bớt lệnh addActionListener của "Quản lý sách" ở dưới này đi vì đã xử lý ở trên
        btn.addActionListener(e -> {
            if (text.contains("Dashboard")) {
                switchPanel(createDashboardBody());
            } else if (text.contains("Khách hàng")) {
                switchPanel(new KhachHangGUI());
            } else if (text.contains("Bán hàng")) {
                switchPanel(new BanHangGUI(tkDangNhap));
            } else if (text.contains("Đơn hàng")) {
                switchPanel(new HoaDonGUI(tkDangNhap));
            } else if (text.contains("Nhập hàng")) {
                switchPanel(new PhieuNhapGUI(tkDangNhap));
            } else if (text.contains("Lịch sử kho")) {
                switchPanel(new LichSuKhoGUI());
            } else if (text.contains("Thống kê")) {
                switchPanel(new ThongKeGUI());
            }
        });
        return btn;
    }

    // NÚT MENU CON (SUB-MENU)
    private JButton createSubMenuBtn(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Chữ nhỏ hơn, không in đậm
        btn.setForeground(new Color(220, 220, 220)); // Chữ hơi xám nhẹ để phân biệt với nút mẹ
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 50, 0, 0)); // Thụt lề sâu hơn (Nút chính là 30, nút này là 50)
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Làm hiệu ứng hover (Di chuột vào thì chữ sáng lên)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(COL_WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(220, 220, 220));
            }
        });

        return btn;
    }

    // ==========================================
    // CÁC HÀM TẠO GIAO DIỆN KHÁC (GIỮ NGUYÊN)
    // ==========================================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COL_WHITE);
        topBar.setPreferredSize(new Dimension(0, 61));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        topBar.setBorder(new EmptyBorder(0, 10, 0, 10));

        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/home.png"));

        JButton btnDashboard = new JButton(icon);
        btnDashboard.setBorderPainted(false);
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setFocusPainted(false);
        btnDashboard.setOpaque(false);
        btnDashboard.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel pnlUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlUser.setOpaque(false);
        JLabel lblUser = new JLabel(tenChucVu + " (" + tkDangNhap.getTenDangNhap() + ")");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ImageIcon iconUser = new ImageIcon(getClass().getResource("/icons/user.png"));
        JLabel lblUserImage = new JLabel(iconUser);
        pnlUser.add(lblUserImage);
        pnlUser.add(lblUser);

        topBar.add(btnDashboard, BorderLayout.WEST);
        topBar.add(pnlUser, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createDashboardBody() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(COL_BG_CONTENT);
        body.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 70));

        pnlCards.add(createStatCard("Tổng sách", "1,200", "/icons/book.png"));
        pnlCards.add(createStatCard("Tổng đơn", "350", "/icons/order.png"));
        pnlCards.add(createStatCard("Khách hàng", "280", "/icons/customer.png"));
        pnlCards.add(createStatCard("Doanh thu", "56.2M", "/icons/revenue.png"));

        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BorderLayout(0, 20));
        centerContent.setOpaque(false);

        JPanel tableSection = createTableSection();
        tableSection.setPreferredSize(new Dimension(0, 270));

        JPanel chartSection = createChartSection();
        chartSection.setPreferredSize(new Dimension(0, 220));

        centerContent.add(tableSection, BorderLayout.NORTH);
        centerContent.add(chartSection, BorderLayout.CENTER);

        body.add(pnlCards, BorderLayout.NORTH);
        body.add(centerContent, BorderLayout.CENTER);

        return body;
    }

    private JPanel createStatCard(String title, String value, String iconPath) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(COL_WHITE);
        card.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        JLabel lblIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            lblIcon.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblIcon.setText("Image");
        }

        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        lblIcon.setPreferredSize(new Dimension(70, 0));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setOpaque(true);
        lblIcon.setBackground(Color.WHITE);

        JPanel pnlText = new JPanel(new GridLayout(2, 1));
        pnlText.setOpaque(false);
        JLabel lblT = new JLabel(title); lblT.setForeground(Color.GRAY);
        JLabel lblV = new JLabel(value); lblV.setFont(FONT_CARD_VAL);
        pnlText.add(lblT); pnlText.add(lblV);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(pnlText, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableSection() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setBackground(COL_WHITE);
        pnl.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Danh sách Sách");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnAdd = new JButton("Thêm sách >");
        btnAdd.setBackground(COL_BG_CONTENT); btnAdd.setForeground(COL_SIDEBAR);
        btnAdd.setFocusPainted(false);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(btnAdd, BorderLayout.EAST);

        String[] columns = {"ID", "Tên sách", "Tác giả", "Giá", "Tồn kho"};
        Object[][] data = {
                {"1", "Java Programming", "James Gosling", "150,000đ", "45"},
                {"2", "Clean Code", "Robert C. Martin", "180,000đ", "30"},
                {"3", "The Alchemist", "Paulo Coelho", "120,000đ", "20"},
                {"4", "Design Patterns", "Erich Gamma", "200,000đ", "25"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 40));

        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        return pnl;
    }

    private JPanel createChartSection() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(COL_WHITE);
        pnl.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("Thống kê Doanh thu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel chartMock = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 240, 245));
                g.fillRect(50, 20, getWidth()-100, getHeight()-70);
                g.setColor(COL_PRIMARY);
                int[] vals = {30, 50, 80, 40, 90, 120, 60};
                for(int i=0; i<vals.length; i++) {
                    g.fillRect(100 + i*60, getHeight()-50-vals[i], 30, vals[i]);
                }
            }
        };
        chartMock.setPreferredSize(new Dimension(0, 200));

        pnl.add(lblTitle, BorderLayout.NORTH);
        pnl.add(chartMock, BorderLayout.CENTER);
        return pnl;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            dto.TaiKhoanDTO tkTest = new dto.TaiKhoanDTO();
            tkTest.setTenDangNhap("Admin_Tun");
            tkTest.setMaQuyen(1);
            new MainFrame(tkTest).setVisible(true);
        });
    }
}