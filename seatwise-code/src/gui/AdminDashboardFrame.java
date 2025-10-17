package gui;
import javax.swing.*;
import logic.SeatAllocator;
import logic.SeatAllocator.AllocationResult;
import logic.ReportGenerator;
import model.AllocationRecord;
import java.util.List;
public class AdminDashboardFrame extends JFrame {
    private JButton uploadStudentsButton, uploadRoomsButton, uploadExamSlotsButton, allocateSeatsButton, viewReportButton;
    public AdminDashboardFrame() {
        setTitle("Admin Dashboard"); setSize(600,420); setLayout(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        uploadStudentsButton = new JButton("Upload Students"); uploadStudentsButton.setBounds(180,50,240,30); add(uploadStudentsButton);
        uploadRoomsButton = new JButton("Upload Rooms"); uploadRoomsButton.setBounds(180,100,240,30); add(uploadRoomsButton);
        uploadExamSlotsButton = new JButton("Upload Exam Slots"); uploadExamSlotsButton.setBounds(180,150,240,30); add(uploadExamSlotsButton);
        allocateSeatsButton = new JButton("Allocate Seats"); allocateSeatsButton.setBounds(180,200,240,30); add(allocateSeatsButton);
        viewReportButton = new JButton("View Full Report"); viewReportButton.setBounds(180,250,240,30); add(viewReportButton);
        uploadStudentsButton.addActionListener(ignored -> new BulkUploadStudentFrame());
        uploadRoomsButton.addActionListener(ignored -> new BulkUploadRoomFrame());
        uploadExamSlotsButton.addActionListener(ignored -> new BulkUploadExamSlotFrame());
        allocateSeatsButton.addActionListener(ignored -> {
            new SwingWorker<AllocationResult, Void>() {
                protected AllocationResult doInBackground() {
                    SeatAllocator allocator = new SeatAllocator();
                    return allocator.allocateSeatsForExam("EXAM1");
                }
                protected void done() {
                    try {
                        AllocationResult res = get();
                        if (res.success) JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Allocation completed successfully.");
                        else JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Allocation failed. Check warnings.");
                        if (!res.warnings.isEmpty()) {
                            JTextArea area = new JTextArea(String.join("\n", res.warnings));
                            area.setEditable(false);
                            JOptionPane.showMessageDialog(AdminDashboardFrame.this, new JScrollPane(area), "Admin Warnings", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) { JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Error: " + ex.getMessage()); }
                }
            }.execute();
        });
        viewReportButton.addActionListener(ignored -> {
            ReportGenerator rg = new ReportGenerator();
            List<AllocationRecord> rows = rg.getAllAllocations();
            if (rows.isEmpty()) { JOptionPane.showMessageDialog(this, "No allocations found."); return; }
            StringBuilder sb = new StringBuilder(); for (AllocationRecord r: rows) sb.append(r.toString()).append("\n");
            JTextArea area = new JTextArea(sb.toString(), 20, 60); area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Full Allocation Report", JOptionPane.INFORMATION_MESSAGE);
        });
        setLocationRelativeTo(null); setVisible(true);
    }
}
