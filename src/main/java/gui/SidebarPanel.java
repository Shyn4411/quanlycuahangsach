package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class SidebarPanel extends JPanel {

    final Color COL_SIDEBAR_START = new Color(67, 51, 76);
    final Color COL_SIDEBAR_END = new Color(45, 35, 50);
    final Color COL_ACTIVE_MENU = new Color(255, 255, 255, 30);
    final Color COL_SUB_MENU_BG = new Color(0, 0, 0, 60);
    final Color COL_TEXT_MENU = Color.WHITE;
    final Color COL_ACCENT_PINK = new Color(255, 143, 183);

    final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 24);
    final Font FONT_GROUP = new Font("Segoe UI", Font.BOLD, 14);
    final Font FONT_ITEM = new Font("Segoe UI", Font.PLAIN, 14);

    private String currentRole;
    private MainFrame parentFrame;

    public SidebarPanel(String role, MainFrame parent) {
        this.currentRole = role;
        this.parentFrame = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);
        pnlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlHeader.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblLogo = new JLabel("BOOKSTORE");
        lblLogo.setFont(FONT_LOGO);
        lblLogo.setForeground(Color.WHITE);
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(100, 2));
        sep.setForeground(new Color(255, 255, 255, 100));
        JLabel lblHello = new JLabel("System Management");
        lblHello.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHello.setForeground(COL_ACCENT_PINK);

        pnlHeader.add(lblLogo);
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlHeader.add(sep);
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlHeader.add(lblHello);
        add(pnlHeader);
        add(Box.createRigidArea(new Dimension(0, 10)));

        add(createSingleMenu("TRANG CHỦ", "/gui/icons/home.png", e -> parentFrame.showDashboard()));
        add(createMenuGroup("HÀNG HÓA", "/gui/icons/box.png", new String[]{"Sách","Tác giả", "Thể loại", "NXB"}));

        if (utils.PermissionUtils.isAdmin()) {
            add(createMenuGroup("GIAO DỊCH", "/gui/icons/transaction.png", new String[]{"Bán hàng", "Khuyến mãi", "Hóa đơn", "Đổi trả hàng", "Nhập hàng","Đổi trả NCC"}));
            add(createMenuGroup("ĐỐI TÁC", "/gui/icons/users.png", new String[]{"Khách hàng", "Nhà cung cấp", "Nhân viên"}));
            add(createMenuGroup("HỆ THỐNG", "/gui/icons/setting.png", new String[]{"Tài khoản", "Phân quyền"}));
            add(createMenuGroup("BÁO CÁO", "/gui/icons/chart.png", new String[]{"Thống kê"}));

        } else if (utils.PermissionUtils.isBanHang()) {
            add(createMenuGroup("GIAO DỊCH", "/gui/icons/transaction.png", new String[]{"Bán hàng", "Khuyến mãi", "Hóa đơn", "Đổi trả hàng"}));
            add(createMenuGroup("ĐỐI TÁC", "/gui/icons/users.png", new String[]{"Khách hàng"}));

        } else if (utils.PermissionUtils.isQuanKho()) {
            add(createMenuGroup("GIAO DỊCH", "/gui/icons/transaction.png", new String[]{"Nhập hàng", "Đổi trả NCC"}));
            add(createMenuGroup("ĐỐI TÁC", "/gui/icons/users.png", new String[]{"Nhà cung cấp"}));
        }

        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, COL_SIDEBAR_START, 0, getHeight(), COL_SIDEBAR_END);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private JPanel createMenuGroup(String title, String iconPath, String[] subItems) {
        JPanel pnlGroup = new JPanel();
        pnlGroup.setLayout(new BoxLayout(pnlGroup, BoxLayout.Y_AXIS));
        pnlGroup.setOpaque(false);

        JButton btnHeader = createSidebarButton(title, iconPath, true);

        JPanel pnlSubItems = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(COL_SUB_MENU_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        pnlSubItems.setLayout(new BoxLayout(pnlSubItems, BoxLayout.Y_AXIS));
        pnlSubItems.setOpaque(false);
        pnlSubItems.setVisible(false);

        for (String item : subItems) {
            JButton btnSub = createSidebarButton("   •  " + item, null, false);
            btnSub.addActionListener(e -> {

                if (item.equals("Sách")) {
                    parentFrame.switchPanel(new SachGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Tác giả")) {
                    parentFrame.switchPanel(new TacGiaGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Thể loại")) {
                    parentFrame.switchPanel(new TheLoaiGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("NXB")) {
                    parentFrame.switchPanel(new NhaXuatBanGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Bán hàng")) {
                    parentFrame.switchPanel(new BanHangGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Khuyến mãi")) {
                    parentFrame.switchPanel(new KhuyenMaiGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Hóa đơn")) {
                    parentFrame.switchPanel(new HoaDonGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Đổi trả hàng")) {
                    parentFrame.switchPanel(new DoiTraGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Nhập hàng")) {
                    parentFrame.switchPanel(new PhieuNhapGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Đổi trả NCC")) {
                    parentFrame.switchPanel(new DoiTraNhaCungCapGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Khách hàng")) {
                    parentFrame.switchPanel(new KhachHangGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Nhà cung cấp")) {
                    parentFrame.switchPanel(new NhaCungCapGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Nhân viên")) {
                    parentFrame.switchPanel(new NhanVienGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Tài khoản")) {
                    parentFrame.switchPanel(new TaiKhoanGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Phân quyền")) {
                    parentFrame.switchPanel(new PhanQuyenGUI(session.UserSession.getCurrentUser()));
                }
                else if (item.equals("Thống kê")) {
                    parentFrame.switchPanel(new ThongKeGUI(session.UserSession.getCurrentUser()));
                }
            });
            pnlSubItems.add(btnSub);
        }
        btnHeader.addActionListener(e -> {
            pnlSubItems.setVisible(!pnlSubItems.isVisible());
            pnlGroup.revalidate();
            SwingUtilities.getWindowAncestor(pnlGroup).repaint();
        });
        pnlGroup.add(btnHeader);
        pnlGroup.add(pnlSubItems);
        return pnlGroup;
    }

    private JButton createSingleMenu(String title, String iconPath, ActionListener action) {
        JButton btn = createSidebarButton(title, iconPath, true);
        if (action != null) btn.addActionListener(action);
        return btn;
    }

    private JButton createSidebarButton(String text, String iconPath, boolean isHeader) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover() || getModel().isPressed()) {
                    g.setColor(COL_ACTIVE_MENU);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(COL_TEXT_MENU);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(12, 25, 12, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (iconPath != null && !iconPath.isEmpty()) {
            btn.setIcon(getResizedIcon(iconPath));
            btn.setIconTextGap(15);
        }

        btn.setFont(isHeader ? FONT_GROUP : FONT_ITEM);
        return btn;
    }

    private ImageIcon getResizedIcon(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}