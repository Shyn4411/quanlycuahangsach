package gui;

import bus.ThongKeBUS;
import dto.TaiKhoanDTO;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ThongKeGUI extends JPanel {

    private final Color COLOR_CREAM = new Color(248, 244, 236);
    private final Color COLOR_PRIMARY = new Color(232, 60, 145); // Hồng
    private final Color COLOR_DARK = new Color(67, 51, 76);    // Tím xám Sidebar
    private final Color COLOR_PROFIT = new Color(46, 204, 113); // Xanh lá

    private JTextField txtTuNgay, txtDenNgay;
    private JLabel lblRevenue, lblProfit, lblCost;
    private JTabbedPane mainTabbedPane;

    private JTable tblRevenue, tblProfit, tblInventory;
    private DefaultTableModel modelRevenue, modelProfit, modelInventory;

    private CustomChartPanel chartRevenue, chartProfit;

    private ThongKeBUS tkBus = new ThongKeBUS();
    private List<Object[]> currentData = new ArrayList<>();
    private DecimalFormat formatter = new DecimalFormat("###,###,### VNĐ");

    public ThongKeGUI(TaiKhoanDTO user) {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_CREAM);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initHeader();
        initMainTabs();

        // Mặc định lọc từ đầu năm
        txtTuNgay.setText(LocalDate.now().withDayOfYear(1).toString());
        txtDenNgay.setText(LocalDate.now().toString());
        refreshAllData();
    }

    private void initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("BÁO CÁO CỬA HÀNG", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_DARK);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilter.setOpaque(false);
        txtTuNgay = new JTextField(8);
        txtDenNgay = new JTextField(8);
        JButton btnLoc = new JButton("Cập Nhật Số Liệu");
        styleButton(btnLoc);

        pnlFilter.add(new JLabel("Từ:")); pnlFilter.add(txtTuNgay);
        pnlFilter.add(new JLabel("Đến:")); pnlFilter.add(txtDenNgay);
        pnlFilter.add(btnLoc);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlFilter, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        btnLoc.addActionListener(e -> refreshAllData());
    }

    private void initMainTabs() {
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // TAB 1: DOANH THU
        mainTabbedPane.addTab("DOANH THU", createRevenuePanel());

        // TAB 2: LỢI NHUẬN
        mainTabbedPane.addTab("LỢI NHUẬN", createProfitPanel());

        // TAB 3: TỒN KHO
        mainTabbedPane.addTab("TỒN KHO", createInventoryPanel());

        add(mainTabbedPane, BorderLayout.CENTER);
    }

    // --- HÀM SET TAB CHO SIDEBAR GỌI ---
    public void setSelectedTab(int index) {
        if (index >= 0 && index < mainTabbedPane.getTabCount()) {
            mainTabbedPane.setSelectedIndex(index);
        }
    }

    private JPanel createRevenuePanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setBackground(COLOR_CREAM);

        pnl.add(createCard("TỔNG DOANH THU BÁN HÀNG", lblRevenue = new JLabel("0"), COLOR_PRIMARY), BorderLayout.NORTH);

        chartRevenue = new CustomChartPanel(true);
        pnl.add(chartRevenue, BorderLayout.CENTER);

        modelRevenue = new DefaultTableModel(new String[]{"Tháng", "Doanh Thu"}, 0);
        tblRevenue = new JTable(modelRevenue);
        setupTable(tblRevenue);
        pnl.add(new JScrollPane(tblRevenue), BorderLayout.SOUTH);
        tblRevenue.getParent().getParent().setPreferredSize(new Dimension(0, 150));

        return pnl;
    }

    private JPanel createProfitPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setBackground(COLOR_CREAM);

        JPanel pnlCards = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlCards.setOpaque(false);
        pnlCards.add(createCard("LỢI NHUẬN THUẦN", lblProfit = new JLabel("0"), COLOR_PROFIT));
        pnlCards.add(createCard("TỔNG VỐN ĐẦU TƯ", lblCost = new JLabel("0"), COLOR_DARK));
        pnl.add(pnlCards, BorderLayout.NORTH);

        chartProfit = new CustomChartPanel(false); // false = vẽ so sánh
        pnl.add(chartProfit, BorderLayout.CENTER);

        modelProfit = new DefaultTableModel(new String[]{"Tháng", "Vốn Nhập", "Lợi Nhuận"}, 0);
        tblProfit = new JTable(modelProfit);
        setupTable(tblProfit);
        pnl.add(new JScrollPane(tblProfit), BorderLayout.SOUTH);
        tblProfit.getParent().getParent().setPreferredSize(new Dimension(0, 150));

        return pnl;
    }

    private JPanel createInventoryPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setBackground(COLOR_CREAM);

        JLabel lblWarn = new JLabel("DANH SÁCH SẢN PHẨM CẦN NHẬP THÊM (TỒN < 10)", SwingConstants.CENTER);
        lblWarn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWarn.setForeground(Color.RED);
        pnl.add(lblWarn, BorderLayout.NORTH);

        modelInventory = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Loại", "NXB", "Tồn Kho"}, 0);
        tblInventory = new JTable(modelInventory);
        setupTable(tblInventory);
        pnl.add(new JScrollPane(tblInventory), BorderLayout.CENTER);

        return pnl;
    }

    private void refreshAllData() {
        String tu = txtTuNgay.getText().trim();
        String den = txtDenNgay.getText().trim();

        // Cập nhật card
        lblRevenue.setText(formatter.format(tkBus.getTongDoanhThu(tu, den)));
        lblProfit.setText(formatter.format(tkBus.getLoiNhuan(tu, den)));
        lblCost.setText(formatter.format(tkBus.getTongVon(tu, den)));

        // Cập nhật dữ liệu tháng
        currentData = tkBus.getDoanhThuTheoThang(tu, den);

        modelRevenue.setRowCount(0);
        modelProfit.setRowCount(0);
        for (Object[] row : currentData) {
            modelRevenue.addRow(new Object[]{row[0], formatter.format(row[2])});
            modelProfit.addRow(new Object[]{row[0], formatter.format(row[1]), formatter.format(row[3])});
        }

        // Cập nhật tồn kho
        List<Object[]> inv = tkBus.getSachSapHet();
        modelInventory.setRowCount(0);
        for (Object[] row : inv) modelInventory.addRow(row);

        repaint();
    }

    // --- CÁC HÀM TIỆN ÍCH UI ---
    private JPanel createCard(String title, JLabel lblValue, Color accentColor) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(accentColor, 2), new EmptyBorder(10, 10, 10, 10)));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(Color.GRAY);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(accentColor);
        pnl.add(t, BorderLayout.NORTH); pnl.add(lblValue, BorderLayout.CENTER);
        return pnl;
    }

    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(255, 143, 183));
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setPreferredSize(new Dimension(0, 35));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(COLOR_PRIMARY); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
    }

    private String formatShort(double a) {
        if (a >= 1e6) return String.format("%.1f tr", a / 1e6).replace(".0", "");
        if (a >= 1e3) return String.format("%.0f k", a / 1e3);
        return String.format("%.0f", a);
    }

    // --- LỚP VẼ BIỂU ĐỒ 2D ---
    // --- LỚP VẼ BIỂU ĐỒ 2D ĐÃ ĐƯỢC FIX TỶ LỆ ---
    class CustomChartPanel extends JPanel {
        private boolean isRevenueOnly;
        public CustomChartPanel(boolean type) { this.isRevenueOnly = type; setBackground(Color.WHITE); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentData == null || currentData.isEmpty()) {
                g.drawString("Không có dữ liệu trong khoảng thời gian này.", 20, 30);
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int pL = 80, pR = 30, pB = 40, pT = 30; // Tăng margin trái (pL) để chứa vừa số tiền lớn

            // Tìm giá trị cao nhất để làm đỉnh biểu đồ
            double max = 1; // Khởi tạo bằng 1 để tránh lỗi chia cho 0
            for (Object[] r : currentData) {
                double dt = (double) r[2]; // Cột Doanh Thu
                double ln = (double) r[3]; // Cột Lợi Nhuận
                if (dt > max) max = dt;
                if (!isRevenueOnly && ln > max) max = ln;
            }
            max *= 1.2; // Tăng thêm 20% khoảng không ở trên đỉnh

            // Vẽ lưới Y
            g2.setColor(new Color(230, 230, 230));
            for (int i = 0; i <= 4; i++) {
                int y = h - pB - (int)((h - pB - pT) * i / 4.0);
                g2.drawLine(pL, y, w - pR, y);
                g2.setColor(Color.GRAY);
                g2.drawString(formatShort((max / 4) * i), 10, y + 5);
                g2.setColor(new Color(230, 230, 230));
            }

            int groupW = (w - pL - pR) / currentData.size();
            int barW = Math.min(40, groupW / 3);

            for (int i = 0; i < currentData.size(); i++) {
                Object[] d = currentData.get(i);
                int x = pL + (i * groupW) + (groupW / 4);

                // Cột Doanh Thu
                int hR = (int)(((double)d[2] / max) * (h - pB - pT));
                // Nếu doanh thu âm thì set chiều cao bằng 0 để không bị lỗi vẽ ngược
                if (hR < 0) hR = 0;
                g2.setPaint(new GradientPaint(x, h-pB-hR, COLOR_PRIMARY, x, h-pB, COLOR_DARK));
                g2.fillRoundRect(x, h - pB - hR, barW, hR, 5, 5);

                if (!isRevenueOnly) {
                    // Cột Lợi Nhuận
                    int hP = (int)(((double)d[3] / max) * (h - pB - pT));
                    if (hP < 0) hP = 0; // Tránh lỗi lỗ vốn vẽ ngược
                    g2.setPaint(new GradientPaint(x + barW + 5, h-pB-hP, COLOR_PROFIT, x + barW + 5, h-pB, COLOR_DARK));
                    g2.fillRoundRect(x + barW + 5, h - pB - hP, barW, hP, 5, 5);
                }

                g2.setColor(COLOR_DARK);
                g2.drawString(d[0].toString(), x, h - 15);
            }
        }
    }
}