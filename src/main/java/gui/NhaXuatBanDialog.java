package gui;

import dto.NhaXuatBanDTO;
import bus.NhaXuatBanBUS;
import enums.TrangThaiCoBan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NhaXuatBanDialog extends JDialog {

    private JTextField txtTenNXB;
    private JComboBox<TrangThaiCoBan> cbxTrangThai; // Dùng Enum trực tiếp cho máu!
    private JButton btnLuu, btnHuy;

    private NhaXuatBanBUS nxbBUS = new NhaXuatBanBUS();
    private NhaXuatBanDTO currentNXB;
    private boolean isEditMode = false;

    final Color COL_PRIMARY = new Color(232, 60, 145);

    public NhaXuatBanDialog(Frame owner, NhaXuatBanDTO nxb) {
        super(owner, "Thông tin Nhà Xuất Bản", true);
        this.currentNXB = nxb;
        this.isEditMode = (nxb != null);
        initUI();
        fillData();
    }

    private void initUI() {
        setSize(400, 300); // Trở về kích thước compact
        setLayout(new BorderLayout());
        setLocationRelativeTo(getOwner());

        JPanel pnlMain = new JPanel(new GridLayout(2, 1, 10, 20));
        pnlMain.setBorder(new EmptyBorder(30, 40, 30, 40));
        pnlMain.setBackground(Color.WHITE);

        // --- Tên NXB ---
        JPanel pnlTen = new JPanel(new BorderLayout(0, 5));
        pnlTen.setOpaque(false);
        pnlTen.add(new JLabel("Tên Nhà Xuất Bản:"), BorderLayout.NORTH);
        txtTenNXB = new JTextField();
        txtTenNXB.setPreferredSize(new Dimension(0, 35));
        pnlTen.add(txtTenNXB, BorderLayout.CENTER);

        // --- Trạng Thái ---
        JPanel pnlStatus = new JPanel(new BorderLayout(0, 5));
        pnlStatus.setOpaque(false);
        pnlStatus.add(new JLabel("Trạng Thái:"), BorderLayout.NORTH);
        cbxTrangThai = new JComboBox<>(TrangThaiCoBan.values());
        cbxTrangThai.setPreferredSize(new Dimension(0, 35));
        pnlStatus.add(cbxTrangThai, BorderLayout.CENTER);

        pnlMain.add(pnlTen);
        if (isEditMode) {
            pnlMain.add(pnlStatus);
        } else {
            pnlMain.add(new JPanel() {{ setOpaque(false); }});
        }

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlButtons.setBackground(new Color(245, 245, 245));
        btnHuy = new JButton("Hủy Bỏ");
        btnLuu = new JButton(isEditMode ? "Cập Nhật" : "Thêm Mới");
        styleButton(btnHuy, Color.GRAY);
        styleButton(btnLuu, COL_PRIMARY);

        pnlButtons.add(btnHuy); pnlButtons.add(btnLuu);
        add(pnlMain, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> xuLyLuu());
    }

    private void fillData() {
        if (isEditMode && currentNXB != null) {
            txtTenNXB.setText(currentNXB.getTenNXB());
            cbxTrangThai.setSelectedItem(currentNXB.getTrangThai());
        }
    }

    private void xuLyLuu() {
        String ten = txtTenNXB.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên NXB không được để trống!"); return;
        }

        String message = "";
        if (!isEditMode) {
            NhaXuatBanDTO newNXB = new NhaXuatBanDTO();
            newNXB.setTenNXB(ten);
            newNXB.setTrangThai(TrangThaiCoBan.HoatDong);
            message = nxbBUS.addNhaXuatBan(newNXB);
        } else {
            currentNXB.setTenNXB(ten);
            currentNXB.setTrangThai((TrangThaiCoBan) cbxTrangThai.getSelectedItem());
            message = nxbBUS.updateNhaXuatBan(currentNXB);
        }

        JOptionPane.showMessageDialog(this, message);
        if (message.startsWith("Thành công")) dispose();
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false); btn.setFocusPainted(false);
    }
}