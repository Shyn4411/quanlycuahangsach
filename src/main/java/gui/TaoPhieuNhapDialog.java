package gui;

import dto.*;
import bus.PhieuNhapBUS;
import bus.SachBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import enums.TrangThaiGiaoDich;
import enums.TrangThaiSach;

import java.io.FileOutputStream;

import static enums.TrangThaiCoBan.HOAT_DONG;

public class TaoPhieuNhapDialog extends JDialog {

    private TaiKhoanDTO currentUser;
    private DecimalFormat df = new DecimalFormat("#,###");

    private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
    private SachBUS sachBUS = new SachBUS();

    private JComboBox<NhaCungCapDTO> cbxNhaCungCap;
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
        loadDataSach();
        initEvents();

        setSize(1100, 700);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COL_BG_MAIN);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // ================= TRÁI: DANH SÁCH SÁCH & NCC =================
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setOpaque(false);
        pnlLeft.setPreferredSize(new Dimension(450, 0));

        JPanel pnlNCC = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNCC.setBackground(Color.WHITE);
        pnlNCC.setBorder(BorderFactory.createTitledBorder("Thông tin nhập hàng"));
        pnlNCC.add(new JLabel("Nhà Cung Cấp: "));
        cbxNhaCungCap = new JComboBox<>();
        cbxNhaCungCap.setPreferredSize(new Dimension(250, 30));

        bus.NhaCungCapBUS nccBUS = new bus.NhaCungCapBUS();
        List<NhaCungCapDTO> listNCC = nccBUS.getAll();

        NhaCungCapDTO itemDefault = new NhaCungCapDTO();
        itemDefault.setMaNCC(-1);
        itemDefault.setTenNCC("-- Chọn Nhà Cung Cấp --");
        cbxNhaCungCap.addItem(itemDefault);

        if (listNCC != null) {
            for (NhaCungCapDTO ncc : listNCC) {
                if (ncc.getTrangThai() == HOAT_DONG) {
                    cbxNhaCungCap.addItem(ncc);
                }
            }
        }

        pnlNCC.add(cbxNhaCungCap);
        pnlLeft.add(pnlNCC, BorderLayout.NORTH);

        JPanel pnlTableSach = new JPanel(new BorderLayout());
        pnlTableSach.setBackground(Color.WHITE);
        pnlTableSach.setBorder(new TitledBorder("Chọn Sách Để Nhập"));

        String[] colsSach = {"Mã", "Tên Sách", "Tồn Kho"};
        modelSach = new DefaultTableModel(colsSach, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblSach = new JTable(modelSach);
        styleTable(tblSach);
        tblSach.getColumnModel().getColumn(1).setPreferredWidth(200);
        pnlTableSach.add(new JScrollPane(tblSach), BorderLayout.CENTER);

        btnThem = new JButton("Thêm Vào Giỏ>>");
        styleButton(btnThem, COL_SIDEBAR, false);

        JPanel pnlBtnThemWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBtnThemWrap.setBackground(Color.WHITE);
        pnlBtnThemWrap.setBorder(new EmptyBorder(5, 0, 5, 0));
        pnlBtnThemWrap.add(btnThem);
        pnlTableSach.add(pnlBtnThemWrap, BorderLayout.SOUTH);

        pnlLeft.add(pnlTableSach, BorderLayout.CENTER);

        // ================= PHẢI: GIỎ HÀNG NHẬP =================
        JPanel pnlRight = new JPanel(new BorderLayout(0, 10));
        pnlRight.setOpaque(false);

        JPanel pnlGioHang = new JPanel(new BorderLayout());
        pnlGioHang.setBackground(Color.WHITE);
        pnlGioHang.setBorder(new TitledBorder("Danh Sách Sách Nhập Của (" + currentUser.getTenDangNhap() + ")"));

        String[] colsGioHang = {"Mã", "Tên Sách", "Số Lượng", "Giá Nhập", "Thành Tiền"};
        modelGioHang = new DefaultTableModel(colsGioHang, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 2 || c == 3;
            }
        };
        tblGioHang = new JTable(modelGioHang);
        styleTable(tblGioHang);
        tblGioHang.getColumnModel().getColumn(1).setPreferredWidth(180);
        pnlGioHang.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);

        btnXoa = new JButton("Xóa Khỏi Giỏ");
        styleButton(btnXoa, new Color(231, 76, 60), false);

        JPanel pnlBtnXoaWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtnXoaWrap.setBackground(Color.WHITE);
        pnlBtnXoaWrap.setBorder(new EmptyBorder(5, 0, 5, 0));
        pnlBtnXoaWrap.add(btnXoa);
        pnlGioHang.add(pnlBtnXoaWrap, BorderLayout.SOUTH);

        pnlRight.add(pnlGioHang, BorderLayout.CENTER);

        // ================= TỔNG KẾT & XÁC NHẬN =================
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 20, 15, 20)));

        lblTongTien = new JLabel("Tổng Thanh Toán: 0 VNĐ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongTien.setForeground(COL_PRIMARY);
        lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBtns.setOpaque(false);

        btnHuy = new JButton("Hủy Bỏ");
        styleButton(btnHuy, Color.GRAY, true);

        btnXacNhan = new JButton("XÁC NHẬN TẠO PHIẾU");
        styleButton(btnXacNhan, new Color(46, 204, 113), true);

        pnlBtns.add(btnHuy);
        pnlBtns.add(btnXacNhan);

        pnlBottom.add(lblTongTien, BorderLayout.NORTH);
        pnlBottom.add(pnlBtns, BorderLayout.SOUTH);

        pnlRight.add(pnlBottom, BorderLayout.SOUTH);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
    }

    private void loadDataSach() {
        modelSach.setRowCount(0);
        List<SachDTO> listSach = sachBUS.getAll();
        if (listSach != null) {
            for (SachDTO s : listSach) {
                if (s.getTrangThai() == TrangThaiSach.DANG_BAN) {
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
        btnHuy.addActionListener(e -> {
            if (modelGioHang.getRowCount() > 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Giỏ hàng đang có sách.\nBạn có chắc chắn muốn hủy toàn bộ phiếu nhập này không?",
                        "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) dispose();
            } else {
                dispose();
            }
        });

        btnThem.addActionListener(e -> {
            int row = tblSach.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 cuốn sách bên trái để nhập!");
                return;
            }

            int maSach = Integer.parseInt(tblSach.getValueAt(row, 0).toString());
            String tenSach = tblSach.getValueAt(row, 1).toString();

            SachDTO sachChon = sachBUS.getById(maSach);
            double giaNhapGoiY = 0;
            if (sachChon != null && sachChon.getGiaBan() != null) {
                giaNhapGoiY = sachChon.getGiaBan().doubleValue() * 0.7; // Tạm tính giá nhập = 70% giá bán
            }

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
                modelGioHang.addRow(new Object[]{maSach, tenSach, 1, df.format(giaNhapGoiY), df.format(giaNhapGoiY)});
                tinhTongTien();
            }
        });

        btnXoa.addActionListener(e -> {
            int row = tblGioHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 cuốn sách trong giỏ để xóa!");
                return;
            }
            modelGioHang.removeRow(row);
            tinhTongTien();
        });

        modelGioHang.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 2 || col == 3) {
                    try {
                        String slStr = modelGioHang.getValueAt(row, 2).toString().replace(",", "");
                        String giaStr = modelGioHang.getValueAt(row, 3).toString().replace(",", "");

                        int sl = Integer.parseInt(slStr);
                        double gia = Double.parseDouble(giaStr);

                        modelGioHang.setValueAt(df.format(sl * gia), row, 4);
                        tinhTongTien();
                    } catch (Exception ex) {
                    }
                }
            }
        });

        btnXacNhan.addActionListener(e -> {
            if (cbxNhaCungCap.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp ở góc trái!");
                return;
            }
            if (modelGioHang.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Giỏ nhập hàng đang trống!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận tạo phiếu nhập hàng này?\nPhiếu sẽ ở trạng thái CHỜ XỬ LÝ, hàng chưa được cộng vào kho.",
                    "Chốt Phiếu Nhập", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            NhaCungCapDTO selectedNCC = (NhaCungCapDTO) cbxNhaCungCap.getSelectedItem();
            double tongTien = tinhTongTien();

            PhieuNhapDTO pn = new PhieuNhapDTO();
            pn.setMaNV(currentUser.getMaNhanVien() != null ? currentUser.getMaNhanVien() : 1);
            pn.setMaNCC(selectedNCC.getMaNCC());
            pn.setTongTien(BigDecimal.valueOf(tongTien));
            pn.setTrangThai(TrangThaiGiaoDich.CHO_XU_LY);

            List<ChiTietPhieuNhapDTO> dsChiTiet = new ArrayList<>();
            for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
                ct.setMaSach(Integer.parseInt(modelGioHang.getValueAt(i, 0).toString()));
                ct.setTenSach(modelGioHang.getValueAt(i, 1).toString());
                ct.setSoLuong(Integer.parseInt(modelGioHang.getValueAt(i, 2).toString().replace(",", "")));

                String giaStr = modelGioHang.getValueAt(i, 3).toString().replace(",", "");
                ct.setGiaNhap(BigDecimal.valueOf(Double.parseDouble(giaStr)));

                String thanhTienStr = modelGioHang.getValueAt(i, 4).toString().replace(",", "");
                ct.setThanhTien(BigDecimal.valueOf(Double.parseDouble(thanhTienStr)));

                dsChiTiet.add(ct);
            }

            String result = pnBUS.addPhieuNhap(pn, dsChiTiet);

            if (result.contains("Thành công")) {
                int inPhieu = JOptionPane.showConfirmDialog(this, "Tạo phiếu thành công!\nBạn có muốn in phiếu nhập kho dự kiến ra PDF không?", "In Phiếu Nhập", JOptionPane.YES_NO_OPTION);
                if (inPhieu == JOptionPane.YES_OPTION) {
                    xuatPhieuNhapPDF(selectedNCC.getTenNCC(), tongTien, dsChiTiet);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private double tinhTongTien() {
        double tong = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            try {
                String thanhTienStr = modelGioHang.getValueAt(i, 4).toString().replace(",", "");
                double thanhTien = Double.parseDouble(thanhTienStr);
                tong += thanhTien;
            } catch (Exception e) {
            }
        }
        lblTongTien.setText("Tổng Thanh Toán: " + df.format(tong) + " VNĐ");
        return tong;
    }

    private void xuatPhieuNhapPDF(String tenNCC, double tongTien, List<ChiTietPhieuNhapDTO> dsChiTiet) {
        try {
            String path = "PhieuNhap_NCC_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(bf, 18, com.itextpdf.text.Font.BOLD, BaseColor.BLUE);
            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.NORMAL);

            Paragraph title = new Paragraph("PHIẾU YÊU CẦU NHẬP KHO", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Người lập phiếu: " + currentUser.getTenDangNhap(), fontNormal));
            document.add(new Paragraph("Nhà cung cấp: " + tenNCC, fontNormal));
            document.add(new Paragraph("Trạng thái: CHỜ XỬ LÝ (Chưa nhập kho)", fontNormal));
            document.add(new Paragraph("Thời gian lập: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontNormal));
            document.add(new Paragraph("---------------------------------------------------------", fontNormal));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(5);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1.5f, 4f, 1.5f, 2.5f, 2.5f});

            String[] headers = {"Mã Sách", "Tên Sách", "SL Nhập", "Giá Nhập", "Thành Tiền"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            for (ChiTietPhieuNhapDTO ct : dsChiTiet) {
                PdfPCell cMa = new PdfPCell(new Phrase("S" + String.format("%03d", ct.getMaSach()), fontNormal));
                cMa.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cMa);

                pdfTable.addCell(new PdfPCell(new Phrase(ct.getTenSach(), fontNormal)));

                PdfPCell cSL = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                cSL.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cSL);

                PdfPCell cGia = new PdfPCell(new Phrase(df.format(ct.getGiaNhap()), fontNormal));
                cGia.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfTable.addCell(cGia);

                PdfPCell cThanhTien = new PdfPCell(new Phrase(df.format(ct.getThanhTien()), fontNormal));
                cThanhTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfTable.addCell(cThanhTien);
            }
            document.add(pdfTable);

            document.add(new Paragraph(" "));
            Paragraph tongTienPara = new Paragraph("TỔNG TIỀN DỰ KIẾN: " + df.format(tongTien) + " VNĐ", new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD, BaseColor.RED));
            tongTienPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(tongTienPara);

            document.close();
            java.awt.Desktop.getDesktop().open(new java.io.File(path));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xuất biên lai PDF: " + ex.getMessage());
        }
    }

    private void styleButton(JButton btn, Color bgColor, boolean isBigButton) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isBigButton) {
            btn.setPreferredSize(new Dimension(200, 45));
        } else {
            btn.setPreferredSize(new Dimension(150, 35));
        }
    }

    private void styleTable(JTable table) {
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(232, 240, 255));
        table.setSelectionForeground(Color.BLACK);


        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 35));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }
}