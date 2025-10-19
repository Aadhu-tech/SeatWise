package gui;
import javax.swing.*;
import java.io.*;

import dao.DatabaseConnection;
import dao.ExamSlotDAO;
import model.ExamSlot;
public class BulkUploadExamSlotFrame extends JFrame {
    private JButton uploadButton;
    public BulkUploadExamSlotFrame() {
        setTitle("Upload Exam Slot Data"); setSize(420,200); setLayout(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        uploadButton = new JButton("Select CSV & Upload"); uploadButton.setBounds(120,60,180,30); add(uploadButton);
        uploadButton.addActionListener(ignored -> {
            JFileChooser fc = new JFileChooser(); if (fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
            File file = fc.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                ExamSlotDAO dao = new ExamSlotDAO(); 
                
                // Clear existing exam slots first
                try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
                    java.sql.Statement stmt = conn.createStatement();
                    stmt.executeUpdate("DELETE FROM Allocation"); // Clear allocations first due to foreign key
                    stmt.executeUpdate("DELETE FROM ExamSlot");
                }
                
                String line; int count=0; boolean firstLine = true;
                while ((line=br.readLine())!=null) {
                    if (firstLine) { firstLine = false; continue; } // Skip header
                    String[] p = line.split(","); if (p.length<3) continue; 
                    ExamSlot s = new ExamSlot(p[0].trim(), p[1].trim(), p[2].trim()); 
                    dao.insertExamSlot(s); count++; 
                }
                JOptionPane.showMessageDialog(this, count + " exam slots uploaded. Previous data cleared.");
                dispose(); // Close dialog on success
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage()); }
        });
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
