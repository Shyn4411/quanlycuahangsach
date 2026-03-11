package gui;

import bus.SachBUS;
import bus.TheLoaiBUS;
import dto.SachDTO;
import dto.TaiKhoanDTO;
import dto.TheLoaiDTO;
import enums.TrangThaiSach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.util.List;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

public class SachGUI extends JPanel {

    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_SIDEBAR = new Color(67, 51, 76);

    private SachBUS sBUS = new SachBUS();
    private TheLoaiBUS tlBUS = new TheLoaiBUS();

    private DefaultTableModel modelSach;
    private JTable tblSach;
    private TableRowSorter<DefaultTableModel> sorterSach; // NÂNG CẤP BỘ LỌC THÔNG MINH

    private JTextField txtTimKiem;
    private JButton btnThem, btnXemChiTiet, btnXoa, btnLamMoi, btnImport; // Đổi nút Lọc thành Làm Mới
    private TaiKhoanDTO userLogin;

    public SachGUI(TaiKhoanDTO user) {
        this.userLogin = user;
        initUI();
        loadDataToTable();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. THANH CÔNG CỤ ---
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setBackground(Color.WHITE);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(250, 35)); // Kéo dài ra xíu

        btnLamMoi = createFlatButton("Làm mới", "/icons/research.png", Color.GRAY);
        btnLamMoi.setPreferredSize(new Dimension(110, 35)); // Nút làm mới cho gọn lại

        pnlSearch.add(new JLabel("Tìm kiếm (Mã/Tên/Tác giả/Thể loại):"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnLamMoi);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setBackground(Color.WHITE);
        btnThem = createFlatButton("Thêm", "/icons/plus.png", COL_PRIMARY);
        btnXemChiTiet = createFlatButton("Xem chi tiết", "/icons/view.png", COL_SIDEBAR);
        btnXemChiTiet.setPreferredSize(new Dimension(150,35));
        btnXoa = createFlatButton("Xóa", "/icons/delete.png", COL_SIDEBAR);
        btnImport = createFlatButton("Nhập", "/icons/pencil.png", new Color(46, 204, 113));

        if (userLogin.getMaQuyen() == 2) {
            btnThem.setVisible(false);
            btnXemChiTiet.setVisible(false);
            btnXoa.setVisible(false);
            btnImport.setVisible(false);
        }

        pnlAction.add(btnImport);
        pnlAction.add(btnXoa);
        pnlAction.add(btnXemChiTiet);
        pnlAction.add(btnThem);

        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlAction, BorderLayout.EAST);
        add(pnlToolbar, BorderLayout.NORTH);

        // --- 2. BẢNG DỮ LIỆU ---
        String[] columns = {"Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Giá Bán", "Kho", "Trạng Thái"};
        modelSach = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSach = new JTable(modelSach);
        tblSach.setRowHeight(40);

        // NÂNG CẤP: Gắn Sorter vào bảng để chuẩn bị làm Live Search
        sorterSach = new TableRowSorter<>(modelSach);
        tblSach.setRowSorter(sorterSach);

