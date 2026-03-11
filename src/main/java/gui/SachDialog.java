package gui;

import bus.SachBUS;
import bus.TheLoaiBUS;
import bus.NhaXuatBanBUS;
import bus.TacGiaBUS;
import dto.SachDTO;
import dto.TaiKhoanDTO;
import dto.TheLoaiDTO;
import dto.NhaXuatBanDTO;
import dto.TacGiaDTO;
import enums.TrangThaiSach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SachDialog extends JDialog {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private JTextField txtTen, txtGiaGoc, txtGiaBan, txtTonKho, txtLoi;
    private JComboBox<ComboItem> cbxLoai, cbxNXB;

    // Khai báo cho phần Tác giả biến hình
    private CardLayout cardTacGia;
    private JPanel pnlTacGiaCards;
    private JTextField txtTacGiaView; // Dùng khi Xem chi tiết
    private JList<ComboItem> listTacGia; // Dùng khi Thêm/Sửa
    private JButton btnAddTacGia;

    private JLabel lblHinhAnh;
    private String selectedImagePath = "";
    private JButton btnLuu, btnHuy, btnChonAnh, btnSuaTrongDialog;

    private SachDTO sach;
    private SachBUS sBUS;
    private TaiKhoanDTO userLogin;

    class ComboItem {
        int id; String name;
        public ComboItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    public SachDialog(Frame owner, SachGUI parentGUI, SachDTO s, SachBUS bus) {
        super(owner, s == null ? "Thêm Sách Mới" : "Cập Nhật Thông Tin Sách", true);
        this.sach = s;
        this.sBUS = bus;

        initUI();
        loadComboboxData();
        loadData();
        initEvents();
    }

    private void initUI() {
        setSize(900, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // --- PANEL TRÁI (ẢNH) ---
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        pnlLeft.setPreferredSize(new Dimension(300, 0));
        pnlLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlLeft.setBackground(Color.WHITE);

        lblHinhAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblHinhAnh.setBorder(new TitledBorder("Ảnh bìa sách"));

        btnChonAnh = new JButton("Chọn ảnh bìa...");
        btnChonAnh.setBackground(COL_PRIMARY); // Màu tím than (67, 51, 76)
        btnChonAnh.setForeground(Color.WHITE);
        btnChonAnh.setOpaque(true);            // ---> THÊM DÒNG NÀY ĐỂ HIỆN MÀU <---
        btnChonAnh.setBorderPainted(false);    // Xóa viền cho nút phẳng đẹp
        btnChonAnh.setFocusPainted(false);

        pnlLeft.add(lblHinhAnh, BorderLayout.CENTER);
        pnlLeft.add(btnChonAnh, BorderLayout.SOUTH);

        // --- PANEL PHẢI (INPUT TỔNG) ---
        JPanel pnlRightMain = new JPanel(new BorderLayout(0, 15));
        pnlRightMain.setBackground(Color.WHITE);
        pnlRightMain.setBorder(new EmptyBorder(20, 10, 20, 20));

        // 1. Nhóm thông tin cơ bản
        JPanel pnlInfo = new JPanel(new GridLayout(4, 2, 10, 15));
        pnlInfo.setBackground(Color.WHITE);

        pnlInfo.add(new JLabel("Tên sách (*):"));
        txtTen = new JTextField();
        pnlInfo.add(txtTen);

        pnlInfo.add(new JLabel("Thể loại:"));
        cbxLoai = new JComboBox<>();
        pnlInfo.add(cbxLoai);

        pnlInfo.add(new JLabel("Nhà xuất bản:"));
        cbxNXB = new JComboBox<>();
        pnlInfo.add(cbxNXB);

        JPanel pnlGia = new JPanel(new GridLayout(1, 4, 5, 0));
        pnlGia.setBackground(Color.WHITE);
        pnlGia.add(new JLabel("Giá gốc:"));
        txtGiaGoc = new JTextField("0");
        pnlGia.add(txtGiaGoc);
        pnlGia.add(new JLabel("Giá bán:"));
        txtGiaBan = new JTextField("0");
        pnlGia.add(txtGiaBan);

        pnlInfo.add(new JLabel("Định giá (VNĐ):"));
        pnlInfo.add(pnlGia);

        // 2. NHÓM TÁC GIẢ (SỬ DỤNG CARDLAYOUT ĐỂ BIẾN HÌNH)
        JPanel pnlTacGia = new JPanel(new BorderLayout(0, 5));
        pnlTacGia.setBackground(Color.WHITE);
        pnlTacGia.add(new JLabel("Tác giả:"), BorderLayout.NORTH);

        cardTacGia = new CardLayout();
        pnlTacGiaCards = new JPanel(cardTacGia);

        // CARD 1: VIEW MODE (Chỉ xem)
        txtTacGiaView = new JTextField();
        txtTacGiaView.setEditable(false);
        txtTacGiaView.setBackground(new Color(245, 245, 250)); // Nền xám
        txtTacGiaView.setPreferredSize(new Dimension(0, 35)); // ---> FIX 1: Ép chiều cao cố định 35px

        JPanel pnlViewMode = new JPanel(new BorderLayout());
        pnlViewMode.setBackground(Color.WHITE);

        // ---> FIX 2: Đổi từ CENTER sang NORTH để nó không bị kéo giãn dọc xuống dưới
        pnlViewMode.add(txtTacGiaView, BorderLayout.NORTH);

        // CARD 2: EDIT MODE (Có JList và nút Thêm)
        JPanel pnlEditMode = new JPanel(new BorderLayout(0, 5));
        pnlEditMode.setBackground(Color.WHITE);

        JPanel pnlEditHeader = new JPanel(new BorderLayout());
        pnlEditHeader.setBackground(Color.WHITE);
        JLabel lblTacGia = new JLabel("Giữ phím Ctrl để chọn nhiều:");
        lblTacGia.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTacGia.setForeground(Color.GRAY);

        btnAddTacGia = new JButton("Thêm mới");
        btnAddTacGia.setBackground(COL_PRIMARY); // Màu tím than
        btnAddTacGia.setForeground(Color.WHITE);
        btnAddTacGia.setOpaque(true);            // ---> ĐỔI TỪ FALSE THÀNH TRUE <---
        btnAddTacGia.setBorderPainted(false);    // Xóa viền
        btnAddTacGia.setFocusPainted(false);
        btnAddTacGia.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlEditHeader.add(lblTacGia, BorderLayout.WEST);
        pnlEditHeader.add(btnAddTacGia, BorderLayout.EAST);

        listTacGia = new JList<>();
        listTacGia.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listTacGia.setVisibleRowCount(4);
        listTacGia.setFixedCellHeight(25);
        JScrollPane scrollTacGia = new JScrollPane(listTacGia);
        scrollTacGia.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        pnlEditMode.add(pnlEditHeader, BorderLayout.NORTH);
        pnlEditMode.add(scrollTacGia, BorderLayout.CENTER);

        // Nạp 2 Card vào kho
        pnlTacGiaCards.add(pnlViewMode, "VIEW");
        pnlTacGiaCards.add(pnlEditMode, "EDIT");

        pnlTacGia.add(pnlTacGiaCards, BorderLayout.CENTER);

        // 3. Nhóm Tồn Kho
        JPanel pnlNumbers = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlNumbers.setBackground(Color.WHITE);
        pnlNumbers.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Quản lý Tồn kho"));

        txtTonKho = new JTextField("0", 5);
        txtLoi = new JTextField("0", 5);
        pnlNumbers.add(new JLabel("Số lượng tồn:"));
        pnlNumbers.add(txtTonKho);
        pnlNumbers.add(new JLabel("     Hàng lỗi:"));
        pnlNumbers.add(txtLoi);

        pnlRightMain.add(pnlInfo, BorderLayout.NORTH);
        pnlRightMain.add(pnlTacGia, BorderLayout.CENTER);
        pnlRightMain.add(pnlNumbers, BorderLayout.SOUTH);

        // --- PANEL NÚT ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(new Color(245, 245, 250));

        btnSuaTrongDialog = new JButton("Chỉnh sửa");
        btnSuaTrongDialog.setBackground(COL_PRIMARY); // Bạn giữ xanh dương hay đổi COL_SIDEBAR cũng đc
        btnSuaTrongDialog.setForeground(Color.WHITE);
        btnSuaTrongDialog.setOpaque(true);       // ---> THÊM DÒNG NÀY <---
        btnSuaTrongDialog.setBorderPainted(false);
        btnSuaTrongDialog.setVisible(false);

        btnHuy = new JButton("Hủy bỏ");

        btnLuu = new JButton("Lưu dữ liệu");
        btnLuu.setBackground(COL_SIDEBAR);       // Màu hồng (232, 60, 145)
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setOpaque(true);                  // ---> THÊM DÒNG NÀY <---
        btnLuu.setBorderPainted(false);

        pnlBottom.add(btnSuaTrongDialog);
        pnlBottom.add(btnHuy);
        pnlBottom.add(btnLuu);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRightMain, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadTacGiaList() {
        try {
            TacGiaBUS tgBUS = new TacGiaBUS();
            DefaultListModel<ComboItem> tacGiaModel = new DefaultListModel<>();
            for (TacGiaDTO tg : tgBUS.getAll()) {
                tacGiaModel.addElement(new ComboItem(tg.getMaTacGia(), tg.getTenTacGia()));
            }
            listTacGia.setModel(tacGiaModel);
        } catch (Exception e) {
            System.err.println("Lỗi nạp Tác giả: " + e.getMessage());
        }
    }

    private void loadComboboxData() {
        try {
            TheLoaiBUS tlBUS = new TheLoaiBUS();
            for (TheLoaiDTO tl : tlBUS.getAll()) cbxLoai.addItem(new ComboItem(tl.getMaLoai(), tl.getTenLoai()));

            NhaXuatBanBUS nxbBUS = new NhaXuatBanBUS();
            for (NhaXuatBanDTO nxb : nxbBUS.getAll()) cbxNXB.addItem(new ComboItem(nxb.getMaNXB(), nxb.getTenNXB()));

            loadTacGiaList(); // Tách ra hàm riêng để tí nút Thêm Mới gọi lại
        } catch (Exception e) {}
    }

    private void loadData() {
        if (sach != null) {
            txtTen.setText(sach.getTenSach());
            txtGiaGoc.setText(sach.getGiaGoc().toString());
            txtGiaBan.setText(sach.getGiaBan().toString());
            txtTonKho.setText(String.valueOf(sach.getSoLuongTon()));
            txtLoi.setText(String.valueOf(sach.getSoLuongLoi()));

            // Set Text cho chế độ VIEW
            txtTacGiaView.setText(sach.getDanhSachTacGia());

            for (int i = 0; i < cbxLoai.getItemCount(); i++) {
                if (cbxLoai.getItemAt(i).id == sach.getMaLoai()) cbxLoai.setSelectedIndex(i);
            }
            for (int i = 0; i < cbxNXB.getItemCount(); i++) {
                if (cbxNXB.getItemAt(i).id == sach.getMaNXB()) cbxNXB.setSelectedIndex(i);
            }

            // Bôi đen danh sách cho chế độ EDIT
            if (sach.getDanhSachTacGia() != null && !sach.getDanhSachTacGia().isEmpty()) {
                String[] tenTacGias = sach.getDanhSachTacGia().split(",\\s*");
                List<Integer> indicesToSelect = new ArrayList<>();
                ListModel<ComboItem> model = listTacGia.getModel();

                for (int i = 0; i < model.getSize(); i++) {
                    String tenItem = model.getElementAt(i).name;
                    for (String ten : tenTacGias) {
                        if (tenItem.equalsIgnoreCase(ten.trim())) {
                            indicesToSelect.add(i);
                            break;
                        }
                    }
                }
                int[] indices = indicesToSelect.stream().mapToInt(i -> i).toArray();
                listTacGia.setSelectedIndices(indices);
            }

            if (sach.getHinhAnh() != null && !sach.getHinhAnh().isEmpty()) {
                updateImage(sach.getHinhAnh());
            }
        }
    }

    private void updateImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(250, 320, Image.SCALE_SMOOTH);
            lblHinhAnh.setIcon(new ImageIcon(img));
            lblHinhAnh.setText("");
            selectedImagePath = path;
        } catch (Exception e) {
            lblHinhAnh.setText("Lỗi load ảnh");
        }
    }

    public void setReadOnlyMode(boolean readOnly, TaiKhoanDTO user) {
        this.userLogin = user;
        txtTen.setEditable(!readOnly);
        txtGiaGoc.setEditable(!readOnly);
        txtGiaBan.setEditable(!readOnly);
        txtTonKho.setEditable(!readOnly);
        txtLoi.setEditable(!readOnly);

        cbxLoai.setEnabled(!readOnly);
        cbxNXB.setEnabled(!readOnly);
        btnChonAnh.setVisible(!readOnly);
        btnLuu.setVisible(!readOnly);

        if (readOnly) {
            setTitle("Chi Tiết Sách");
            btnHuy.setText("Đóng");
            cardTacGia.show(pnlTacGiaCards, "VIEW"); // Lật sang mặt VIEW

            if (user != null && (user.getMaQuyen() == 1 || user.getMaQuyen() == 3)) {
                btnSuaTrongDialog.setVisible(true);
            }
        } else {
            setTitle(sach == null ? "Thêm Sách Mới" : "Cập Nhật Sách");
            btnHuy.setText("Hủy bỏ");
            btnSuaTrongDialog.setVisible(false);
            cardTacGia.show(pnlTacGiaCards, "EDIT"); // Lật sang mặt EDIT
        }
    }

    private void initEvents() {
        btnChonAnh.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                updateImage(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        btnHuy.addActionListener(e -> dispose());
        btnSuaTrongDialog.addActionListener(e -> setReadOnlyMode(false, userLogin));

        // Nút Thêm tác giả nhanh
        btnAddTacGia.addActionListener(e -> {
            String tenMoi = JOptionPane.showInputDialog(this, "Nhập tên tác giả mới:", "Thêm tác giả", JOptionPane.QUESTION_MESSAGE);
            if (tenMoi != null && !tenMoi.trim().isEmpty()) {
                // Tủn nhớ tạo hàm insert bên TacGiaBUS để test phần này nhé
                // TacGiaDTO tgNew = new TacGiaDTO(); tgNew.setTenTacGia(tenMoi.trim());
                // if (new TacGiaBUS().add(tgNew)) {
                //     loadTacGiaList();
                //     JOptionPane.showMessageDialog(this, "Đã thêm!");
                // }
                JOptionPane.showMessageDialog(this, "Chức năng đang chờ kết nối TacGiaBUS!");
            }
        });

        // Xử lý Lưu
        btnLuu.addActionListener(e -> {
            try {
                String ten = txtTen.getText().trim();
                if (cbxLoai.getSelectedItem() == null || cbxNXB.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng thêm Thể loại và NXB vào hệ thống trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int maLoai = ((ComboItem) cbxLoai.getSelectedItem()).id;
                int maNXB = ((ComboItem) cbxNXB.getSelectedItem()).id;
                BigDecimal giaGoc = new BigDecimal(txtGiaGoc.getText().trim());
                BigDecimal giaBan = new BigDecimal(txtGiaBan.getText().trim());
                int tonKho = Integer.parseInt(txtTonKho.getText().trim());
                int loi = Integer.parseInt(txtLoi.getText().trim());

                List<ComboItem> selectedTacGia = listTacGia.getSelectedValuesList();
                List<Integer> dsMaTacGia = new ArrayList<>();
                for (ComboItem item : selectedTacGia) dsMaTacGia.add(item.id);

                if (dsMaTacGia.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 tác giả!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                SachDTO sNew = new SachDTO();
                sNew.setTenSach(ten);
                sNew.setMaLoai(maLoai);
                sNew.setMaNXB(maNXB);
                sNew.setHinhAnh(selectedImagePath);
                sNew.setGiaGoc(giaGoc);
                sNew.setGiaBan(giaBan);
                sNew.setSoLuongTon(tonKho);
                sNew.setSoLuongLoi(loi);
                sNew.setTrangThai(TrangThaiSach.DANG_BAN);

                String msg = "";
                if (sach == null) {
                    msg = sBUS.addSach(sNew, dsMaTacGia);
                } else {
                    sNew.setMaSach(sach.getMaSach());
                    msg = sBUS.updateSach(sNew, dsMaTacGia);
                }

                JOptionPane.showMessageDialog(this, msg);
                if (msg.contains("Thành công")) dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Giá tiền hoặc Số lượng phải là chữ số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}