package gui;

import bus.CauHinhBUS;
import dto.TaiKhoanDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HeThongGUI extends JPanel {

    private JTextField txtTenShop, txtDiaChi, txtHotline, txtVat;
    private JButton btnLuu;
    private CauHinhBUS bus = new CauHinhBUS();

    private TaiKhoanDTO currentUser;
    public HeThongGUI(TaiKhoanDTO user) {
        this.currentUser = user;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(40, 100, 40, 100));

        // --- TIÊU ĐỀ ---
        JLabel lblHeader = new JLabel("CẤU HÌNH THÔNG TIN CỬA HÀNG", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(67, 51, 76));
        lblHeader.setBorder(new EmptyBorder(0, 0, 30, 0));

        // --- FORM NHẬP LIỆU ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(232, 60, 145)),
                " Thông tin hiển thị trên hóa đơn ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(232, 60, 145)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(15, 20, 15, 20);
        g.fill = GridBagConstraints.HORIZONTAL;

        addInput(pnlForm, g, "Tên nhà sách:", txtTenShop = new JTextField(), 0);
        addInput(pnlForm, g, "Địa chỉ:", txtDiaChi = new JTextField(), 1);
        addInput(pnlForm, g, "Số điện thoại (Hotline):", txtHotline = new JTextField(), 2);
        addInput(pnlForm, g, "Thuế VAT mặc định (%):", txtVat = new JTextField(), 3);

        // --- PANEL NÚT BẤM (ĐÃ CHỈNH SỬA) ---
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlAction.setBackground(Color.WHITE);
        pnlAction.setBorder(new EmptyBorder(30, 0, 0, 0)); // Tạo khoảng cách với Form

        btnLuu = new JButton("LƯU CẤU HÌNH HỆ THỐNG");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(232, 60, 145));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(300, 45)); // Đặt chiều rộng cố định thay vì giãn hết
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false); // Bỏ viền mặc định

        // Hiệu ứng Hover (Rê chuột vào đổi màu)
        btnLuu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLuu.setBackground(new Color(200, 40, 120));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLuu.setBackground(new Color(232, 60, 145));
            }
        });

        pnlAction.add(btnLuu);

        add(lblHeader, BorderLayout.NORTH);
        add(pnlForm, BorderLayout.CENTER);
        add(pnlAction, BorderLayout.SOUTH); // Thay đổi từ btnLuu sang pnlAction

        btnLuu.addActionListener(e -> handleSave());
    }

    private void addInput(JPanel p, GridBagConstraints g, String label, JTextField txt, int row) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.3;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(l, g);

        g.gridx = 1; g.weightx = 0.7;
        txt.setPreferredSize(new Dimension(0, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(txt, g);
    }

    private void loadData() {
        Map<String, String> data = bus.getAll();
        txtTenShop.setText(data.get("TEN_SHOP"));
        txtDiaChi.setText(data.get("DIA_CHI"));
        txtHotline.setText(data.get("HOTLINE"));
        txtVat.setText(data.get("VAT"));
    }

    private void handleSave() {
        Map<String, String> data = new HashMap<>();
        data.put("TEN_SHOP", txtTenShop.getText());
        data.put("DIA_CHI", txtDiaChi.getText());
        data.put("HOTLINE", txtHotline.getText());
        data.put("VAT", txtVat.getText());

        String msg = bus.saveConfig(data);
        JOptionPane.showMessageDialog(this, msg);
    }
}