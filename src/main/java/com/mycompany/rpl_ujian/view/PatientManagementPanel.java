package com.mycompany.rpl_ujian.view;

import com.mycompany.rpl_ujian.model.Patient;
import com.mycompany.rpl_ujian.service.PatientService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientManagementPanel extends JPanel {

    private final transient PatientService patientService;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientManagementPanel(ApplicationContext context) {
        this.patientService = context.getBean(PatientService.class);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header / Toolbar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add Patient");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(addButton);

        JButton refreshButton = new JButton("Refresh");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(refreshButton);
        refreshButton.setBackground(com.mycompany.rpl_ujian.util.UIUtils.SECONDARY_COLOR);
        refreshButton.setForeground(com.mycompany.rpl_ujian.util.UIUtils.TEXT_COLOR);

        addButton.addActionListener(e -> showPatientDialog(null));
        refreshButton.addActionListener(e -> loadData());

        topPanel.add(addButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Name", "NIK", "Phone", "Birth Date" };
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
        List<Patient> patients = patientService.getAllPatients();
        for (Patient p : patients) {
            tableModel.addRow(new Object[] { p.getId(), p.getName(), p.getNik(), p.getPhone(), p.getBirthDate() });
        }
    }

    private void showPatientDialog(Patient patient) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Patient Details", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Ensure components expand
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField nameField = new JTextField(patient != null ? patient.getName() : "", 20);
        JTextField nikField = new JTextField(patient != null ? patient.getNik() : "", 20);
        JTextField phoneField = new JTextField(patient != null ? patient.getPhone() : "", 20);
        JTextField addressField = new JTextField(patient != null ? patient.getAddress() : "", 20);
        JTextField dobField = new JTextField(
                patient != null && patient.getBirthDate() != null ? patient.getBirthDate().toString() : "yyyy-MM-dd",
                20);

        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(nameField);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(nikField);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(phoneField);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(addressField);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(dobField);

        gbc.weightx = 0.0; // Reset for label
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Expand for field
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("NIK:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(nikField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        dialog.add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Birth Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dialog.add(dobField, gbc);

        JButton saveButton = new JButton("Save");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(saveButton);
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String nik = nikField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();
                String dobStr = dobField.getText();

                if (name.isEmpty() || nik.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name and NIK are required.");
                    return;
                }

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date dob = sdf.parse(dobStr);

                Patient p = (patient != null) ? patient : new Patient();
                p.setName(name);
                p.setNik(nik);
                p.setPhone(phone);
                p.setAddress(address);
                p.setBirthDate(dob);

                patientService.savePatient(p);

                dialog.dispose();
                loadData();
                JOptionPane.showMessageDialog(this, "Patient saved successfully.");
            } catch (java.text.ParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving patient: " + ex.getMessage());
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
}
