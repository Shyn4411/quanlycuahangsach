package gui;

import bus.KhachHangBUS;
import dto.KhachHangDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class KhachHangDialog extends JDialog {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private JTextField txtHoTen, txtSDT, txtDiaChi, txtDiem, txtUpdatedAt, txtMatKhau;
    private JButton btnLuu, btnHuy;

    private KhachHangDTO khachHang;
    private KhachHangBUS khBUS;
    private KhachHangGUI parentGUI;

    public KhachHangDialog(Frame owner, KhachHangGUI parentGUI, KhachHangDTO kh, KhachHangBUS bus) {
        super(owner, kh == null ? "Thêm Khách Hàng Mới" : "Cập Nhật Khách Hàng", true);
        this.parentGUI = parentGUI;
        this.khachHang = kh;
        this.khBUS = bus;

        initUI();
        loadDataToForm();
        initEvents();
    }

    private void initUI() {

        setSize(420, 480); // Tăng chiều cao xíu cho vừa 6 hàng
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Tăng lên 6 hàng
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 10, 20));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(20, 20, 20, 20));

        pnlForm.add(new JLabel("Họ tên (*):"));
        txtHoTen = new JTextField();
        pnlForm.add(txtHoTen);

        pnlForm.add(new JLabel("Số điện thoại (User):"));
        txtSDT = new JTextField();
        pnlForm.add(txtSDT);

        // Ô Mật khẩu mới thêm
        pnlForm.add(new JLabel("Mật khẩu cấp mới:"));
        txtMatKhau = new JTextField();
        pnlForm.add(txtMatKhau);

        pnlForm.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pnlForm.add(txtDiaChi);

        pnlForm.add(new JLabel("Điểm tích lũy:"));
        txtDiem = new JTextField("0");
        txtDiem.setEnabled(false);
        pnlForm.add(txtDiem);

        pnlForm.add(new JLabel("Cập nhật lần cuối:"));
        txtUpdatedAt = new JTextField("Khách hàng mới");
        txtUpdatedAt.setEnabled(false);
        txtUpdatedAt.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlForm.add(txtUpdatedAt);

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlAction.setBackground(new Color(245, 245, 250));

        btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHuy.setBackground(COL_SIDEBAR);
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.setBorderPainted(false);
        btnHuy.setOpaque(true);

        btnLuu = new JButton("Lưu Thông Tin");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLuu.setBackground(COL_PRIMARY); // Nút lưu cho màu hồng nổi bật
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setOpaque(true);

        pnlAction.add(btnHuy);
        pnlAction.add(btnLuu);

        add(pnlAction, BorderLayout.SOUTH);
    }

    private void loadDataToForm() {
        if (khachHang != null) {
            txtHoTen.setText(khachHang.getHoTen());
            txtSDT.setText(khachHang.getSoDienThoai());
            txtDiaChi.setText(khachHang.getDiaChi() != null ? khachHang.getDiaChi() : "");
            txtDiem.setText(String.valueOf(khachHang.getDiemTichLuy()));

            // Khóa ô mật khẩu nếu là Cập nhật Khách Hàng
            txtMatKhau.setText("********");
            txtMatKhau.setEnabled(false);
            txtMatKhau.setToolTipText("Đổi mật khẩu trong mục Quản lý Tài Khoản");

            if (khachHang.getUpdatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
                txtUpdatedAt.setText(khachHang.getUpdatedAt().format(formatter));
            } else {
                txtUpdatedAt.setText(khachHang.getNgayTaoFormat());
            }
        }
    }

    private void initEvents() {
        btnHuy.addActionListener(e -> dispose());

        btnLuu.addActionListener(e -> {
            String ten = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            String matKhau = txtMatKhau.getText().trim();

            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Họ tên và Số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String msg = "";
            if (khachHang == null) {
                // Kiểm tra Mật khẩu khi tạo mới
                if (matKhau.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng cấp mật khẩu cho tài khoản của khách hàng này!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                KhachHangDTO newKh = new KhachHangDTO();
                newKh.setHoTen(ten);
                newKh.setSoDienThoai(sdt);
                newKh.setDiaChi(diaChi.isEmpty() ? null : diaChi);

                // TRUYỀN MẬT KHẨU VÀO HÀM BUS
                msg = khBUS.addKhachHang(newKh, matKhau);
            } else {
                khachHang.setHoTen(ten);
                khachHang.setSoDienThoai(sdt);
                khachHang.setDiaChi(diaChi.isEmpty() ? null : diaChi);

                msg = khBUS.updateKhachHang(khachHang);
            }

            JOptionPane.showMessageDialog(this, msg);

            if (msg.toLowerCase().contains("thành công")) {
                parentGUI.loadDataToTable();
                dispose();
            }
        });
    }
}