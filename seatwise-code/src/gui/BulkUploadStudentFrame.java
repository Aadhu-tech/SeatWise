package gui;
import javax.swing.*;
import java.io.*;
import dao.StudentDAO;
import model.Student;
public class BulkUploadStudentFrame extends JFrame {
    private JButton uploadButton;
    public BulkUploadStudentFrame() {
        setTitle("Upload Student Data"); setSize(420,200); setLayout(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        uploadButton = new JButton("Select CSV & Upload"); uploadButton.setBounds(120,60,180,30); add(uploadButton);
        uploadButton.addActionListener(ignored -> {
            JFileChooser fc = new JFileChooser(); if (fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
            File file = fc.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StudentDAO dao = new StudentDAO(); String line; int count=0;
                while ((line=br.readLine())!=null) { String[] p = line.split(","); if (p.length<3) continue; Student s = new Student(p[0].trim(), p[1].trim(), p[2].trim()); dao.insertStudent(s); count++; }
                JOptionPane.showMessageDialog(this, count + " students uploaded."); 
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage()); }
        });
        setLocationRelativeTo(null); setVisible(true);
    }
}
