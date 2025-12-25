package com.mycompany.rpl_ujian;

import com.mycompany.rpl_ujian.config.HibernateConfig;
import com.mycompany.rpl_ujian.view.LoginFrame;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;

public class RPL_Ujian {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Spring Context
                ApplicationContext context = new AnnotationConfigApplicationContext(HibernateConfig.class);

                // Seed Data
                com.mycompany.rpl_ujian.util.DataSeeder seeder = context
                        .getBean(com.mycompany.rpl_ujian.util.DataSeeder.class);
                seeder.seed();

                // Set Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Show Login Frame
                LoginFrame loginFrame = new LoginFrame(context);
                loginFrame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage());
            }
        });
    }
}
