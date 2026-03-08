package gui;

import bus.TaiKhoanBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginGUI extends JFrame {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private TaiKhoanBUS taiKhoanBUS =  new TaiKhoanBUS();

    public LoginGUI() {
        initUI();
        initEvents();
    }

    private void initUI() {
        setTitle("Đăng Nhập Hệ Thống");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBackground(COL_SIDEBAR);
        pnlLeft.setPreferredSize(new Dimension(300, 450));

        JLabel lblLogo = new JLabel("BOOKSTORE", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblLogo.setForeground(Color.WHITE);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/icons/book-shop.png"));
            Image scaledImg = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(scaledImg));

            lblLogo.setHorizontalTextPosition(JLabel.CENTER);
            lblLogo.setVerticalTextPosition(JLabel.BOTTOM);
            lblLogo.setIconTextGap(15);

        } catch (Exception e) {
            System.err.println("Vui lòng kiểm tra đường dẫn!");
        }

        pnlLeft.add(lblLogo, BorderLayout.CENTER);


        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(new EmptyBorder(50, 40, 50, 40));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COL_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Vui lòng đăng nhập để tiếp tục");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pnlUser = new JPanel(new BorderLayout(0, 5));
        pnlUser.setBackground(Color.WHITE);
        pnlUser.setMaximumSize(new Dimension(400, 60));
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 35));
        pnlUser.add(lblUser, BorderLayout.NORTH);
        pnlUser.add(txtUsername, BorderLayout.CENTER);


        JPanel pnlPass = new JPanel(new BorderLayout(0, 5));
        pnlPass.setBackground(Color.WHITE);
        pnlPass.setMaximumSize(new Dimension(400, 60));
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 35));
        pnlPass.add(lblPass, BorderLayout.NORTH);
        pnlPass.add(txtPassword, BorderLayout.CENTER);

        btnLogin = new JButton("ĐĂNG NHẬP VÀO HỆ THỐNG");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(COL_PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(400, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlRight.add(lblTitle);
        pnlRight.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlRight.add(lblSub);
        pnlRight.add(Box.createRigidArea(new Dimension(0, 30)));
        pnlRight.add(pnlUser);
        pnlRight.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlRight.add(pnlPass);
        pnlRight.add(Box.createRigidArea(new Dimension(0, 30)));
        pnlRight.add(btnLogin);


        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
    }

    private void initEvents() {
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());

            try {
                // Gọi xuống BUS. Nếu sai pass hoặc bị khóa, nó sẽ văng Exception và nhảy xuống block catch
                dto.TaiKhoanDTO tkLogin = taiKhoanBUS.login(user, pass);

                // Nếu code chạy được xuống đây có nghĩa là Đăng nhập thành công!
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nXin chào: " + tkLogin.getTenDangNhap());

                new MainFrame(tkLogin).setVisible(true);

                this.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Hàm main dùng để chạy thử Form Login này
    public static void main(String[] args) {
        // Đổi giao diện về giống Windows cho đẹp
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        new LoginGUI().setVisible(true);
    }
}