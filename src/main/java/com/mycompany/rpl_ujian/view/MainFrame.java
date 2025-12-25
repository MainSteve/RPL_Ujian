package com.mycompany.rpl_ujian.view;

import com.mycompany.rpl_ujian.model.User;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final transient ApplicationContext context;
    private final User currentUser;

    public MainFrame(ApplicationContext context, User user) {
        this.context = context;
        this.currentUser = user;

        setTitle("Clinic Appointment Manager - " + user.getRole());
        setSize(1000, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(com.mycompany.rpl_ujian.util.UIUtils.SECONDARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = com.mycompany.rpl_ujian.util.UIUtils
                .createSubHeaderLabel("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(logoutButton);
        logoutButton.setBackground(new Color(220, 53, 69)); // Red for logout
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(com.mycompany.rpl_ujian.util.UIUtils.BOLD_FONT);

        if ("ADMIN".equals(user.getRole())) {
            tabbedPane.addTab("Patients", new PatientManagementPanel(context));
            tabbedPane.addTab("Appointments", new AppointmentManagementPanel(context, currentUser));
            tabbedPane.addTab("Doctor Schedules", new DoctorSchedulePanel(context, currentUser));
        } else if ("DOCTOR".equals(user.getRole())) {
            tabbedPane.addTab("Appointments", new AppointmentManagementPanel(context, currentUser));
            tabbedPane.addTab("My Schedule", new DoctorSchedulePanel(context, currentUser));
            tabbedPane.addTab("Patients", new PatientManagementPanel(context));
        } else if ("PATIENT".equals(user.getRole())) {
            tabbedPane.addTab("My Appointments", new AppointmentManagementPanel(context, currentUser));
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void logout() {
        this.dispose();
        new LoginFrame(context).setVisible(true);
    }
}
