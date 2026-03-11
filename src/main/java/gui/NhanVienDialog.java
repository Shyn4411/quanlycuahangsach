package gui;

import bus.NhanVienBUS;
import dto.NhanVienDTO;
import enums.Role;
import enums.TrangThaiTaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NhanVienDialog extends JDialog {

    private JTextField txtHoTen, txtSdt, txtUser;
    private JPasswordField txtPass;
    private JToggleButton btnEye; // Nút con mắt
    private JComboBox<Role> cbxRole;
    private JButton btnLuu, btnHuy;

    private NhanVienBUS nvBUS;
    private NhanVienDTO currentNV;

    public NhanVienDialog(Frame owner, NhanVienDTO nv, NhanVienBUS bus) {
        super(owner, nv == null ? "THÊM NHÂN VIÊN MỚI" : "CẬP NHẬT THÔNG TIN", true);
        this.currentNV = nv;
        this.nvBUS = bus;
        initUI();
        if (nv != null) fillData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabel(pnlForm, gbc, "Họ tên nhân viên:", 0);
        txtHoTen = new JTextField(20);
        txtHoTen.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, txtHoTen, 0);

        addLabel(pnlForm, gbc, "Số điện thoại:", 1);
        txtSdt = new JTextField(20);
        txtSdt.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, txtSdt, 1);

        addLabel(pnlForm, gbc, "Chức vụ / Quyền:", 2);
        cbxRole = new JComboBox<>();
        for (Role r : Role.values()) {
            if (r != Role.KHACH_HANG) cbxRole.addItem(r);
        }
        cbxRole.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Role) value = ((Role) value).getTenChucVu();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        cbxRole.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, cbxRole, 2);


        addLabel(pnlForm, gbc, "Tên đăng nhập:", 3);
        txtUser = new JTextField(20);
        txtUser.setPreferredSize(new Dimension(250, 35));
        if (currentNV != null) {
            txtUser.setEditable(false);
            txtUser.setBackground(new Color(245, 245, 245));
            txtUser.setToolTipText("Không thể đổi tên đăng nhập!");
        }
        addComponent(pnlForm, gbc, txtUser, 3);

        // HIỂN THỊ Ô MẬT KHẨU TRONG CẢ 2 TRƯỜNG HỢP (THÊM VÀ SỬA)
        addLabel(pnlForm, gbc, currentNV == null ? "Mật khẩu:" : "Mật khẩu hiện tại:", 4);

        // Tạo một Panel nhỏ để chứa cả ô nhập Pass và nút Con mắt
        JPanel pnlPass = new JPanel(new BorderLayout());
        pnlPass.setOpaque(false);
        txtPass = new JPasswordField(20);
        txtPass.setPreferredSize(new Dimension(210, 35));

        btnEye = new JToggleButton("👁");
        btnEye.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        btnEye.setPreferredSize(new Dimension(40, 35));
        btnEye.setFocusPainted(false);
        btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Logic Bật/Tắt xem mật khẩu
        btnEye.addActionListener(e -> {
            if (btnEye.isSelected()) {
                txtPass.setEchoChar((char) 0); // Hiện chữ
                btnEye.setForeground(new Color(232, 60, 145)); // Đổi màu hường cho biết là đang soi
            } else {
                txtPass.setEchoChar('•'); // Ẩn chữ thành dấu chấm
                btnEye.setForeground(Color.BLACK);
            }
        });

        pnlPass.add(txtPass, BorderLayout.CENTER);
        pnlPass.add(btnEye, BorderLayout.EAST);

        addComponent(pnlForm, gbc, pnlPass, 4);

        // --- NÚT BẤM ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlButtons.setBackground(new Color(248, 249, 250));

        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setPreferredSize(new Dimension(100, 35));

        btnLuu = new JButton("Xác Nhận");
        btnLuu.setPreferredSize(new Dimension(120, 35));
        btnLuu.setBackground(new Color(232, 60, 145));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnLuu);

        add(pnlForm, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(this::handleSave);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void handleSave(ActionEvent e) {
        // 1. Thu thập dữ liệu
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim(); // Lấy pass
        Role selectedRole = (Role) cbxRole.getSelectedItem();

        // 2. Validate cơ bản
        if (hoTen.isEmpty() || sdt.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường bắt buộc (kể cả mật khẩu)!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Đóng gói dữ liệu vào DTO
        if (currentNV == null) {
            // Thêm mới
            NhanVienDTO nvMoi = new NhanVienDTO();
            nvMoi.setHoTen(hoTen);
            nvMoi.setSoDienThoai(sdt);
            nvMoi.setTenDangNhap(user);
            nvMoi.setMatKhau(pass);
            nvMoi.setMaQuyen(selectedRole.getMaQuyen());
            nvMoi.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);

            String res = nvBUS.addNhanVien(nvMoi);
            JOptionPane.showMessageDialog(this, res);
            if (res.contains("Thành công")) dispose();
        } else {
            // Cập nhật (Gắn thêm pass vào để update)
            currentNV.setHoTen(hoTen);
            currentNV.setSoDienThoai(sdt);
            currentNV.setMaQuyen(selectedRole.getMaQuyen());
            currentNV.setMatKhau(pass); // Ghi đè pass mới nếu Admin có sửa

            String res = nvBUS.updateNhanVien(currentNV);
            JOptionPane.showMessageDialog(this, res);
            if (res.contains("Thành công")) dispose();
        }
    }

    private void fillData() {
        txtHoTen.setText(currentNV.getHoTen());
        txtSdt.setText(currentNV.getSoDienThoai());
        txtUser.setText(currentNV.getTenDangNhap());
        // Hiển thị mật khẩu cũ lên ô txtPass
        txtPass.setText(currentNV.getMatKhau());
        cbxRole.setSelectedItem(Role.fromRole(currentNV.getMaQuyen()));
    }

    private void addLabel(JPanel p, GridBagConstraints g, String text, int row) {
        g.gridx = 0; g.gridy = row;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl, g);
    }

    private void addComponent(JPanel p, GridBagConstraints g, JComponent comp, int row) {
        g.gridx = 1; g.gridy = row;
        p.add(comp, g);
    }
}