package gui;

import dto.TaiKhoanDTO;
import dto.SachDTO;
import dto.ChiTietPhieuNhapDTO;
import dto.PhieuNhapDTO;
import bus.PhieuNhapBUS;
import bus.SachBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TaoPhieuNhapDialog extends JDialog {

    private TaiKhoanDTO currentUser;
    private DecimalFormat df = new DecimalFormat("#,###");

    private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
    private SachBUS sachBUS = new SachBUS(); // Đã mở khóa SachBUS

    private JComboBox<String> cbxNhaCungCap;
    private JTable tblSach, tblGioHang;
    private DefaultTableModel modelSach, modelGioHang;
    private JLabel lblTongTien;
    private JButton btnThem, btnXoa, btnXacNhan, btnHuy;

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_BG_MAIN = new Color(248, 244, 236);

    public TaoPhieuNhapDialog(Window owner, TaiKhoanDTO user) {
        super(owner, "Tạo Phiếu Nhập Mới", ModalityType.APPLICATION_MODAL);
        this.currentUser = user;
        initUI();
        loadDataSach(); // Gọi thẳng vào Database
        initEvents();

        setSize(1100, 700);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COL_BG_MAIN);
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // ================= TRÁI: DANH SÁCH SÁCH & NCC =================
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new Dimension(500, 0));

        JPanel pnlNCC = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNCC.setBackground(Color.WHITE);
        pnlNCC.setBorder(BorderFactory.createTitledBorder("Thông tin nhập hàng"));
        pnlNCC.add(new JLabel("Nhà Cung Cấp: "));
        cbxNhaCungCap = new JComboBox<>(new String[]{"-- Chọn Nhà Cung Cấp --", "1 - NXB Kim Đồng", "2 - NXB Trẻ", "3 - Nhã Nam"});
        cbxNhaCungCap.setPreferredSize(new Dimension(250, 30));
        pnlNCC.add(cbxNhaCungCap);
        pnlLeft.add(pnlNCC, BorderLayout.NORTH);

        JPanel pnlTableSach = new JPanel(new BorderLayout());
        pnlTableSach.setBackground(Color.WHITE);
        pnlTableSach.setBorder(new TitledBorder("Chọn Sách Để Nhập"));

        String[] colsSach = {"Mã", "Tên Sách", "Tồn Kho"};
        modelSach = new DefaultTableModel(colsSach, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSach = new JTable(modelSach);
        styleTable(tblSach);
        tblSach.getColumnModel().getColumn(1).setPreferredWidth(250);
        pnlTableSach.add(new JScrollPane(tblSach), BorderLayout.CENTER);

        btnThem = new JButton("Thêm Vào Giỏ Nhập >>");
        styleButton(btnThem, COL_SIDEBAR);
        pnlTableSach.add(btnThem, BorderLayout.SOUTH);
        pnlLeft.add(pnlTableSach, BorderLayout.CENTER);

        // ================= PHẢI: GIỎ HÀNG NHẬP =================
        JPanel pnlRight = new JPanel(new BorderLayout(0, 10));
        pnlRight.setOpaque(false);

        JPanel pnlGioHang = new JPanel(new BorderLayout());
        pnlGioHang.setBackground(Color.WHITE);
        pnlGioHang.setBorder(new TitledBorder("Danh Sách Sách Nhập Của (" + currentUser.getTenDangNhap() + ")"));

        String[] colsGioHang = {"Mã", "Tên Sách", "Số Lượng", "Giá Nhập", "Thành Tiền"};
        modelGioHang = new DefaultTableModel(colsGioHang, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == 2 || c == 3; // CHỈ CHO SỬA CỘT SỐ LƯỢNG VÀ GIÁ NHẬP
            }
        };
        tblGioHang = new JTable(modelGioHang);
        styleTable(tblGioHang);
        tblGioHang.getColumnModel().getColumn(1).setPreferredWidth(200);
        pnlGioHang.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);

        btnXoa = new JButton("Xóa Khỏi Giỏ");
        styleButton(btnXoa, new Color(231, 76, 60));
        pnlGioHang.add(btnXoa, BorderLayout.SOUTH);
        pnlRight.add(pnlGioHang, BorderLayout.CENTER);

        // Tổng kết & Xác nhận
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(new EmptyBorder(15, 15, 15, 15));

        lblTongTien = new JLabel("Tổng Thanh Toán: 0 VNĐ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongTien.setForeground(COL_PRIMARY);
        pnlBottom.add(lblTongTien, BorderLayout.WEST);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtns.setOpaque(false);
        btnHuy = new JButton("Hủy Bỏ");
        styleButton(btnHuy, Color.GRAY);
        btnXacNhan = new JButton("XÁC NHẬN TẠO PHIẾU");
        styleButton(btnXacNhan, new Color(46, 204, 113));
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnXacNhan);
        pnlBottom.add(pnlBtns, BorderLayout.EAST);

        pnlRight.add(pnlBottom, BorderLayout.SOUTH);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
    }

    private void loadDataSach() {
        modelSach.setRowCount(0);

        // ĐÃ MỞ KHÓA CODE GỌI TỪ DATABASE THẬT LÊN
        List<SachDTO> listSach = sachBUS.getAll();
        if (listSach != null) {
            for (SachDTO s : listSach) {
                // Tùy Tủn, nếu muốn hiển thị cả sách Ngưng Bán để nhập hàng tiếp thì bỏ chữ if này đi
                if (s.getTrangThai().name().equals("DangBan")) {
                    modelSach.addRow(new Object[]{
                            s.getMaSach(),
                            s.getTenSach(),
                            s.getSoLuongTon()
                    });
                }
            }
        }
    }

    private void initEvents() {
        btnHuy.addActionListener(e -> dispose());

        btnThem.addActionListener(e -> {
            int row = tblSach.getSelectedRow();
            if(row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 cuốn sách bên trái để nhập!");
                return;
            }

            int maSach = Integer.parseInt(tblSach.getValueAt(row, 0).toString());
            String tenSach = tblSach.getValueAt(row, 1).toString();

            boolean daTonTai = false;
            for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                int maSachGioHang = Integer.parseInt(modelGioHang.getValueAt(i, 0).toString());
                if (maSach == maSachGioHang) {
                    int slHienTai = Integer.parseInt(modelGioHang.getValueAt(i, 2).toString());
                    modelGioHang.setValueAt(slHienTai + 1, i, 2);
                    daTonTai = true;
                    break;
                }
            }

            if (!daTonTai) {
                // Giá trị ban đầu là 1 cuốn và 0 đồng
                modelGioHang.addRow(new Object[]{maSach, tenSach, 1, 0, 0});
            }
            // Auto-calculate sẽ được kích hoạt bởi TableModelListener
        });

        // SỰ KIỆN TỰ ĐỘNG TÍNH TIỀN KHI GÕ SỐ VÀO BẢNG GIỎ HÀNG
        modelGioHang.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                if (col == 2 || col == 3) {
                    try {
                        int sl = Integer.parseInt(modelGioHang.getValueAt(row, 2).toString().replace(",", ""));
                        double gia = Double.parseDouble(modelGioHang.getValueAt(row, 3).toString().replace(",", ""));
                        double thanhTien = sl * gia;

                        // Tạm thời tắt listener để update Thành Tiền tránh bị lặp vô tận
                        modelGioHang.removeTableModelListener(this.modelGioHang.getTableModelListeners()[0]);
                        modelGioHang.setValueAt(df.format(thanhTien), row, 4); // Cập nhật cột Thành Tiền có format

                        // Nếu muốn đẹp hơn thì format luôn cả cột Giá Nhập khi người dùng vừa gõ xong
                        if(col == 3) {
                            modelGioHang.setValueAt(df.format(gia), row, 3);
                        }

                        // Bật lại listener
                        initEvents_TableModel();

                        tinhTongTien();
                    } catch (Exception ex) {
                        // Người dùng gõ chữ bậy bạ thì bỏ qua
                    }
                }
            }
        });

        btnXoa.addActionListener(e -> {
            int row = tblGioHang.getSelectedRow();
            if(row >= 0) {
                modelGioHang.removeRow(row);
                tinhTongTien();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách trong giỏ để xóa!");
            }
        });

        btnXacNhan.addActionListener(e -> {
            if(cbxNhaCungCap.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp ở góc trái!");
                return;
            }
            if(modelGioHang.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Giỏ nhập hàng đang trống!");
                return;
            }

            String selectedNCC = cbxNhaCungCap.getSelectedItem().toString();
            int maNCC = Integer.parseInt(selectedNCC.split(" - ")[0]);

            double tongTien = tinhTongTien();

            PhieuNhapDTO pn = new PhieuNhapDTO();
            pn.setMaNV(currentUser.getMaTaiKhoan());
            pn.setMaNCC(maNCC);
            pn.setTongTien(BigDecimal.valueOf(tongTien));
            pn.setTrangThai(enums.TrangThaiGiaoDich.ChoXuLy);

            List<ChiTietPhieuNhapDTO> dsChiTiet = new ArrayList<>();
            for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
                ct.setMaSach(Integer.parseInt(modelGioHang.getValueAt(i, 0).toString()));
                ct.setSoLuong(Integer.parseInt(modelGioHang.getValueAt(i, 2).toString()));

                // Nhớ xóa dấu phẩy trước khi parse số để đẩy vào DB
                String giaStr = modelGioHang.getValueAt(i, 3).toString().replace(",", "");
                ct.setGiaNhap(BigDecimal.valueOf(Double.parseDouble(giaStr)));
                dsChiTiet.add(ct);
            }

            String result = pnBUS.addPhieuNhap(pn, dsChiTiet);

            if (result.contains("Thành công")) {
                JOptionPane.showMessageDialog(this, result, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Tách riêng cục listener ra để dễ bật/tắt chống lặp
    private void initEvents_TableModel() {
        modelGioHang.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 2 || col == 3) {
                    try {
                        int sl = Integer.parseInt(modelGioHang.getValueAt(row, 2).toString().replace(",", ""));
                        double gia = Double.parseDouble(modelGioHang.getValueAt(row, 3).toString().replace(",", ""));
                        modelGioHang.removeTableModelListener(this.modelGioHang.getTableModelListeners()[0]);
                        modelGioHang.setValueAt(df.format(sl * gia), row, 4);
                        if(col == 3) modelGioHang.setValueAt(df.format(gia), row, 3);
                        initEvents_TableModel();
                        tinhTongTien();
                    } catch (Exception ex) {}
                }
            }
        });
    }

    private double tinhTongTien() {
        double tong = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            try {
                // Ép kiểu bỏ dấu phẩy đi để tính toán
                double thanhTien = Double.parseDouble(modelGioHang.getValueAt(i, 4).toString().replace(",", ""));
                tong += thanhTien;
            } catch (Exception e) {}
        }
        lblTongTien.setText("Tổng Thanh Toán: " + df.format(tong) + " VNĐ");
        return tong;
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 40));
    }

    private void styleTable(JTable table) {
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(232, 240, 255));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 35));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }
}