package gui;

import bus.KhuyenMaiBUS;
import dto.KhuyenMaiDTO;
import enums.TrangThaiKhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class KhuyenMaiGUI extends JPanel {

    private KhuyenMaiBUS kmBUS = new KhuyenMaiBUS();
    private JTable tblKM;
    private DefaultTableModel modelKM;
    private JTextField txtMaCode, txtTenKM, txtPhanTram, txtSoTienGiam, txtDonHangMin, txtNgayBD, txtNgayKT;
    private JComboBox<TrangThaiKhuyenMai> cbxTrangThai;
    private int selectedMaKM = -1;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    public KhuyenMaiGUI() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 244, 236));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlInput = new JPanel(new GridLayout(4, 4, 15, 15));
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230)), new EmptyBorder(15, 15, 15, 15)));

        pnlInput.add(new JLabel("Mã Code:")); txtMaCode = new JTextField(); pnlInput.add(txtMaCode);
        pnlInput.add(new JLabel("Tên CT:")); txtTenKM = new JTextField(); pnlInput.add(txtTenKM);
        pnlInput.add(new JLabel("% Giảm:")); txtPhanTram = new JTextField("0"); pnlInput.add(txtPhanTram);
        pnlInput.add(new JLabel("Số tiền giảm:")); txtSoTienGiam = new JTextField("0"); pnlInput.add(txtSoTienGiam);
        pnlInput.add(new JLabel("Đơn tối thiểu:")); txtDonHangMin = new JTextField("0"); pnlInput.add(txtDonHangMin);
        pnlInput.add(new JLabel("Ngày bắt đầu:")); txtNgayBD = new JTextField(LocalDate.now().toString()); pnlInput.add(txtNgayBD);
        pnlInput.add(new JLabel("Ngày kết thúc:")); txtNgayKT = new JTextField(LocalDate.now().plusMonths(1).toString()); pnlInput.add(txtNgayKT);
        pnlInput.add(new JLabel("Trạng thái:")); cbxTrangThai = new JComboBox<>(TrangThaiKhuyenMai.values()); pnlInput.add(cbxTrangThai);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBtns.setOpaque(false);
        JButton btnAdd = new JButton("THÊM MỚI"); styleButton(btnAdd, COL_PRIMARY);
        JButton btnUpdate = new JButton("CẬP NHẬT"); styleButton(btnUpdate, COL_SIDEBAR);
        JButton btnReset = new JButton("LÀM MỚI"); styleButton(btnReset, Color.GRAY);
        pnlBtns.add(btnAdd); pnlBtns.add(btnUpdate); pnlBtns.add(btnReset);

        JPanel pnlNorth = new JPanel(new BorderLayout(0, 15));
        pnlNorth.setOpaque(false);
        pnlNorth.add(pnlInput, BorderLayout.CENTER);
        pnlNorth.add(pnlBtns, BorderLayout.SOUTH);

        String[] cols = {"Mã", "Code", "Tên Chương Trình", "% Giảm", "Tiền Giảm", "Đơn Min", "Hết Hạn", "Trạng Thái"};
        modelKM = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKM = new JTable(modelKM);
        styleTable(tblKM);

        add(pnlNorth, BorderLayout.NORTH);
        add(new JScrollPane(tblKM), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handlingAdd());
        btnUpdate.addActionListener(e -> handlingUpdate());
        btnReset.addActionListener(e -> resetForm());
        tblKM.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    private void loadData() {
        modelKM.setRowCount(0);
        List<KhuyenMaiDTO> list = kmBUS.getAll();
        for (KhuyenMaiDTO km : list) {
            modelKM.addRow(new Object[]{
                    km.getMaKM(), km.getMaCode(), km.getTenKM(),
                    km.getPhanTramGiam() + "%",
                    String.format("%,.0f", km.getSoTienGiam()),
                    String.format("%,.0f", km.getDonHangToiThieu()),
                    km.getNgayKetThuc(), km.getTrangThai()
            });
        }
    }

    private void fillForm() {
        int row = tblKM.getSelectedRow();
        if (row != -1) {
            selectedMaKM = (int) tblKM.getValueAt(row, 0);
            txtMaCode.setText(tblKM.getValueAt(row, 1).toString());
            txtTenKM.setText(tblKM.getValueAt(row, 2).toString());
            txtPhanTram.setText(tblKM.getValueAt(row, 3).toString().replace("%", ""));
            txtSoTienGiam.setText(tblKM.getValueAt(row, 4).toString().replace(",", ""));
            txtDonHangMin.setText(tblKM.getValueAt(row, 5).toString().replace(",", ""));
            txtNgayBD.setText(kmBUS.getAll().get(row).getNgayBatDau().toString());
            txtNgayKT.setText(tblKM.getValueAt(row, 6).toString());
            cbxTrangThai.setSelectedItem(tblKM.getValueAt(row, 7));
        }
    }

    private void handlingAdd() {
        KhuyenMaiDTO km = getDTOFromForm();
        if (km == null) return;
        JOptionPane.showMessageDialog(this, kmBUS.addKhuyenMai(km));
        loadData();
    }

    private void handlingUpdate() {
        if (selectedMaKM == -1) { JOptionPane.showMessageDialog(this, "Chọn dòng để sửa!"); return; }
        KhuyenMaiDTO km = getDTOFromForm();
        if (km == null) return;
        km.setMaKM(selectedMaKM);
        JOptionPane.showMessageDialog(this, kmBUS.updateKhuyenMai(km));
        loadData();
    }

    private KhuyenMaiDTO getDTOFromForm() {
        try {
            KhuyenMaiDTO km = new KhuyenMaiDTO();
            km.setMaCode(txtMaCode.getText());
            km.setTenKM(txtTenKM.getText());
            km.setPhanTramGiam(new BigDecimal(txtPhanTram.getText()));
            km.setSoTienGiam(new BigDecimal(txtSoTienGiam.getText()));
            km.setDonHangToiThieu(new BigDecimal(txtDonHangMin.getText()));
            km.setNgayBatDau(LocalDate.parse(txtNgayBD.getText()));
            km.setNgayKetThuc(LocalDate.parse(txtNgayKT.getText()));
            km.setTrangThai((TrangThaiKhuyenMai) cbxTrangThai.getSelectedItem());
            return km;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ! Kiểm tra số và định dạng ngày (yyyy-MM-dd)");
            return null;
        }
    }

    private void resetForm() {
        txtMaCode.setText(""); txtTenKM.setText(""); txtPhanTram.setText("0");
        txtSoTienGiam.setText("0"); txtDonHangMin.setText("0");
        txtNgayBD.setText(LocalDate.now().toString());
        txtNgayKT.setText(LocalDate.now().plusMonths(1).toString());
        selectedMaKM = -1;
        tblKM.clearSelection();
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(232, 240, 255));
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}