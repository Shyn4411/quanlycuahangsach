package gui;

import dto.TacGiaDTO;
import bus.TacGiaBUS;
import enums.TrangThaiCoBan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TacGiaDialog extends JDialog {

    private JTextField txtTenTacGia;
    private JComboBox<TrangThaiCoBan> cbxTrangThai;
    private JButton btnLuu, btnHuy;

    private TacGiaBUS tgBUS = new TacGiaBUS();
    private TacGiaDTO currentTG;
    private boolean isEditMode = false;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    public TacGiaDialog(Frame owner, TacGiaDTO tg) {
        super(owner, "Thông tin Tác Giả", true);
        this.currentTG = tg;
        this.isEditMode = (tg != null);
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

        JPanel pnlTen = new JPanel(new BorderLayout(0, 5));
        pnlTen.setOpaque(false);
        pnlTen.add(new JLabel("Tên Tác Giả:"), BorderLayout.NORTH);
        txtTenTacGia = new JTextField();
        txtTenTacGia.setPreferredSize(new Dimension(0, 35));
        txtTenTacGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlTen.add(txtTenTacGia, BorderLayout.CENTER);

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
        if (isEditMode && currentTG != null) {
            txtTenTacGia.setText(currentTG.getTenTacGia());
            // FIX 3: Gán thẳng giá trị Enum cho ComboBox
            cbxTrangThai.setSelectedItem(currentTG.getTrangThai());
        }
    }

    private void xuLyLuu() {
        String ten = txtTenTacGia.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên tác giả không được để trống!");
            return;
        }

        String message = "";
        if (!isEditMode) {
            TacGiaDTO newTG = new TacGiaDTO();
            newTG.setTenTacGia(ten);
            newTG.setTrangThai(TrangThaiCoBan.HOAT_DONG);

            message = tgBUS.addTacGia(newTG);
        } else {
            // --- LOGIC CẬP NHẬT ---
            currentTG.setTenTacGia(ten);
            // FIX 5: Ép kiểu item được chọn về TrangThaiCoBan
            currentTG.setTrangThai((TrangThaiCoBan) cbxTrangThai.getSelectedItem());

            message = tgBUS.updateTacGia(currentTG);
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