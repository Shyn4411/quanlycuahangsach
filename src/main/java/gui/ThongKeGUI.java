package gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

// Import thư viện JCalendar
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JYearChooser;

// Import thư viện Apache POI để xuất Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// IMPORT THƯ VIỆN JFREECHART VẼ BIỂU ĐỒ
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class ThongKeGUI extends JPanel {

    private bus.ThongKeBUS tkBUS = new bus.ThongKeBUS();
    private java.text.DecimalFormat df = new java.text.DecimalFormat("#,### VNĐ");

    private JTable tblThongKe;
    private DefaultTableModel modelThongKe;
    private JPanel pnlTable, pnlChart; // Thêm pnlChart
    private JTabbedPane tabCenter;     // Tab để chuyển qua lại giữa Bảng và Biểu Đồ

    // Các thành phần bộ lọc
    private JComboBox<String> cbxLoaiThongKe;
    private JLabel lblTu, lblDen, lblNam;
    private JDateChooser txtTuNgay, txtDenNgay;
    private JYearChooser txtNam;

    private JLabel lblTongVon, lblTongDoanhThu, lblLoiNhuan;
    private JButton btnThongKe, btnXuatExcel;

    final Color COL_SIDEBAR = new Color(67, 51, 76);
    final Color COL_PRIMARY = new Color(232, 60, 145);
    final Color COL_BG = new Color(248, 244, 236);

    public ThongKeGUI() {
        initUI();
        initEvents();
        updateFilterUI();
        thucHienThongKe();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. THANH BỘ LỌC (NORTH) ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        pnlFilter.add(new JLabel("Loại báo cáo:"));
        cbxLoaiThongKe = new JComboBox<>(new String[]{
                "Top 10 Sách Bán Chạy",
                "Doanh Thu Theo Tháng",
                "Sách Sắp Hết Hàng (Tồn < 10)"
        });
        cbxLoaiThongKe.setPreferredSize(new Dimension(220, 32));
        cbxLoaiThongKe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlFilter.add(cbxLoaiThongKe);

        lblTu = new JLabel(" | Từ ngày:");
        txtTuNgay = new JDateChooser();
        txtTuNgay.setDateFormatString("dd/MM/yyyy");
        txtTuNgay.setPreferredSize(new Dimension(130, 32));
        txtTuNgay.setDate(new Date());

        lblDen = new JLabel("Đến:");
        txtDenNgay = new JDateChooser();
        txtDenNgay.setDateFormatString("dd/MM/yyyy");
        txtDenNgay.setPreferredSize(new Dimension(130, 32));
        txtDenNgay.setDate(new Date());

        lblNam = new JLabel(" | Chọn Năm:");
        txtNam = new JYearChooser();
        txtNam.setPreferredSize(new Dimension(80, 32));

        pnlFilter.add(lblTu); pnlFilter.add(txtTuNgay);
        pnlFilter.add(lblDen); pnlFilter.add(txtDenNgay);
        pnlFilter.add(lblNam); pnlFilter.add(txtNam);

        btnThongKe = new JButton("Lọc Dữ Liệu");
        styleButton(btnThongKe, COL_SIDEBAR);
        pnlFilter.add(btnThongKe);

        btnXuatExcel = new JButton("Xuất Excel");
        styleButton(btnXuatExcel, new Color(46, 204, 113));
        pnlFilter.add(btnXuatExcel);

        add(pnlFilter, BorderLayout.NORTH);

        // --- 2. TRUNG TÂM (CENTER) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 100));

        lblTongVon = createStatCard(pnlCards, "TỔNG VỐN NHẬP KHO", "0 VNĐ", Color.GRAY);
        lblTongDoanhThu = createStatCard(pnlCards, "TỔNG DOANH THU BÁN", "0 VNĐ", COL_PRIMARY);
        lblLoiNhuan = createStatCard(pnlCards, "LỢI NHUẬN RÒNG", "0 VNĐ", new Color(41, 128, 185));

        pnlCenter.add(pnlCards, BorderLayout.NORTH);

        // --- TẠO TAB CHỨA BẢNG VÀ BIỂU ĐỒ ---
        tabCenter = new JTabbedPane();
        tabCenter.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Tab 1: Bảng
        pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        modelThongKe = new DefaultTableModel(new String[]{"Cột 1", "Cột 2"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblThongKe = new JTable(modelThongKe);
        pnlTable.add(new JScrollPane(tblThongKe), BorderLayout.CENTER);

        // Tab 2: Biểu Đồ
        pnlChart = new JPanel(new BorderLayout());
        pnlChart.setBackground(Color.WHITE);

        tabCenter.addTab("BẢNG DỮ LIỆU", pnlTable);
        tabCenter.addTab("BIỂU ĐỒ TRỰC QUAN", pnlChart);

        pnlCenter.add(tabCenter, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private void setupTableColumns(String[] cols, int loaiBaoCao) {
        modelThongKe.setColumnIdentifiers(cols);
        tblThongKe.setFocusable(false);
        tblThongKe.setIntercellSpacing(new Dimension(0, 0));
        tblThongKe.setRowHeight(40);
        tblThongKe.setSelectionBackground(new Color(232, 240, 255));
        tblThongKe.setSelectionForeground(Color.BLACK);
        tblThongKe.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = tblThongKe.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(false);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        if (loaiBaoCao == 0) {
            tblThongKe.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblThongKe.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            tblThongKe.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

            tblThongKe.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);
                    if (value != null) {
                        c.setForeground(new Color(46, 204, 113));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                    if (isSelected) c.setForeground(table.getSelectionForeground());
                    return c;
                }
            });
            tblThongKe.getColumnModel().getColumn(2).setPreferredWidth(250);

        } else if (loaiBaoCao == 1) {
            tblThongKe.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblThongKe.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            tblThongKe.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

            tblThongKe.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);
                    if (value != null) {
                        c.setForeground(new Color(41, 128, 185));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                    if (isSelected) c.setForeground(table.getSelectionForeground());
                    return c;
                }
            });

        } else if (loaiBaoCao == 2) {
            tblThongKe.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblThongKe.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            tblThongKe.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

            tblThongKe.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);
                    if (value != null) {
                        c.setForeground(new Color(231, 76, 60));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                    if (isSelected) c.setForeground(table.getSelectionForeground());
                    return c;
                }
            });
            tblThongKe.getColumnModel().getColumn(1).setPreferredWidth(250);
        }
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblT = new JLabel(title, SwingConstants.CENTER);
        lblT.setForeground(Color.GRAY);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lblV = new JLabel(value, SwingConstants.CENTER);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblV.setForeground(color);

        card.add(lblT);
        card.add(lblV);
        parent.add(card);
        return lblV;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
    }

    private void updateFilterUI() {
        int index = cbxLoaiThongKe.getSelectedIndex();
        if (index == 0) {
            lblTu.setVisible(true); txtTuNgay.setVisible(true);
            lblDen.setVisible(true); txtDenNgay.setVisible(true);
            lblNam.setVisible(false); txtNam.setVisible(false);
        } else if (index == 1) {
            lblTu.setVisible(false); txtTuNgay.setVisible(false);
            lblDen.setVisible(false); txtDenNgay.setVisible(false);
            lblNam.setVisible(true); txtNam.setVisible(true);
        } else if (index == 2) {
            lblTu.setVisible(false); txtTuNgay.setVisible(false);
            lblDen.setVisible(false); txtDenNgay.setVisible(false);
            lblNam.setVisible(false); txtNam.setVisible(false);
        }
    }

    private void initEvents() {
        cbxLoaiThongKe.addActionListener(e -> {
            updateFilterUI();
            thucHienThongKe();
        });
        btnThongKe.addActionListener(e -> thucHienThongKe());
        btnXuatExcel.addActionListener(e -> xuatExcel());
    }

    // ==========================================
    // LOGIC ĐỔ DỮ LIỆU & VẼ BIỂU ĐỒ
    // ==========================================
    private void thucHienThongKe() {
        int index = cbxLoaiThongKe.getSelectedIndex();
        modelThongKe.setRowCount(0);

        SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd");
        String tuNgayDB = "", denNgayDB = "";

        if (index == 0 && txtTuNgay.getDate() != null && txtDenNgay.getDate() != null) {
            tuNgayDB = sdfDB.format(txtTuNgay.getDate()) + " 00:00:00";
            denNgayDB = sdfDB.format(txtDenNgay.getDate()) + " 23:59:59";
        } else if (index == 1) {
            int year = txtNam.getYear();
            tuNgayDB = year + "-01-01 00:00:00";
            denNgayDB = year + "-12-31 23:59:59";
        }

        double doanhThu = tkBUS.getTongDoanhThu(tuNgayDB, denNgayDB);
        double von = tkBUS.getTongVon(tuNgayDB, denNgayDB);
        double loiNhuan = doanhThu - von;

        lblTongDoanhThu.setText(df.format(doanhThu));
        lblTongVon.setText(df.format(von));
        lblLoiNhuan.setText(df.format(loiNhuan));

        // Khởi tạo tập dữ liệu cho Biểu Đồ
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (index == 0) {
            setupTableColumns(new String[]{"Hạng", "Mã Sách", "Tên Sách", "Số Lượng Đã Bán", "Doanh Thu Mang Lại"}, 0);
            List<Object[]> list = tkBUS.getTopSachBanChay(tuNgayDB, denNgayDB);
            if(list != null) {
                for (Object[] row : list) {
                    // Nạp dữ liệu vào biểu đồ (Lấy số lượng bán)
                    dataset.addValue(((Number)row[3]).doubleValue(), "Số lượng bán", row[2].toString());

                    // Format tiền cho bảng
                    row[4] = df.format(row[4]);
                    modelThongKe.addRow(row);
                }
            }
            veBieuDo(dataset, "Top 10 Sách Bán Chạy", "Tên Sách", "Số lượng (Cuốn)");

        } else if (index == 1) {
            setupTableColumns(new String[]{"Tháng/Năm", "Tổng Vốn Nhập", "Tổng Doanh Thu Bán", "Lợi Nhuận Ròng"}, 1);
            List<Object[]> list = tkBUS.getDoanhThuTheoThang(tuNgayDB, denNgayDB);
            if(list != null) {
                for (Object[] row : list) {
                    // Nạp 2 cột vào biểu đồ để so sánh
                    dataset.addValue(((Number)row[2]).doubleValue(), "Doanh Thu", row[0].toString());
                    dataset.addValue(((Number)row[3]).doubleValue(), "Lợi Nhuận", row[0].toString());

                    row[1] = df.format(row[1]);
                    row[2] = df.format(row[2]);
                    row[3] = df.format(row[3]);
                    modelThongKe.addRow(row);
                }
            }
            veBieuDo(dataset, "Biểu đồ Doanh Thu & Lợi Nhuận Năm " + txtNam.getYear(), "Tháng", "Số tiền (VNĐ)");

        } else if (index == 2) {
            setupTableColumns(new String[]{"Mã Sách", "Tên Sách", "Thể Loại", "Nhà Xuất Bản", "Tồn Kho Hiện Tại"}, 2);
            List<Object[]> list = tkBUS.getSachSapHet();
            if(list != null) {
                for (Object[] row : list) {
                    // Nạp dữ liệu tồn kho vào biểu đồ
                    dataset.addValue(((Number)row[4]).doubleValue(), "Tồn Kho", row[1].toString());
                    modelThongKe.addRow(row);
                }
            }
            veBieuDo(dataset, "Sách Sắp Hết Hàng", "Tên Sách", "Số lượng tồn");
        }
    }

    // ==========================================
    // HÀM VẼ BIỂU ĐỒ BẰNG JFREECHART
    // ==========================================
    private void veBieuDo(DefaultCategoryDataset dataset, String title, String categoryLabel, String valueLabel) {
        pnlChart.removeAll(); // Xóa biểu đồ cũ

        // Tạo biểu đồ cột
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                categoryLabel,
                valueLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Fix lỗi Font Tiếng Việt cho thư viện JFreeChart
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 13));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Tút lại màu sắc cho cột
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(41, 128, 185)); // Xanh dương
        if (dataset.getRowCount() > 1) {
            renderer.setSeriesPaint(1, new Color(46, 204, 113)); // Xanh lá cây cho cột thứ 2 (Lợi Nhuận)
        }

        // Nhét vào Panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(pnlChart.getWidth(), pnlChart.getHeight()));
        pnlChart.setLayout(new BorderLayout());
        pnlChart.add(chartPanel, BorderLayout.CENTER);
        pnlChart.validate();
        pnlChart.repaint();
    }

    private void xuatExcel() {
        if (tblThongKe.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
            return;
        }

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("ThongKe");

            Row rowHeader = sheet.createRow(0);
            for (int i = 0; i < tblThongKe.getColumnCount(); i++) {
                Cell cell = rowHeader.createCell(i);
                cell.setCellValue(tblThongKe.getColumnName(i));
            }

            for (int i = 0; i < tblThongKe.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < tblThongKe.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    if (tblThongKe.getValueAt(i, j) != null) {
                        cell.setCellValue(tblThongKe.getValueAt(i, j).toString());
                    }
                }
            }

            String fileName = "BaoCaoThongKe_" + System.currentTimeMillis() + ".xlsx";
            FileOutputStream out = new FileOutputStream(new File(fileName));
            workbook.write(out);
            out.close();
            workbook.close();

            JOptionPane.showMessageDialog(this, "Xuất Excel thành công!\nFile được lưu tại: " + fileName);
            Desktop.getDesktop().open(new File(fileName));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất Excel: " + ex.getMessage());
        }
    }
}