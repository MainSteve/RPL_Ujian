package com.mycompany.rpl_ujian.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIUtils {

    // Colors
    public static final Color PRIMARY_COLOR = new Color(51, 153, 255); // Nice Blue
    public static final Color SECONDARY_COLOR = new Color(240, 240, 240); // Light Gray
    public static final Color TEXT_COLOR = new Color(50, 50, 50); // Dark Gray
    public static final Color WHITE_COLOR = Color.WHITE;
    public static final Color ACCENT_COLOR = new Color(0, 102, 204); // Darker Blue

    // Fonts
    private static final String FONT_NAME = "Segoe UI";
    public static final Font HEADER_FONT = new Font(FONT_NAME, Font.BOLD, 24);
    public static final Font SUBHEADER_FONT = new Font(FONT_NAME, Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font(FONT_NAME, Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font(FONT_NAME, Font.BOLD, 14);

    private UIUtils() {
        // Private constructor to hide implicit public one
    }

    public static void styleButton(JButton button) {
        button.setFont(BOLD_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(WHITE_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(REGULAR_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(REGULAR_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    public static void styleTable(JTable table) {
        table.setFont(REGULAR_FONT);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    public static JLabel createSubHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBHEADER_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(REGULAR_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }
}