        JScrollPane scrollPane = new JScrollPane(tblSach);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // TÚT TÁT GIAO DIỆN BẢNG SÁCH
        // ==========================================
        tblSach.setFocusable(false);
        tblSach.setIntercellSpacing(new Dimension(0, 0));
        tblSach.setSelectionBackground(new Color(232, 240, 255));
        tblSach.setSelectionForeground(Color.BLACK);
        tblSach.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = tblSach.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tblSach.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblSach.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblSach.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblSach.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        tblSach.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    if (value.toString().equals("ĐANG BÁN")) {
                        c.setForeground(new Color(46, 204, 113));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(231, 76, 60));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        });

        tblSach.getColumnModel().getColumn(1).setPreferredWidth(250);
    }

    private JButton createFlatButton(String text, String iconPath, Color bg) {
        JButton btn = new JButton(text);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            btn.setIconTextGap(8);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh: " + iconPath);
        }

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));
        return btn;
    }

    public void loadDataToTable() {
        modelSach.setRowCount(0);
        List<SachDTO> ds = sBUS.getAll();
        for (SachDTO s : ds) {
            String ttStr = (s.getTrangThai() == TrangThaiSach.DANG_BAN) ? "ĐANG BÁN" : "NGỪNG BÁN";

            String tenTheLoai = (s.getTenLoai() != null) ? s.getTenLoai() : "Chưa phân loại";

            modelSach.addRow(new Object[]{
                    s.getMaSachCode(),
                    s.getTenSach(),
                    s.getDanhSachTacGia(),
                    tenTheLoai,
                    String.format("%,.0fđ", s.getGiaBan()),
                    s.getSoLuongTon(),
                    ttStr
            });
        }
    }

    private void initEvents() {

        // ===============================================
        // NÂNG CẤP: TÌM KIẾM LIVE GÕ TỚI ĐÂU LỌC TỚI ĐÓ
        // ===============================================
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterLive(); }
            public void removeUpdate(DocumentEvent e) { filterLive(); }
            public void changedUpdate(DocumentEvent e) { filterLive(); }

            private void filterLive() {
                String text = txtTimKiem.getText().trim();
                if (text.length() == 0) {
                    sorterSach.setRowFilter(null); // Không gõ gì thì hiện toàn bộ
                } else {
                    // Cấu hình: Tìm kiếm không phân biệt hoa thường trên các cột: 0(Mã), 1(Tên), 2(Tác giả), 3(Thể loại)
                    sorterSach.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1, 2, 3));
                }
            }
        });

        // Đổi nút Lọc thành Làm mới: Xóa chữ trên ô tìm kiếm và gọi loadDataToTable
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            sorterSach.setRowFilter(null);
            loadDataToTable(); // Đề phòng DB có thay đổi từ người khác thì nhấn Làm Mới sẽ kéo Data mới nhất về
        });

        btnThem.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            SachDialog dialog = new SachDialog(owner, this, null, sBUS);
            dialog.setVisible(true);

            loadDataToTable();
        });

        btnXemChiTiet.addActionListener(e -> {
            int row = tblSach.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 quyển sách trên bảng!");
                return;
            }

            // FIX LỖI KHI DÙNG SORTER: Phải dùng convertRowIndexToModel để lấy đúng Index dòng thực sự dưới DB
            int modelRow = tblSach.convertRowIndexToModel(row);
            int id = Integer.parseInt(modelSach.getValueAt(modelRow, 0).toString().substring(1));
            SachDTO s = sBUS.getById(id);

            if (s != null) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                SachDialog dialog = new SachDialog(owner, this, s, sBUS);
                dialog.setReadOnlyMode(true, userLogin);
                dialog.setVisible(true);

                loadDataToTable();
            }
        });

        btnXoa.addActionListener(e -> {
            int row = tblSach.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!");
                return;
            }

            int modelRow = tblSach.convertRowIndexToModel(row);
            String trangThaiHienTai = modelSach.getValueAt(modelRow, 6).toString();

            if (trangThaiHienTai.equals("NGỪNG BÁN")) {
                JOptionPane.showMessageDialog(this, "Sách này đã ngừng bán từ trước rồi!");
                return;
            }

            String tenSach = modelSach.getValueAt(modelRow, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn NGỪNG BÁN sách:\n" + tenSach + " ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(modelSach.getValueAt(modelRow, 0).toString().substring(1));

                String msg = sBUS.deleteSach(id);
                JOptionPane.showMessageDialog(this, msg);

                loadDataToTable();
            }
        });
        btnImport.addActionListener(e -> xuLyImportExcel());
    }


    private void xuLyImportExcel() {
        // 1. Mở cửa sổ chọn file cho người dùng
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel chứa danh sách Sách");
        // Chỉ cho phép chọn file .xlsx
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();

            try (FileInputStream fis = new FileInputStream(fileToImport);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                // Lấy Sheet đầu tiên (Sheet 0)
                Sheet sheet = workbook.getSheetAt(0);
                int soLuongThanhCong = 0;

                // Tạm gọi SachBUS để lưu vào Database
                bus.SachBUS sachBUS = new bus.SachBUS();

                // 2. Vòng lặp đọc từng dòng (Bắt đầu từ 1 để bỏ qua dòng Tiêu đề)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue; // Bỏ qua dòng trống

                    try {
                        dto.SachDTO sachMoi = new dto.SachDTO();

                        // Cột 0: Tên sách (String)
                        sachMoi.setTenSach(row.getCell(0).getStringCellValue());

                        // Cột 1: Mã Loại (Số)
                        sachMoi.setMaLoai((int) row.getCell(1).getNumericCellValue());

                        // Cột 2: Mã NXB (Số)
                        sachMoi.setMaNXB((int) row.getCell(2).getNumericCellValue());

                        // Cột 3: Giá Gốc (Số -> BigDecimal)
                        double giaGoc = row.getCell(3).getNumericCellValue();
                        sachMoi.setGiaGoc(BigDecimal.valueOf(giaGoc));

                        // Cột 4: Giá Bán (Số -> BigDecimal)
                        double giaBan = row.getCell(4).getNumericCellValue();
                        sachMoi.setGiaBan(BigDecimal.valueOf(giaBan));

                        // Cột 5: Số lượng tồn (Số)
                        sachMoi.setSoLuongTon((int) row.getCell(5).getNumericCellValue());

                        // Set các thông tin mặc định khác
                        sachMoi.setTrangThai(enums.TrangThaiSach.DANG_BAN);
                        sachMoi.setSoLuongLoi(0);
                        sachMoi.setHinhAnh(""); // Tạm thời để trống ảnh

                        // 3. Đẩy xuống Database (Giả sử tác giả mặc định là 1 để tránh lỗi)
                        java.util.List<Integer> listTacGia = new java.util.ArrayList<>();
                        listTacGia.add(1);

                        String msg = sachBUS.addSach(sachMoi, listTacGia);
                        if (msg.contains("Thành công")) {
                            soLuongThanhCong++;
                        }

                    } catch (Exception exRow) {
                        System.out.println("Lỗi ở dòng " + (i + 1) + ": " + exRow.getMessage());

                    }
                }

                JOptionPane.showMessageDialog(this, "Import thành công " + soLuongThanhCong + " cuốn sách vào CSDL!");


                loadDataToTable();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi đọc file Excel: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}