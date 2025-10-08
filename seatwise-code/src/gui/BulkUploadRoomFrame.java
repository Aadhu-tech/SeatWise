package gui;
import javax.swing.*;
import java.io.*;
import dao.RoomDAO;
import model.Room;
public class BulkUploadRoomFrame extends JFrame {
    private JButton uploadButton;
    public BulkUploadRoomFrame() {
        setTitle("Upload Room Data"); setSize(420,200); setLayout(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        uploadButton = new JButton("Select CSV & Upload"); uploadButton.setBounds(120,60,180,30); add(uploadButton);
        uploadButton.addActionListener(ignored -> {
            JFileChooser fc = new JFileChooser(); if (fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
            File file = fc.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                RoomDAO dao = new RoomDAO(); String line; int count=0;
                while ((line=br.readLine())!=null) { String[] p = line.split(","); if (p.length<3) continue; Room r = new Room(p[0].trim(), Integer.parseInt(p[1].trim()), Boolean.parseBoolean(p[2].trim())); dao.insertRoom(r); count++; }
                JOptionPane.showMessageDialog(this, count + " rooms uploaded.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage()); }
        });
        setLocationRelativeTo(null); setVisible(true);
    }
}
