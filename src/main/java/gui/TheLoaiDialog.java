package gui;

import dto.TheLoaiDTO;
import bus.TheLoaiBUS;
import enums.TrangThaiCoBan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TheLoaiDialog extends JDialog {

    private JTextField txtTenLoai;
    // FIX 1: Dùng trực tiếp kiểu Enum cho JComboBox
    private JComboBox<TrangThaiCoBan> cbxTrangThai;
    private JButton btnLuu, btnHuy;

    private TheLoaiBUS tlBUS = new TheLoaiBUS();
    private TheLoaiDTO currentTL;
    private boolean isEditMode = false;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    public TheLoaiDialog(Frame owner, TheLoaiDTO tl) {
        super(owner, "Thông tin Thể Loại", true);
        this.currentTL = tl;
        this.isEditMode = (tl != null);
        initUI();
        fillData();
    }

    private void initUI() {
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(getOwner());

        JPanel pnlMain = new JPanel(new GridLayout(2, 1, 10, 20));
        pnlMain.setBorder(new EmptyBorder(30, 40, 30, 40));
        pnlMain.setBackground(Color.WHITE);

        // --- Dòng 1: Tên Thể Loại ---
        JPanel pnlTen = new JPanel(new BorderLayout(0, 5));
        pnlTen.setOpaque(false);
        pnlTen.add(new JLabel("Tên Thể Loại:"), BorderLayout.NORTH);
        txtTenLoai = new JTextField();
        txtTenLoai.setPreferredSize(new Dimension(0, 35));
        txtTenLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlTen.add(txtTenLoai, BorderLayout.CENTER);

        // --- Dòng 2: Trạng Thái ---
        JPanel pnlStatus = new JPanel(new BorderLayout(0, 5));
        pnlStatus.setOpaque(false);
        pnlStatus.add(new JLabel("Trạng Thái:"), BorderLayout.NORTH);

        // FIX 2: Đổ thẳng các giá trị của Enum vào ComboBox
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
        styleButton(btnHuy, Color.GRAY);

        btnLuu = new JButton(isEditMode ? "Cập Nhật" : "Thêm Mới");
        styleButton(btnLuu, COL_PRIMARY);

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnLuu);

        add(pnlMain, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> xuLyLuu());
    }

    private void fillData() {
        if (isEditMode && currentTL != null) {
            txtTenLoai.setText(currentTL.getTenLoai());
            // FIX 3: Gán thẳng đối tượng Enum, không cần toString()
            cbxTrangThai.setSelectedItem(currentTL.getTrangThai());
        }
    }

    private void xuLyLuu() {
        String ten = txtTenLoai.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên thể loại không được để trống!");
            return;
        }

        String message = "";
        if (!isEditMode) {
            // --- LOGIC THÊM MỚI ---
            TheLoaiDTO newTL = new TheLoaiDTO();
            newTL.setTenLoai(ten);
            // Mặc định là HoatDong
            newTL.setTrangThai(TrangThaiCoBan.HoatDong);

            message = tlBUS.addTheLoai(newTL);
        } else {
            // --- LOGIC CẬP NHẬT ---
            currentTL.setTenLoai(ten);
            // FIX 4: Lấy thẳng đối tượng Enum từ ComboBox (ép kiểu về TrangThaiCoBan)
            currentTL.setTrangThai((TrangThaiCoBan) cbxTrangThai.getSelectedItem());

            message = tlBUS.updateTheLoai(currentTL);
        }

        JOptionPane.showMessageDialog(this, message);
        if (message.startsWith("Thành công")) {
            dispose();
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}