package gui;

import bus.HoaDonBUS;
import bus.SachBUS;
import dto.TaiKhoanDTO;
import session.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.BigDecimal;
import java.util.Map;

public class DashboardPanel extends JPanel {

    final Color COL_BG_CONTENT = new Color(248, 244, 236);
    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR_START = new Color(67, 51, 76);
    final Color COL_ACCENT_PINK = new Color(255, 143, 183);
    final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 15);

    private int hoverBarIndex = -1;
    private JPopupMenu glassMenu;
    private MainFrame parentFrame;

    // Khởi tạo các lớp BUS để lấy dữ liệu thực tế
    private HoaDonBUS hdBUS = new HoaDonBUS();
    private SachBUS sBUS = new SachBUS();

    public DashboardPanel(MainFrame parent, String role) {
        this.parentFrame = parent;
        setLayout(new BorderLayout());
        setBackground(COL_BG_CONTENT);

        if (!role.equals("Admin")) {
            JPanel pnlWelcome = new JPanel(new GridBagLayout());
            pnlWelcome.setBackground(COL_BG_CONTENT);

            // Lấy tên thật từ UserSession
            TaiKhoanDTO currentUser = UserSession.getCurrentUser();
            String username = (currentUser != null) ? currentUser.getTenDangNhap() : role;

            JLabel lblHello = new JLabel("Xin chào " + username + " (" + role + ")! Vui lòng chọn chức năng làm việc.");
            lblHello.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHello.setForeground(new Color(120, 120, 120));
            pnlWelcome.add(lblHello);
            add(pnlWelcome, BorderLayout.CENTER);
            return;
        }

        initAdminDashboard();
    }

    private void initAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COL_BG_CONTENT);
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Tổng quan hệ thống");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));

        // DỮ LIỆU THẬT: Lấy thông tin Admin từ Session
        TaiKhoanDTO currentUser = UserSession.getCurrentUser();
        String adminName = (currentUser != null) ? currentUser.getTenDangNhap() : "Admin";
        String adminID = (currentUser != null) ? "TK" + String.format("%03d", currentUser.getMaTaiKhoan()) : "TK001";

        JPanel pnlAdminInfo = createAdminProfilePanel(adminName, adminID, "Quản trị viên");
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlAdminInfo, BorderLayout.EAST);

        // DỮ LIỆU THẬT: Các thẻ thống kê
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 12, 0));
        pnlCards.setBackground(COL_BG_CONTENT);
        pnlCards.setPreferredSize(new Dimension(0, 140));

        // Gọi BUS lấy số liệu
        BigDecimal doanhThu = hdBUS.getDoanhThuHomNay();
        int donMoi = hdBUS.getSoDonHangMoi();
        int sachSapHet = sBUS.getSoLuongSachSapHet(10); // Cảnh báo dưới 10 cuốn
        BigDecimal loiNhuan = hdBUS.getLoiNhuanHomNay();

        pnlCards.add(createShadowCard("Doanh thu thực hôm nay", formatMoneyShort(doanhThu), donMoi + " đơn hàng mới", COL_PRIMARY, "/gui/icons/money.png"));
        pnlCards.add(createShadowCard("Đơn hàng mới", String.valueOf(donMoi), "Chờ duyệt ngay", new Color(255, 153, 0), "/gui/icons/cart.png"));
        pnlCards.add(createShadowCard("Sách sắp hết", String.format("%02d", sachSapHet), "Cần nhập hàng gấp", new Color(220, 53, 69), "/gui/icons/warning.png"));
        pnlCards.add(createShadowCard("Lợi nhuận gộp", formatMoneyShort(loiNhuan), "Lợi nhuận ước tính", new Color(40, 167, 69), "/gui/icons/profit.png"));

        JPanel pnlMainContent = new JPanel(new BorderLayout(15, 0));
        pnlMainContent.setBackground(COL_BG_CONTENT);
        pnlMainContent.setPreferredSize(new Dimension(0, 500));

        // DỮ LIỆU THẬT: Biểu đồ
        double[] currentMonthlyRevenue = hdBUS.getDoanhThuTheoTuanTrongThang();
        if (currentMonthlyRevenue == null || currentMonthlyRevenue.length < 4) {
            currentMonthlyRevenue = new double[]{0, 0, 0, 0}; // Tránh lỗi null nếu chưa có code ở BUS
        }
        JPanel pnlChart = createChartPanel(currentMonthlyRevenue);
        pnlChart.setPreferredSize(new Dimension(550, 0));

        // DỮ LIỆU THẬT: Top 5 sách
        Map<String, Integer> topBooks = sBUS.getTopSachBanChay(5);
        JPanel pnlTopBooks = createTopListPanel("Top 5 Sách bán chạy tháng này", topBooks);

        pnlMainContent.add(pnlChart, BorderLayout.WEST);
        pnlMainContent.add(pnlTopBooks, BorderLayout.CENTER);

        Box boxContainer = Box.createVerticalBox();
        boxContainer.add(pnlHeader);
        boxContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        boxContainer.add(pnlCards);
        boxContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        boxContainer.add(pnlMainContent);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COL_BG_CONTENT);
        wrapper.add(boxContainer, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createAdminProfilePanel(String name, String id, String role) {
        final boolean[] isProfileHovered = {false};
        final long[] lastHiddenTime = {0};

        JPanel pnlProfile = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isProfileHovered[0] || (glassMenu != null && glassMenu.isVisible())) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(232, 60, 145, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                }
            }
        };
        pnlProfile.setOpaque(false);
        pnlProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlProfile.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel lblAvatar = new JLabel(String.valueOf(name.charAt(0)).toUpperCase()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COL_PRIMARY); g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16)); lblAvatar.setForeground(Color.WHITE);
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER); lblAvatar.setPreferredSize(new Dimension(42, 42));

        JPanel pnlText = new JPanel(); pnlText.setLayout(new BoxLayout(pnlText, BoxLayout.Y_AXIS)); pnlText.setOpaque(false);
        JLabel lblN = new JLabel(name); lblN.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblN.setForeground(COL_SIDEBAR_START);
        JLabel lblSub = new JLabel(role + " | " + id); lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lblSub.setForeground(new Color(130, 130, 130));
        pnlText.add(Box.createVerticalGlue()); pnlText.add(lblN); pnlText.add(lblSub); pnlText.add(Box.createVerticalGlue());

        JLabel lblArrow = new JLabel(" ▼ "); lblArrow.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        pnlProfile.add(lblAvatar, BorderLayout.WEST); pnlProfile.add(pnlText, BorderLayout.CENTER); pnlProfile.add(lblArrow, BorderLayout.EAST);

        int shadowSize = 8;

        glassMenu = new JPopupMenu() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 0; i < shadowSize; i++) {
                    g2.setColor(new Color(0, 0, 0, 4));
                    g2.fillRoundRect(i, i, getWidth() - (i * 2), getHeight() - (i * 2), 20 + (shadowSize - i), 20 + (shadowSize - i));
                }

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(shadowSize, shadowSize, getWidth() - (shadowSize * 2), getHeight() - (shadowSize * 2), 15, 15);
                g2.dispose();
            }
        };
        glassMenu.setOpaque(false);
        glassMenu.setBorder(new EmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));

        glassMenu.add(createMenuRow("Họ tên: ", name, "/gui/icons/user_tag.png", false, false, glassMenu));
        glassMenu.add(createMenuRow("Tài khoản: ", id, "/gui/icons/id_card.png", false, false, glassMenu));
        glassMenu.addSeparator();

        glassMenu.add(createMenuRow("Thông tin cá nhân", "", "/gui/icons/user.png", true, false, glassMenu));
        glassMenu.add(createMenuRow("Đổi mật khẩu", "", "/gui/icons/lock.png", true, false, glassMenu));
        glassMenu.addSeparator();
        glassMenu.add(createMenuRow("Đăng xuất", "", "/gui/icons/logout.png", true, true, glassMenu));

        glassMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
            @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                lastHiddenTime[0] = System.currentTimeMillis();
                pnlProfile.repaint();
            }
            @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        pnlProfile.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { isProfileHovered[0] = true; pnlProfile.repaint(); }
            @Override public void mouseExited(MouseEvent e) { isProfileHovered[0] = false; pnlProfile.repaint(); }
            @Override public void mousePressed(MouseEvent e) {
                if (System.currentTimeMillis() - lastHiddenTime[0] < 100) return;

                int desiredWidth = pnlProfile.getWidth() + (shadowSize * 2);
                glassMenu.setPreferredSize(null);
                glassMenu.setPreferredSize(new Dimension(desiredWidth, glassMenu.getPreferredSize().height));
                glassMenu.show(pnlProfile, -shadowSize, pnlProfile.getHeight() + 2);
                pnlProfile.repaint();
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 20));
        wrapper.add(pnlProfile, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createMenuRow(String label, String value, String iconPath, boolean isAction, boolean isLogout, JPopupMenu parentMenu) {
        final boolean[] isRowHovered = {false};

        JPanel row = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isRowHovered[0] && isAction) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(232, 60, 145, 35));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 15, 10, 15));

        String text = value.isEmpty() ? label : "<html>" + label + "<b>" + value + "</b></html>";
        JLabel lbl = new JLabel(text);
        lbl.setIcon(getResizedIcon(iconPath));
        lbl.setIconTextGap(15);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(isLogout ? Color.RED : COL_SIDEBAR_START);

        row.add(lbl, BorderLayout.WEST);

        if (isAction) {
            row.setCursor(new Cursor(Cursor.HAND_CURSOR));
            row.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isRowHovered[0] = true; row.repaint(); }
                @Override public void mouseExited(MouseEvent e) { isRowHovered[0] = false; row.repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    parentMenu.setVisible(false); // Đóng menu ngay lập tức

                    // LOGIC ĐIỀU HƯỚNG DỰA TRÊN LABEL
                    if (isLogout) {
                        handleLogout();
                    } else if (label.contains("Thông tin cá nhân")) {
                        handleShowProfile();
                    } else if (label.contains("Đổi mật khẩu")) {
                        handleUpdatePassword();
                    }
                }
            });
        }
        return row;
    }

    private ImageIcon getResizedIcon(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) { return null; }
    }

    private JPanel createShadowCard(String title, String value, String sub, Color accentColor, String iconPath) {
        JPanel pnlShadow = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 220, 220)); g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 20, 20);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
            }
        };
        pnlShadow.setBackground(COL_BG_CONTENT); pnlShadow.setBorder(new EmptyBorder(15, 20, 15, 20));
        JPanel pnlText = new JPanel(new GridLayout(3, 1, 0, 5)); pnlText.setOpaque(false);
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblTitle.setForeground(new Color(120, 120, 120));
        JLabel lblValue = new JLabel(value); lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28)); lblValue.setForeground(new Color(50, 50, 50));
        JLabel lblSub = new JLabel(sub); lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblSub.setForeground(accentColor);
        pnlText.add(lblTitle); pnlText.add(lblValue); pnlText.add(lblSub);

        JLabel lblIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            lblIcon.setIcon(new ImageIcon(img));
        } catch (Exception e) {}
        lblIcon.setVerticalAlignment(SwingConstants.TOP);

        JPanel content = new JPanel(new BorderLayout(10, 0)); content.setOpaque(false);
        content.add(pnlText, BorderLayout.CENTER); content.add(lblIcon, BorderLayout.EAST);
        pnlShadow.add(content, BorderLayout.CENTER);

        JPanel line = new JPanel(); line.setPreferredSize(new Dimension(6, 0)); line.setBackground(accentColor);
        JPanel lineContainer = new JPanel(new BorderLayout()); lineContainer.setOpaque(false);
        lineContainer.add(line, BorderLayout.CENTER); lineContainer.setBorder(new EmptyBorder(5, 0, 5, 15));
        pnlShadow.add(lineContainer, BorderLayout.WEST);
        return pnlShadow;
    }

    // NẠP CHỒNG HÀM: Dành cho BigDecimal
    private String formatMoneyShort(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return formatMoneyShort(amount.doubleValue());
    }

    // HÀM GỐC: Dành cho Biểu đồ (vì biểu đồ dùng mảng double[])
    private String formatMoneyShort(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1f tỷ", amount / 1_000_000_000);
        else if (amount >= 1_000_000) return String.format("%.1f tr", amount / 1_000_000);
        else if (amount >= 1_000) return String.format("%.0f k", amount / 1_000);
        else return String.format("%,.0f đ", amount);
    }

    private JPanel createChartPanel(final double[] revenueData) {
        JPanel pnlShadow = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 210, 210)); g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 15, 15);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 15, 15);
            }
        };
        pnlShadow.setBackground(COL_BG_CONTENT); pnlShadow.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel lbl = new JLabel("Doanh thu thực tháng này"); lbl.setFont(FONT_TITLE); lbl.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel pnlChartDrawing = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(); int h = getHeight();
                int bottomPadding = 40; int leftPadding = 30; int topPadding = 30;
                String[] labels = {"Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4"};

                double maxVal = 0;
                for(double d : revenueData) maxVal = Math.max(maxVal, d);
                maxVal = (maxVal == 0) ? 1 : maxVal * 1.2;

                g2.setColor(new Color(245, 245, 245));
                for(int i=0; i<5; i++) {
                    int yLine = h - bottomPadding - (int)((h - bottomPadding - topPadding) * i / 4.0);
                    g2.drawLine(leftPadding, yLine, w - 20, yLine);
                }
                g2.setColor(Color.LIGHT_GRAY); g2.drawLine(leftPadding, h-bottomPadding, w-20, h-bottomPadding);

                int colWidth = 50; int availableWidth = w - leftPadding - 20;
                int gap = (availableWidth - (revenueData.length * colWidth)) / (revenueData.length + 1);

                for(int i=0; i<revenueData.length; i++) {
                    int x = leftPadding + gap + (i * (colWidth + gap));
                    int barH = (int)((revenueData[i] / maxVal) * (h - bottomPadding - topPadding));
                    int y = h - bottomPadding - barH;
                    g2.setColor(new Color(0, 0, 0, 15)); g2.fillRoundRect(x + 3, y + 3, colWidth, barH, 10, 10);
                    if (i == hoverBarIndex) g2.setPaint(new GradientPaint(x, y, COL_ACCENT_PINK, x, y+barH, COL_PRIMARY));
                    else g2.setPaint(new GradientPaint(x, y, COL_PRIMARY, x, y+barH, COL_SIDEBAR_START));
                    g2.fillRoundRect(x, y, colWidth, barH, 10, 10);

                    g2.setColor(new Color(100, 100, 100)); g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2.drawString(labels[i], x + (colWidth - g2.getFontMetrics().stringWidth(labels[i]))/2, h - 15);
                    String valText = formatMoneyShort(revenueData[i]);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, i == hoverBarIndex ? 12 : 11));
                    g2.setColor(i == hoverBarIndex ? new Color(255, 100, 0) : new Color(80, 80, 80));
                    g2.drawString(valText, x + (colWidth - g2.getFontMetrics().stringWidth(valText))/2, y - 5);
                }
            }
        };
        pnlChartDrawing.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int w = pnlChartDrawing.getWidth(); int h = pnlChartDrawing.getHeight();
                int bottomPadding = 40; int topPadding = 30; int leftPadding = 30; int colWidth = 50;
                double maxVal = 0;
                for(double d : revenueData) maxVal = Math.max(maxVal, d);
                maxVal = (maxVal == 0) ? 1 : maxVal * 1.2;

                int gap = (w - leftPadding - 20 - (revenueData.length * colWidth)) / (revenueData.length + 1);
                int foundIndex = -1;
                for(int i=0; i<revenueData.length; i++) {
                    int x = leftPadding + gap + (i * (colWidth + gap));
                    int barH = (int)((revenueData[i] / maxVal) * (h - bottomPadding - topPadding));
                    int y = h - bottomPadding - barH;
                    if (e.getX() >= x && e.getX() <= x + colWidth && e.getY() >= y && e.getY() <= y + barH) { foundIndex = i; break; }
                }
                if (foundIndex != hoverBarIndex) { hoverBarIndex = foundIndex; pnlChartDrawing.repaint(); }
            }
        });
        pnlChartDrawing.setOpaque(false); pnlShadow.add(lbl, BorderLayout.NORTH); pnlShadow.add(pnlChartDrawing, BorderLayout.CENTER);
        return pnlShadow;
    }

    private JPanel createTopListPanel(String title, Map<String, Integer> topBooks) {
        JPanel pnlShadow = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 210, 210)); g2.fillRoundRect(2, 2,getWidth()-4, getHeight()-4, 15, 15);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 15, 15);
            }
        };
        pnlShadow.setBackground(COL_BG_CONTENT); pnlShadow.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel lbl = new JLabel(title); lbl.setFont(FONT_TITLE); lbl.setBorder(new EmptyBorder(10, 0, 10, 0)); pnlShadow.add(lbl, BorderLayout.NORTH);

        JPanel list = new JPanel(); list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS)); list.setOpaque(false);

        if (topBooks == null || topBooks.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có dữ liệu bán hàng tháng này.");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            list.add(lblEmpty);
        } else {
            int maxQty = 1;
            for (int qty : topBooks.values()) { maxQty = Math.max(maxQty, qty); }

            int index = 1;
            for (Map.Entry<String, Integer> entry : topBooks.entrySet()) {
                String bookName = entry.getKey();
                int qty = entry.getValue();
                final int finalMaxQty = maxQty;

                JPanel row = new JPanel(new BorderLayout(10, 0)); row.setOpaque(false); row.setBorder(new EmptyBorder(10, 0, 10, 0));

                String displayName = bookName.length() > 22 ? bookName.substring(0, 22) + "..." : bookName;
                JLabel lblName = new JLabel(index + ". " + displayName);
                lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lblName.setPreferredSize(new Dimension(160, 0));

                JLabel lblCount = new JLabel(qty + " cuốn"); lblCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblCount.setForeground(new Color(100, 100, 100)); lblCount.setPreferredSize(new Dimension(60, 0)); lblCount.setHorizontalAlignment(SwingConstants.RIGHT);

                JPanel pnlBarArea = new JPanel() {
                    @Override protected void paintComponent(Graphics g) {
                        super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(250, 220, 230));
                        int barWidth = (int) (((double) qty / finalMaxQty) * 150);
                        g2.fillRoundRect(0, (getHeight() - 12) / 2, Math.max(barWidth, 5), 12, 10, 10);
                    }
                };
                pnlBarArea.setOpaque(false); pnlBarArea.setPreferredSize(new Dimension(160, 30));
                row.add(lblName, BorderLayout.WEST); row.add(pnlBarArea, BorderLayout.CENTER); row.add(lblCount, BorderLayout.EAST);
                list.add(row);
                index++;
            }
        }
        list.add(Box.createVerticalGlue()); pnlShadow.add(list, BorderLayout.CENTER);
        return pnlShadow;
    }


    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            session.UserSession.logout();
            parentFrame.dispose();
            new LoginGUI().setVisible(true);
        }
    }

    private void handleUpdatePassword() {
        // Tạo Panel chứa 2 ô mật khẩu cho chuyên nghiệp
        JPanel pnl = new JPanel(new GridLayout(2, 2, 5, 5));
        JPasswordField pf1 = new JPasswordField();
        JPasswordField pf2 = new JPasswordField();

        pnl.add(new JLabel("Mật khẩu mới:"));
        pnl.add(pf1);
        pnl.add(new JLabel("Xác nhận lại:"));
        pnl.add(pf2);

        int action = JOptionPane.showConfirmDialog(this, pnl,
                "Đổi mật khẩu tài khoản", JOptionPane.OK_CANCEL_OPTION);

        if (action == JOptionPane.OK_OPTION) {
            String pass = new String(pf1.getPassword());
            String confirm = new String(pf2.getPassword());

            if (pass.length() < 4) {
                JOptionPane.showMessageDialog(this, "Mật khẩu phải từ 4 ký tự trở lên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            bus.TaiKhoanBUS tkBus = new bus.TaiKhoanBUS();
            String msg = tkBus.updatePassword(UserSession.getCurrentUser().getMaTaiKhoan(), pass);

            JOptionPane.showMessageDialog(this, msg);
        }
    }
    private void handleShowProfile() {
        TaiKhoanDTO user = UserSession.getCurrentUser();
        String info = "Tên đăng nhập: " + user.getTenDangNhap() + "\n"
                + "Mã tài khoản: TK" + String.format("%03d", user.getMaTaiKhoan()) + "\n"
                + "Ngày tạo: " + user.getNgayTao();
        JOptionPane.showMessageDialog(this, info, "Thông tin cá nhân", JOptionPane.INFORMATION_MESSAGE);

    }
}