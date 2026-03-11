package gui;

import enums.Role;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private String currentRole;
    private JPanel mainContentPanel;
    private DashboardPanel dashboardPanel;

    public MainFrame(String role) {
        this.currentRole = role;
        initUI();
    }

    private void initUI() {
        setTitle("QUẢN LÝ NHÀ SÁCH - " + currentRole.toUpperCase());
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Ráp Sidebar vào bên trái
        SidebarPanel sidebarPanel = new SidebarPanel(currentRole, this);
        JScrollPane scrollPane = new JScrollPane(sidebarPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(260, 0));
        add(scrollPane, BorderLayout.WEST);

        // Chuẩn bị khu vực Content bên phải
        mainContentPanel = new JPanel(new BorderLayout());
        dashboardPanel = new DashboardPanel(this, currentRole);
        mainContentPanel.add(dashboardPanel, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    // Hàm này cho Sidebar gọi khi click menu con
    public void switchPanel(JPanel newPanel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // Hàm này cho Sidebar gọi khi click nút Trang Chủ
    public void showDashboard() {
        switchPanel(dashboardPanel);
    }


}