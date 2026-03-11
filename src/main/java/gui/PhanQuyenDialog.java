package gui;

import bus.PhanQuyenBUS;
import dto.PhanQuyenDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PhanQuyenDialog extends JDialog {

    // Khai báo lại bảng màu cho đồng bộ
    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private JTextField txtMaCode, txtTenQuyen, txtMoTa;
    private JButton btnLuu, btnHuy;
    private PhanQuyenBUS pqBUS;
    private PhanQuyenDTO currentPQ;

    public PhanQuyenDialog(Frame owner, PhanQuyenDTO pq, PhanQuyenBUS bus) {
        super(owner, pq == null ? "THÊM NHÓM QUYỀN" : "SỬA NHÓM QUYỀN", true);
        this.currentPQ = pq;
        this.pqBUS = bus;
        initUI();
        if (pq != null) fillData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- FORM NHẬP LIỆU ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. MaCode
        addLabel(pnlForm, gbc, "Mã Code (VD: ADMIN):", 0);
        txtMaCode = new JTextField(20);
        txtMaCode.setPreferredSize(new Dimension(250, 35));
        // Khóa mã code hệ thống (1-4)
        if (currentPQ != null && currentPQ.getMaQuyen() >= 1 && currentPQ.getMaQuyen() <= 4) {
            txtMaCode.setEditable(false);
            txtMaCode.setBackground(new Color(245, 245, 245));
            txtMaCode.setToolTipText("Mã Code hệ thống không được phép sửa!");
        }
        addComponent(pnlForm, gbc, txtMaCode, 0);

        // 2. TenQuyen
        addLabel(pnlForm, gbc, "Tên Nhóm Quyền:", 1);
        txtTenQuyen = new JTextField(20);
        txtTenQuyen.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, txtTenQuyen, 1);

        // 3. MoTa
        addLabel(pnlForm, gbc, "Mô tả chức năng:", 2);
        txtMoTa = new JTextField(20);
        txtMoTa.setPreferredSize(new Dimension(250, 35));
        addComponent(pnlForm, gbc, txtMoTa, 2);

        // --- PANEL NÚT BẤM (ĐÃ CHỈNH MÀU) ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlButtons.setBackground(new Color(248, 249, 250));

        // Nút Hủy (Màu xám Sidebar)
        btnHuy = createStyledButton("Hủy", COL_SIDEBAR);

        // Nút Lưu (Màu hồng Primary)
        btnLuu = createStyledButton("Xác Nhận", COL_PRIMARY);

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnLuu);

        add(pnlForm, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> handleSave());

        pack();
        setLocationRelativeTo(getOwner());
    }

    // Hàm phụ trợ để tạo Button đẹp, đỡ phải lặp code
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); // Bỏ viền mặc định
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hiệu ứng bàn tay
        return btn;
    }

    private void handleSave() {
        String code = txtMaCode.getText().trim();
        String ten = txtTenQuyen.getText().trim();
        String mota = txtMoTa.getText().trim();

        if (ten.isEmpty() || code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã Code và Tên Quyền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentPQ == null) {
            PhanQuyenDTO pq = new PhanQuyenDTO();
            pq.setMaCode(code);
            pq.setTenQuyen(ten);
            pq.setMoTa(mota);
            String res = pqBUS.addPhanQuyen(pq);
            JOptionPane.showMessageDialog(this, res);
            if (res.contains("Thành công")) dispose();
        } else {
            currentPQ.setMaCode(code);
            currentPQ.setTenQuyen(ten);
            currentPQ.setMoTa(mota);
            String res = pqBUS.updatePhanQuyen(currentPQ);
            JOptionPane.showMessageDialog(this, res);
            if (res.contains("Thành công")) dispose();
        }
    }

    private void fillData() {
        txtMaCode.setText(currentPQ.getMaCode());
        txtTenQuyen.setText(currentPQ.getTenQuyen());
        txtMoTa.setText(currentPQ.getMoTa());
    }

    private void addLabel(JPanel p, GridBagConstraints g, String t, int row) {
        g.gridx = 0; g.gridy = row;
        JLabel lbl = new JLabel(t);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl, g);
    }

    private void addComponent(JPanel p, GridBagConstraints g, JComponent c, int row) {
        g.gridx = 1; g.gridy = row;
        p.add(c, g);
    }
}