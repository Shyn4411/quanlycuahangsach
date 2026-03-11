package gui;

import bus.NhaCungCapBUS;
import dto.NhaCungCapDTO;
import enums.TrangThaiCoBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NhaCungCapDialog extends JDialog {

    private JTextField txtTenNCC, txtSdt, txtDiaChi;
    private JComboBox<TrangThaiCoBan> cbxTrangThai;
    private JButton btnLuu, btnHuy;

    private NhaCungCapBUS nccBUS;
    private NhaCungCapDTO currentNCC; // null = Thêm mới, !null = Cập nhật

    public NhaCungCapDialog(Frame owner, NhaCungCapDTO ncc, NhaCungCapBUS bus) {
        super(owner, ncc == null ? "THÊM NHÀ CUNG CẤP MỚI" : "SỬA THÔNG TIN NHÀ CUNG CẤP", true);
        this.currentNCC = ncc;
        this.nccBUS = bus;
        initUI();
        if (ncc != null) fillData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- KHUNG NHẬP LIỆU ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Tên Nhà Cung Cấp
        addLabel(pnlForm, gbc, "Tên Nhà Cung Cấp:", 0);
        txtTenNCC = new JTextField(20);
        txtTenNCC.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, txtTenNCC, 0);

        // 2. Số Điện Thoại
        addLabel(pnlForm, gbc, "Số Điện Thoại:", 1);
        txtSdt = new JTextField(20);
        txtSdt.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, txtSdt, 1);

        // 3. Địa Chỉ
        addLabel(pnlForm, gbc, "Địa Chỉ:", 2);
        txtDiaChi = new JTextField(20);
        txtDiaChi.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, txtDiaChi, 2);

        // 4. Trạng Thái
        addLabel(pnlForm, gbc, "Trạng Thái:", 3);
        cbxTrangThai = new JComboBox<>(TrangThaiCoBan.values());
        cbxTrangThai.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, cbxTrangThai, 3);

        // --- KHUNG NÚT BẤM ---
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

        // --- SỰ KIỆN ---
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> handleSave());

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void handleSave() {
        String ten = txtTenNCC.getText().trim();
        String sdt = txtSdt.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        TrangThaiCoBan tt = (TrangThaiCoBan) cbxTrangThai.getSelectedItem();

        if (ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và Số điện thoại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentNCC == null) {
            // Thêm mới
            NhaCungCapDTO nccMoi = new NhaCungCapDTO();
            nccMoi.setTenNCC(ten);
            nccMoi.setSoDienThoai(sdt);
            nccMoi.setDiaChi(diaChi);
            nccMoi.setTrangThai(tt);

            String result = nccBUS.addNhaCungCap(nccMoi);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("Thành công")) dispose();
        } else {
            // Cập nhật
            currentNCC.setTenNCC(ten);
            currentNCC.setSoDienThoai(sdt);
            currentNCC.setDiaChi(diaChi);
            currentNCC.setTrangThai(tt);

            String result = nccBUS.updateNhaCungCap(currentNCC);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("Thành công")) dispose();
        }
    }

    private void fillData() {
        txtTenNCC.setText(currentNCC.getTenNCC());
        txtSdt.setText(currentNCC.getSoDienThoai());
        txtDiaChi.setText(currentNCC.getDiaChi());
        cbxTrangThai.setSelectedItem(currentNCC.getTrangThai());
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