package gui;

import bus.TaiKhoanBUS;
import dto.TaiKhoanDTO;
import enums.Role;
import session.UserSession;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI extends JFrame {

    private final Color COL_PRIMARY = new Color(232, 60, 145);
    private final Color COL_GRADIENT_1 = new Color(67, 51, 76);
    private final Color COL_GRADIENT_2 = new Color(45, 35, 50);
    private final Color COL_BORDER = new Color(200, 180, 200);
    private final Color COL_BG_CREAM = new Color(248, 244, 236);
    private final Color COL_TEXT_DARK = new Color(67, 51, 76);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Dimension BOX_DIMENSION = new Dimension(320, 42); // Tăng chiều rộng lên chút cho đẹp

    // Khai báo CardLayout để chuyển đổi form
    private JPanel pnlRight;
    private CardLayout cardLayout;

    public LoginGUI() {
        setTitle("Hệ thống Bookstore - Đăng nhập & Đăng ký");
        setSize(900, 550); // Tăng size lên một chút để chứa form đăng ký
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        buildLeftPanel();

        // Thiết lập CardLayout cho Panel bên phải
        cardLayout = new CardLayout();
        pnlRight = new JPanel(cardLayout);

        // Thêm 2 form vào CardLayout
        pnlRight.add(buildLoginForm(), "LOGIN");
        pnlRight.add(buildRegisterForm(), "REGISTER");

        this.add(pnlRight);
    }

    private void buildLeftPanel() {
        JPanel pnlLeft = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, COL_GRADIENT_1, 0, getHeight(), COL_GRADIENT_2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlLeft.setLayout(new GridBagLayout());

        JPanel pnlBrand = new JPanel();
        pnlBrand.setLayout(new BoxLayout(pnlBrand, BoxLayout.Y_AXIS));
        pnlBrand.setOpaque(false);

        JLabel lblIcon = new JLabel("📚");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
        lblIcon.setForeground(new Color(255, 143, 183));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBrandName = new JLabel("BOOKSTORE");
        lblBrandName.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblBrandName.setForeground(Color.WHITE);
        lblBrandName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSlogan = new JLabel("Hệ thống quản lý nhà sách hiện đại");
        lblSlogan.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSlogan.setForeground(new Color(255, 143, 183));
        lblSlogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlBrand.add(lblIcon);
        pnlBrand.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlBrand.add(lblBrandName);
        pnlBrand.add(lblSlogan);

        pnlLeft.add(pnlBrand);
        this.add(pnlLeft);
    }

    // ==========================================
    // FORM ĐĂNG NHẬP
    // ==========================================
    private JPanel buildLoginForm() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(COL_BG_CREAM);

        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setOpaque(false);

        JLabel lblTitle = new JLabel("Chào mừng trở lại!", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COL_TEXT_DARK);

        JLabel lblSubTitle = new JLabel("Vui lòng đăng nhập để tiếp tục", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubTitle.setForeground(new Color(120, 100, 120));

        RoundedTextField txtUser = new RoundedTextField(20);
        RoundedPasswordBox pnlPassBox = new RoundedPasswordBox(20);
        RoundedButton btnLogin = new RoundedButton("ĐĂNG NHẬP", 40);

        // Nút chuyển sang Đăng ký
        JButton btnGoToRegister = createLinkButton("Chưa có tài khoản? Đăng ký ngay");
        btnGoToRegister.addActionListener(e -> cardLayout.show(pnlRight, "REGISTER"));

        ActionListener actionLogin = e -> {
            String user = txtUser.getText().trim();
            String pass = new String(pnlPassBox.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                TaiKhoanBUS bus = new TaiKhoanBUS();
                TaiKhoanDTO tkLogin = bus.login(user, pass);

                UserSession.login(tkLogin);
                Role role = Role.fromRole(tkLogin.getMaQuyen());

                if (role == Role.KHACH_HANG) {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Chào khách hàng " + tkLogin.getTenDangNhap());
                    JFrame storeFrame = new JFrame("Bookstore - Mua sắm");
                    storeFrame.setSize(1100, 700);
                    storeFrame.setLocationRelativeTo(null);
                    storeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    storeFrame.add(new gui.StoreGUI(tkLogin));
                    storeFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                    String roleName = (role == Role.ADMIN) ? "Admin" :
                            (role == Role.NHANVIEN_BANHANG) ? "Nhân Viên Bán Hàng" : "Nhân viên kho";
                    new MainFrame(roleName).setVisible(true);
                }
                this.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                pnlPassBox.clearPassword();
                txtUser.requestFocus();
            }
        };

        btnLogin.addActionListener(actionLogin);
        pnlPassBox.addEnterListener(actionLogin);
        txtUser.addActionListener(actionLogin);

        pnlForm.add(lblTitle);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlForm.add(lblSubTitle);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 30)));
        pnlForm.add(createLabelWithIcon(" Tên đăng nhập", "/gui/icons/user.png"));
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlForm.add(txtUser);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 20)));
        pnlForm.add(createLabelWithIcon(" Mật khẩu", "/gui/icons/lock.png"));
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlForm.add(pnlPassBox);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 35)));
        pnlForm.add(btnLogin);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlForm.add(btnGoToRegister);

        pnl.add(pnlForm);
        return pnl;
    }

    // ==========================================
    // FORM ĐĂNG KÝ
    // ==========================================
    private JPanel buildRegisterForm() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(COL_BG_CREAM);

        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setOpaque(false);

        JLabel lblTitle = new JLabel("Tạo tài khoản mới", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COL_TEXT_DARK);

        RoundedTextField txtRegUser = new RoundedTextField(20);
        RoundedPasswordBox pnlRegPass = new RoundedPasswordBox(20);
        RoundedPasswordBox pnlConfirmPass = new RoundedPasswordBox(20);

        RoundedButton btnRegister = new RoundedButton("ĐĂNG KÝ NGAY", 40);

        JButton btnGoToLogin = createLinkButton("Đã có tài khoản? Đăng nhập");
        btnGoToLogin.addActionListener(e -> cardLayout.show(pnlRight, "LOGIN"));

        btnRegister.addActionListener(e -> {
            String user = txtRegUser.getText().trim();
            String pass = new String(pnlRegPass.getPassword());
            String confirm = new String(pnlConfirmPass.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                TaiKhoanBUS bus = new TaiKhoanBUS();
                String msg = bus.register(user, pass);

                if (msg.equals("Thành công")) {
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    txtRegUser.setText("");
                    pnlRegPass.clearPassword();
                    pnlConfirmPass.clearPassword();
                    cardLayout.show(pnlRight, "LOGIN");
                } else {
                    JOptionPane.showMessageDialog(this, msg, "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlForm.add(lblTitle);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 25)));
        pnlForm.add(createLabelWithIcon(" Tên đăng nhập mới", "/gui/icons/user.png"));
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlForm.add(txtRegUser);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlForm.add(createLabelWithIcon(" Mật khẩu", "/gui/icons/lock.png"));
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlForm.add(pnlRegPass);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlForm.add(createLabelWithIcon(" Xác nhận mật khẩu", "/gui/icons/lock.png"));
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlForm.add(pnlConfirmPass);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 30)));
        pnlForm.add(btnRegister);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlForm.add(btnGoToLogin);

        pnl.add(pnlForm);
        return pnl;
    }

    // ==========================================
    // CÁC HÀM TIỆN ÍCH UI
    // ==========================================
    private JButton createLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        btn.setForeground(COL_PRIMARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createLabelWithIcon(String text, String iconPath) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(COL_TEXT_DARK);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            lbl.setIcon(new ImageIcon(img));
        } catch (Exception e) {}
        return lbl;
    }

    // --- CÁC CLASS CUSTOM UI CỦA ÔNG (GIỮ NGUYÊN) ---
    class RoundedTextField extends JTextField {
        private int radius;
        private Color currentBorderColor = COL_BORDER;

        public RoundedTextField(int radius) {
            this.radius = radius;
            setOpaque(false);
            setFont(FONT_INPUT);
            setBorder(new EmptyBorder(5, 15, 5, 15));
            setPreferredSize(BOX_DIMENSION);
            setMaximumSize(BOX_DIMENSION);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { currentBorderColor = COL_PRIMARY; repaint(); }
                @Override public void focusLost(FocusEvent e) { currentBorderColor = COL_BORDER; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(currentBorderColor);
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
            g2.dispose();
        }
    }

    class RoundedPasswordBox extends JPanel {
        private int radius;
        private Color currentBorderColor = COL_BORDER;
        private JPasswordField txtPass;
        private JToggleButton btnEye;

        public RoundedPasswordBox(int radius) {
            this.radius = radius;
            setLayout(new BorderLayout());
            setOpaque(false);
            setPreferredSize(BOX_DIMENSION);
            setMaximumSize(BOX_DIMENSION);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            txtPass = new JPasswordField();
            txtPass.setOpaque(false);
            txtPass.setFont(FONT_INPUT);
            txtPass.setBorder(new EmptyBorder(5, 15, 5, 0));
            btnEye = new JToggleButton("👁");
            btnEye.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
            btnEye.setForeground(new Color(150, 150, 150));
            btnEye.setBorder(new EmptyBorder(0, 5, 0, 15));
            btnEye.setContentAreaFilled(false);
            btnEye.setFocusPainted(false);
            btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEye.addActionListener(e -> {
                if (btnEye.isSelected()) { txtPass.setEchoChar((char) 0); btnEye.setForeground(COL_PRIMARY); }
                else { txtPass.setEchoChar('•'); btnEye.setForeground(new Color(150, 150, 150)); }
            });
            txtPass.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { currentBorderColor = COL_PRIMARY; repaint(); }
                @Override public void focusLost(FocusEvent e) { currentBorderColor = COL_BORDER; repaint(); }
            });
            add(txtPass, BorderLayout.CENTER);
            add(btnEye, BorderLayout.EAST);
        }
        public char[] getPassword() { return txtPass.getPassword(); }
        public void clearPassword() { txtPass.setText(""); }
        public void addEnterListener(ActionListener a) { txtPass.addActionListener(a); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(currentBorderColor);
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
            g2.dispose();
        }
    }

    class RoundedButton extends JButton {
        private int radius;
        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setPreferredSize(BOX_DIMENSION);
            setMaximumSize(BOX_DIMENSION);
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isRollover()) g2.setColor(new Color(255, 143, 183));
            else g2.setColor(COL_PRIMARY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}