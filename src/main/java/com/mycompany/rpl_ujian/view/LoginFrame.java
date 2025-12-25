package com.mycompany.rpl_ujian.view;

import com.mycompany.rpl_ujian.model.User;
import com.mycompany.rpl_ujian.service.AuthService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final ApplicationContext context;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(ApplicationContext context) {
        this.context = context;
        this.authService = context.getBean(AuthService.class);

        setTitle("Clinic Appointment Manager - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel headerLabel = com.mycompany.rpl_ujian.util.UIUtils.createHeaderLabel("Welcome Back");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(headerLabel, gbc);

        // Subtitle
        JLabel subLabel = com.mycompany.rpl_ujian.util.UIUtils.createLabel("Please login to your account");
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        add(subLabel, gbc);

        // Username Label
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Username"), gbc);

        // Username Field
        usernameField = new JTextField(20);
        com.mycompany.rpl_ujian.util.UIUtils.styleTextField(usernameField);
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(usernameField, gbc);

        // Password Label
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(com.mycompany.rpl_ujian.util.UIUtils.createLabel("Password"), gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        com.mycompany.rpl_ujian.util.UIUtils.stylePasswordField(passwordField);
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(passwordField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        com.mycompany.rpl_ujian.util.UIUtils.styleButton(loginButton);
        loginButton.addActionListener(this::handleLogin);
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Initialize admin if needed
        authService.createAdminIfNotExists();
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = authService.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user.getFullName());
            // Open MainFrame
            MainFrame mainFrame = new MainFrame(context, user);
            mainFrame.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
