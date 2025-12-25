package com.mycompany.rpl_ujian.view;

import com.mycompany.rpl_ujian.model.DoctorSchedule;
import com.mycompany.rpl_ujian.model.User;
import com.mycompany.rpl_ujian.repository.UserRepository;
import com.mycompany.rpl_ujian.service.DoctorScheduleService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class DoctorSchedulePanel extends JPanel {

    private final transient DoctorScheduleService scheduleService;
    private final transient UserRepository userRepository;
    private final transient User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorSchedulePanel(ApplicationContext context, User currentUser) {
        this.scheduleService = context.getBean(DoctorScheduleService.class);
        this.userRepository = context.getBean(UserRepository.class);
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Toolbar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshButton = new JButton("Refresh");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(refreshButton);
        refreshButton.setBackground(com.mycompany.rpl_ujian.util.UIUtils.SECONDARY_COLOR);
        refreshButton.setForeground(com.mycompany.rpl_ujian.util.UIUtils.TEXT_COLOR);
        refreshButton.addActionListener(e -> loadData());
        topPanel.add(refreshButton);

        if ("ADMIN".equals(currentUser.getRole())) {
            JButton addButton = new JButton("Add Schedule");
            com.mycompany.rpl_ujian.util.UIUtils.styleButton(addButton);
            addButton.addActionListener(e -> showAddDialog());
            topPanel.add(addButton);

            JButton deleteButton = new JButton("Delete Schedule");
            com.mycompany.rpl_ujian.util.UIUtils.styleButton(deleteButton);
            deleteButton.setBackground(new Color(220, 53, 69)); // Red
            deleteButton.addActionListener(e -> deleteSelected());
            topPanel.add(deleteButton);
        }

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Doctor", "Day", "Start Time", "End Time" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        com.mycompany.rpl_ujian.util.UIUtils.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<User> doctors = userRepository.findByRole("DOCTOR");

        for (User doctor : doctors) {
            // If current user is a doctor, only show their own schedule
            if ("DOCTOR".equals(currentUser.getRole()) && !currentUser.getId().equals(doctor.getId())) {
                continue;
            }

            List<DoctorSchedule> schedules = scheduleService.getSchedulesByDoctor(doctor);
            for (DoctorSchedule s : schedules) {
                tableModel.addRow(new Object[] {
                        s.getId(),
                        doctor.getFullName(),
                        s.getDayOfWeek(),
                        s.getStartTime(),
                        s.getEndTime()
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Schedule", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Doctor Combo
        List<User> doctors = userRepository.findByRole("DOCTOR");
        JComboBox<UserItem> doctorCombo = new JComboBox<>();
        for (User d : doctors) {
            doctorCombo.addItem(new UserItem(d));
        }
        doctorCombo.setFont(com.mycompany.rpl_ujian.util.UIUtils.REGULAR_FONT);

        // Day Combo
        JComboBox<DayOfWeek> dayCombo = new JComboBox<>(DayOfWeek.values());
        dayCombo.setFont(com.mycompany.rpl_ujian.util.UIUtils.REGULAR_FONT);

        JTextField startField = new JTextField("09:00", 10);
        JTextField endField = new JTextField("17:00", 10);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(startField);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(endField);

        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Doctor:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(doctorCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Day:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(dayCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Start Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(startField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("End Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(endField, gbc);

        JButton saveButton = new JButton("Save");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            try {
                UserItem selectedDoctor = (UserItem) doctorCombo.getSelectedItem();
                DayOfWeek selectedDay = (DayOfWeek) dayCombo.getSelectedItem();
                LocalTime start = LocalTime.parse(startField.getText());
                LocalTime end = LocalTime.parse(endField.getText());

                DoctorSchedule schedule = new DoctorSchedule(selectedDoctor.user, selectedDay, start, end);
                scheduleService.saveSchedule(schedule);

                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(saveButton, gbc);

        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a schedule to delete.");
            return;
        }

        Long id = (Long) table.getValueAt(selectedRow, 0);
        scheduleService.deleteSchedule(id);
        loadData();
    }

    private static class UserItem {
        User user;

        UserItem(User u) {
            this.user = u;
        }

        public String toString() {
            return user.getFullName();
        }
    }
}
