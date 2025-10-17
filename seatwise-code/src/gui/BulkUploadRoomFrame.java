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
				RoomDAO dao = new RoomDAO(); String line; int count=0; boolean first=true;
				while ((line=br.readLine())!=null) {
					line = line.trim(); if (line.isEmpty()) continue;
					String[] p = line.split(",");
					for (int i=0;i<p.length;i++) p[i] = p[i].trim();
					if (first) {
						first = false;
						String lower = p[0].toLowerCase();
						if (lower.contains("room") || lower.contains("roomid") || lower.contains("room_no") || lower.contains("roomno")) {
							continue; // skip header row
						}
					}
					if (p.length < 2) continue; // need at least id and capacity
					String roomId = p[0];
					int capacity;
					try { capacity = Integer.parseInt(p[1]); } catch (NumberFormatException nfe) { continue; }
					boolean isBackup = false;
					if (p.length >= 3) {
						String b = p[2].toLowerCase();
						isBackup = b.equals("true") || b.equals("yes") || b.equals("1");
					}
					Room r = new Room(roomId, capacity, isBackup);
					dao.insertRoom(r);
					count++;
				}
				JOptionPane.showMessageDialog(this, count + " rooms uploaded.");
			} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage()); }
		});
		setLocationRelativeTo(null); setVisible(true);
	}
}
