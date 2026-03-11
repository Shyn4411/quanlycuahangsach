package gui;

import bus.KhuyenMaiBUS;
import dto.KhuyenMaiDTO;
import enums.TrangThaiKhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class KhuyenMaiDialog extends JDialog {

    private KhuyenMaiGUI parentGUI;
    private KhuyenMaiBUS kmBUS;
    private KhuyenMaiDTO kmDTO;

    private JTextField txtMaCode, txtTenKM, txtPhanTram, txtSoTienGiam, txtDonHangMin, txtNgayBD, txtNgayKT;
    private JComboBox<TrangThaiKhuyenMai> cbxTrangThai;

    private final Color CLR_SIDEBAR = new Color(67, 51, 76);
    private final Color CLR_ACTIVE  = new Color(232, 60, 145);

    // TẠO BỘ ĐỊNH DẠNG NGÀY THÁNG CHUẨN VIỆT NAM (Ngày/Tháng/Năm)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public KhuyenMaiDialog(Frame owner, KhuyenMaiGUI parent, KhuyenMaiDTO kmDTO, KhuyenMaiBUS bus) {
        super(owner, kmDTO == null ? "THÊM KHUYẾN MÃI MỚI" : "CHI TIẾT KHUYẾN MÃI", true);
        this.parentGUI = parent;
        this.kmDTO = kmDTO;
        this.kmBUS = bus;

        setSize(700, 400);
        setLocationRelativeTo(owner);
        initComponents();

        if (kmDTO != null) fillData();
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBackground(Color.WHITE);
        pnlMain.setBorder(new EmptyBorder(20, 30, 10, 30));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.0; pnlInput.add(new JLabel("Mã Code (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3; txtMaCode = createStyledTextField(); pnlInput.add(txtMaCode, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0; pnlInput.add(new JLabel("Tên CT (*):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7; txtTenKM = createStyledTextField(); pnlInput.add(txtTenKM, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0.0; pnlInput.add(new JLabel("% Giảm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3; txtPhanTram = createStyledTextField(); txtPhanTram.setText("0"); pnlInput.add(txtPhanTram, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0; pnlInput.add(new JLabel("Số tiền giảm:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7; txtSoTienGiam = createStyledTextField(); txtSoTienGiam.setText("0"); pnlInput.add(txtSoTienGiam, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0; gbc.weightx = 0.0; pnlInput.add(new JLabel("Đơn tối thiểu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3; txtDonHangMin = createStyledTextField(); txtDonHangMin.setText("0"); pnlInput.add(txtDonHangMin, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0; pnlInput.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7; cbxTrangThai = new JComboBox<>(TrangThaiKhuyenMai.values()); cbxTrangThai.setBackground(Color.WHITE); pnlInput.add(cbxTrangThai, gbc);

        // Hàng 4: Cập nhật nhãn để người dùng biết nhập theo định dạng nào
        gbc.gridy = 3;
        gbc.gridx = 0; gbc.weightx = 0.0; pnlInput.add(new JLabel("Bắt đầu (dd/mm/yyyy):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3; txtNgayBD = createStyledTextField();
        txtNgayBD.setText(LocalDate.now().format(dateFormatter)); // Định dạng ngày hiện tại
        pnlInput.add(txtNgayBD, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0; pnlInput.add(new JLabel("Kết thúc (dd/mm/yyyy):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7; txtNgayKT = createStyledTextField();
        txtNgayKT.setText(LocalDate.now().plusMonths(1).format(dateFormatter)); // Định dạng tháng tới
        pnlInput.add(txtNgayKT, gbc);

        pnlMain.add(pnlInput, BorderLayout.CENTER);

        // ================= KHU VỰC NÚT BẤM =================
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBtns.setBackground(Color.WHITE);
        pnlBtns.setBorder(new EmptyBorder(15, 0, 10, 0));

        JButton btnLuu = new JButton(kmDTO == null ? "TẠO MỚI" : "CẬP NHẬT");
        styleButton(btnLuu, CLR_ACTIVE);
        JButton btnHuy = new JButton("HỦY BỎ");
        styleButton(btnHuy, Color.GRAY);

        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        pnlMain.add(pnlBtns, BorderLayout.SOUTH);

        add(pnlMain);

        // ================= EVENTS =================
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> handlingSave());
    }

    private void fillData() {
        txtMaCode.setText(kmDTO.getMaCode());
        txtTenKM.setText(kmDTO.getTenKM());

        // FIX: Bỏ phần thập phân, ép cứng hiển thị về số nguyên bằng .intValue()
        txtPhanTram.setText(String.valueOf(kmDTO.getPhanTramGiam().intValue()));
        txtSoTienGiam.setText(String.valueOf(kmDTO.getSoTienGiam().intValue()));
        txtDonHangMin.setText(String.valueOf(kmDTO.getDonHangToiThieu().intValue()));

        // FIX: Hiển thị ngày tháng theo định dạng dd/MM/yyyy
        txtNgayBD.setText(kmDTO.getNgayBatDau().format(dateFormatter));
        txtNgayKT.setText(kmDTO.getNgayKetThuc().format(dateFormatter));
        cbxTrangThai.setSelectedItem(kmDTO.getTrangThai());
    }

    private void handlingSave() {
        if (txtMaCode.getText().trim().isEmpty() || txtTenKM.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã Code và Tên chương trình!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        try {
            KhuyenMaiDTO kmToSave = new KhuyenMaiDTO();
            if (kmDTO != null) kmToSave.setMaKM(kmDTO.getMaKM());

            kmToSave.setMaCode(txtMaCode.getText().trim());
            kmToSave.setTenKM(txtTenKM.getText().trim());

            String strPhanTram = txtPhanTram.getText().trim().replace(",", "");
            String strTienGiam = txtSoTienGiam.getText().trim().replace(",", "");
            String strDonMin = txtDonHangMin.getText().trim().replace(",", "");

            BigDecimal phanTram = new BigDecimal(strPhanTram);
            BigDecimal tienGiam = new BigDecimal(strTienGiam);
            BigDecimal donMin = new BigDecimal(strDonMin);

            if(phanTram.compareTo(BigDecimal.ZERO) < 0 || tienGiam.compareTo(BigDecimal.ZERO) < 0 || donMin.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Các giá trị (%, Tiền) không được là số âm!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }

            kmToSave.setPhanTramGiam(phanTram);
            kmToSave.setSoTienGiam(tienGiam);
            kmToSave.setDonHangToiThieu(donMin);

            // FIX: Đọc ngày tháng nhập vào theo định dạng dd/MM/yyyy thay vì YYYY-MM-DD
            kmToSave.setNgayBatDau(LocalDate.parse(txtNgayBD.getText().trim(), dateFormatter));
            kmToSave.setNgayKetThuc(LocalDate.parse(txtNgayKT.getText().trim(), dateFormatter));
            kmToSave.setTrangThai((TrangThaiKhuyenMai) cbxTrangThai.getSelectedItem());

            if(kmToSave.getNgayKetThuc().isBefore(kmToSave.getNgayBatDau())) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc không được nhỏ hơn ngày bắt đầu!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }

            String msg = (kmDTO == null) ? kmBUS.addKhuyenMai(kmToSave) : kmBUS.updateKhuyenMai(kmToSave);
            JOptionPane.showMessageDialog(this, msg);

            if (msg.toLowerCase().contains("thành công")) {
                parentGUI.loadData();
                this.dispose();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Nhập ngày theo định dạng Ngày/Tháng/Năm (Ví dụ: 31/12/2026)", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createStyledTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5)));
        txt.setPreferredSize(new Dimension(150, 35));
        return txt;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}