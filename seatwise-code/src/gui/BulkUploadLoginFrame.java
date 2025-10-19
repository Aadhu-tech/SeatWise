package gui;
import javax.swing.*;
import java.io.*;

import dao.DatabaseConnection;
import dao.LoginDAO;
import java.util.Map;

public class BulkUploadLoginFrame extends JFrame {
    private JButton uploadButton;
    
    public BulkUploadLoginFrame() {
        setTitle("Upload Login Credentials"); 
        setSize(420,200); 
        setLayout(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        uploadButton = new JButton("Select CSV & Upload"); 
        uploadButton.setBounds(120,60,180,30); 
        add(uploadButton);
        
        uploadButton.addActionListener(ignored -> {
            JFileChooser fc = new JFileChooser(); 
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            
            File file = fc.getSelectedFile();
            try {
                // Clear existing login data first
                try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
                    java.sql.Statement stmt = conn.createStatement();
                    stmt.executeUpdate("DELETE FROM Login");
                }
                
                // Load credentials from CSV
                Map<String, LoginDAO.LoginCredentials> credentials = 
                    LoginDAO.loadCredentialsFromCSV(file.getAbsolutePath());
                
                // Save to database
                LoginDAO.saveCredentialsToDB(credentials);
                
                JOptionPane.showMessageDialog(this, 
                    credentials.size() + " login credentials uploaded. Previous data cleared."); 
                dispose(); // Close dialog on success
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage()); 
            }
        });
        
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
