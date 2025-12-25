package com.mycompany.rpl_ujian.view;

import com.mycompany.rpl_ujian.model.Appointment;
import com.mycompany.rpl_ujian.model.Patient;
import com.mycompany.rpl_ujian.model.User;
import com.mycompany.rpl_ujian.repository.UserRepository;
import com.mycompany.rpl_ujian.service.AppointmentService;
import com.mycompany.rpl_ujian.service.PatientService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class AppointmentManagementPanel extends JPanel {

    private final transient AppointmentService appointmentService;
    private final transient PatientService patientService;
    private final transient UserRepository userRepository;
    private final transient User currentUser;
    private final transient ApplicationContext context;
    private JTable table;
    private DefaultTableModel tableModel;

    public AppointmentManagementPanel(ApplicationContext context, User currentUser) {
        this.context = context;
        this.appointmentService = context.getBean(AppointmentService.class);
        this.patientService = context.getBean(PatientService.class);
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

        if (!"DOCTOR".equals(currentUser.getRole())) {
            JButton bookButton = new JButton("Book Appointment");
            com.mycompany.rpl_ujian.util.UIUtils.styleButton(bookButton);
            bookButton.addActionListener(e -> showBookDialog());
            topPanel.add(bookButton);
        } else {
            JButton prescriptionButton = new JButton("Add Prescription");
            com.mycompany.rpl_ujian.util.UIUtils.styleButton(prescriptionButton);
            prescriptionButton.addActionListener(e -> showPrescriptionDialog());
            topPanel.add(prescriptionButton);
        }

        if ("ADMIN".equals(currentUser.getRole())) {
            JButton rescheduleButton = new JButton("Reschedule");
            com.mycompany.rpl_ujian.util.UIUtils.styleButton(rescheduleButton);
            rescheduleButton.addActionListener(e -> showRescheduleDialog());
            topPanel.add(rescheduleButton);

            JButton cancelButton = new JButton("Cancel");
            com.mycompany.rpl_ujian.util.UIUtils.styleButton(cancelButton);
            cancelButton.setBackground(new Color(220, 53, 69)); // Red
            cancelButton.addActionListener(e -> cancelAppointment());
            topPanel.add(cancelButton);
        }

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Patient", "Doctor", "Date", "Status", "Notes" };
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
        List<Appointment> appointments = appointmentService.getAllAppointments();

        for (Appointment a : appointments) {
            // Filter for Patient
            if ("PATIENT".equals(currentUser.getRole())) {
                if (a.getPatient() == null || a.getPatient().getUser() == null
                        || !a.getPatient().getUser().getId().equals(currentUser.getId())) {
                    continue;
                }
            }
            // Filter for Doctor
            if ("DOCTOR".equals(currentUser.getRole())) {
                if (!a.getDoctor().getId().equals(currentUser.getId())) {
                    continue;
                }
            }

            tableModel.addRow(new Object[] {
                    a.getId(),
                    a.getPatient().getName(),
                    a.getDoctor().getFullName(),
                    a.getAppointmentDate(),
                    a.getStatus(),
                    a.getNotes()
            });
        }
    }

    private void showBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Appointment", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Patient Dropdown or Label
        JComboBox<PatientItem> patientCombo = new JComboBox<>();
        if ("PATIENT".equals(currentUser.getRole())) {
            Optional<Patient> p = patientService.getPatientByUser(currentUser);
            if (p.isPresent()) {
                patientCombo.addItem(new PatientItem(p.get()));
                patientCombo.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "No patient record found for your account.");
                return;
            }
        } else {
            List<Patient> patients = patientService.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(new PatientItem(p));
            }
        }
        patientCombo.setFont(com.mycompany.rpl_ujian.util.UIUtils.REGULAR_FONT);

        // Doctor Dropdown
        List<User> doctors = userRepository.findByRole("DOCTOR");
        JComboBox<UserItem> doctorCombo = new JComboBox<>();
        for (User d : doctors) {
            doctorCombo.addItem(new UserItem(d));
        }
        doctorCombo.setFont(com.mycompany.rpl_ujian.util.UIUtils.REGULAR_FONT);

        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), 20);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(dateField);

        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setFont(com.mycompany.rpl_ujian.util.UIUtils.REGULAR_FONT);
        notesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Patient:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(patientCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Doctor:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(doctorCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Date (yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.BOTH; // Allow vertical expansion for notes
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Notes:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Expand vertically
        dialog.add(new JScrollPane(notesArea), gbc);

        JButton saveButton = new JButton("Book");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            try {
                PatientItem selectedPatient = (PatientItem) patientCombo.getSelectedItem();
                UserItem selectedDoctor = (UserItem) doctorCombo.getSelectedItem();

                if (selectedPatient == null || selectedDoctor == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select patient and doctor");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = sdf.parse(dateField.getText());

                Appointment appointment = new Appointment();
                appointment.setPatient(selectedPatient.patient);
                appointment.setDoctor(selectedDoctor.user);
                appointment.setAppointmentDate(date);
                appointment.setStatus("SCHEDULED");
                appointment.setNotes(notesArea.getText());

                appointmentService.saveAppointment(appointment);
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error booking appointment: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // Reset weight
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(saveButton, gbc);

        dialog.setVisible(true);
    }

    private void showRescheduleDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to reschedule.");
            return;
        }
        Long id = (Long) table.getValueAt(selectedRow, 0);

        String newDateStr = JOptionPane.showInputDialog(this, "Enter new date (yyyy-MM-dd HH:mm):");
        if (newDateStr != null && !newDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = sdf.parse(newDateStr);
                appointmentService.rescheduleAppointment(id, date);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format.");
            }
        }
    }

    private void cancelAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to cancel.");
            return;
        }
        Long id = (Long) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentService.cancelAppointment(id);
            loadData();
        }
    }

    private void showPrescriptionDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        Long appointmentId = (Long) table.getValueAt(selectedRow, 0);

        Appointment appointment = appointmentService.getAllAppointments().stream()
                .filter(a -> a.getId().equals(appointmentId)).findFirst().orElse(null);

        if (appointment == null)
            return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Prescription", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField medicationField = new JTextField(20);
        JTextField dosageField = new JTextField(20);
        JTextArea instructionsArea = new JTextArea(3, 20);

        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(medicationField);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(dosageField);
        instructionsArea.setFont(com.mycompany.rpl_ujian.util.UIUtils.REGULAR_FONT);
        instructionsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Medication:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(medicationField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Dosage:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(dosageField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.BOTH;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Instructions:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(new JScrollPane(instructionsArea), gbc);

        JButton saveButton = new JButton("Save Prescription");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            try {
                com.mycompany.rpl_ujian.model.Prescription p = new com.mycompany.rpl_ujian.model.Prescription();
                p.setAppointment(appointment);
                p.setMedication(medicationField.getText());
                p.setDosage(dosageField.getText());
                p.setInstructions(instructionsArea.getText());

                appointmentService.addPrescription(p);

                // Also update appointment status to COMPLETED
                appointment.setStatus("COMPLETED");
                appointmentService.saveAppointment(appointment);

                dialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this, "Prescription added and appointment marked as COMPLETED.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving prescription: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(saveButton, gbc);

        dialog.setVisible(true);
    }

    // Helper classes for ComboBox
    private static class PatientItem {
        Patient patient;

        PatientItem(Patient p) {
            this.patient = p;
        }

        public String toString() {
            return patient.getName() + " (" + patient.getNik() + ")";
        }
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
