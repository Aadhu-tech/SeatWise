package gui;
import javax.swing.*;
import logic.SeatAllocator;
import logic.SeatAllocator.AllocationResult;
import logic.ReportGenerator;
import model.AllocationRecord;
import java.util.List;
public class AdminDashboardFrame extends JFrame {
    private JButton uploadStudentsButton, uploadRoomsButton, uploadExamSlotsButton, uploadLoginButton, allocateSeatsButton, viewReportButton;
    public AdminDashboardFrame() {
        setTitle("Admin Dashboard"); setSize(600,420); setLayout(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        uploadStudentsButton = new JButton("Upload Students"); uploadStudentsButton.setBounds(180,50,240,30); add(uploadStudentsButton);
        uploadRoomsButton = new JButton("Upload Rooms"); uploadRoomsButton.setBounds(180,100,240,30); add(uploadRoomsButton);
        uploadExamSlotsButton = new JButton("Upload Exam Slots"); uploadExamSlotsButton.setBounds(180,150,240,30); add(uploadExamSlotsButton);
        uploadLoginButton = new JButton("Upload Login Credentials"); uploadLoginButton.setBounds(180,200,240,30); add(uploadLoginButton);
        allocateSeatsButton = new JButton("Allocate Seats"); allocateSeatsButton.setBounds(180,250,240,30); add(allocateSeatsButton);
        viewReportButton = new JButton("View Full Report"); viewReportButton.setBounds(180,300,240,30); add(viewReportButton);
        uploadStudentsButton.addActionListener(ignored -> {
            SwingUtilities.invokeLater(() -> new BulkUploadStudentFrame());
        });
        uploadRoomsButton.addActionListener(ignored -> {
            SwingUtilities.invokeLater(() -> new BulkUploadRoomFrame());
        });
        uploadExamSlotsButton.addActionListener(ignored -> {
            SwingUtilities.invokeLater(() -> new BulkUploadExamSlotFrame());
        });
        uploadLoginButton.addActionListener(ignored -> {
            SwingUtilities.invokeLater(() -> new BulkUploadLoginFrame());
        });
        allocateSeatsButton.addActionListener(ignored -> {
            // Check if exam slots exist first
            try {
                dao.ExamSlotDAO examDAO = new dao.ExamSlotDAO();
                java.util.List<model.ExamSlot> examSlots = examDAO.getAllExamSlots();
                if (examSlots.isEmpty()) {
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this, 
                        "No exam slots found. Please upload exam slots first.", 
                        "No Exam Slots", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Filter out header-like entries and show exam selection dialog
                java.util.List<String> validExams = new java.util.ArrayList<>();
                for (model.ExamSlot slot : examSlots) {
                    String examId = slot.getExamSlotId();
                    String subject = slot.getSubject();
                    String date = slot.getDate();
                    
                    // Skip header-like entries
                    if (examId == null || examId.trim().isEmpty() || 
                        examId.toLowerCase().contains("exam") && examId.toLowerCase().contains("slot") ||
                        subject == null || subject.trim().isEmpty() ||
                        date == null || date.trim().isEmpty()) {
                        continue;
                    }
                    
                    validExams.add(examId + " - " + subject + " (" + date + ")");
                }
                
                if (validExams.isEmpty()) {
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this, 
                        "No valid exam slots found. Please check your uploaded data.", 
                        "No Valid Exam Slots", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String[] examOptions = validExams.toArray(new String[0]);
                
                String selectedExam = (String) JOptionPane.showInputDialog(
                    AdminDashboardFrame.this,
                    "Select an exam slot to allocate seats for:",
                    "Select Exam Slot",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    examOptions,
                    examOptions[0]
                );
                
                if (selectedExam == null) return; // User cancelled
                
                // Extract exam slot ID from selection
                String examSlotId = selectedExam.split(" - ")[0];
                
                new SwingWorker<AllocationResult, Void>() {
                    protected AllocationResult doInBackground() {
                        SeatAllocator allocator = new SeatAllocator();
                        return allocator.allocateSeatsForExam(examSlotId);
                    }
                    protected void done() {
                        try {
                            AllocationResult res = get();
                            if (res.success) JOptionPane.showMessageDialog(AdminDashboardFrame.this, 
                                "Allocation completed successfully for exam: " + examSlotId);
                            else JOptionPane.showMessageDialog(AdminDashboardFrame.this, 
                                "Allocation failed. Check warnings.");
                            if (!res.warnings.isEmpty()) {
                                JTextArea area = new JTextArea(String.join("\n", res.warnings));
                                area.setEditable(false);
                                JOptionPane.showMessageDialog(AdminDashboardFrame.this, new JScrollPane(area), "Admin Warnings", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (Exception ex) { 
                            JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Error: " + ex.getMessage()); 
                        }
                    }
                }.execute();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(AdminDashboardFrame.this, 
                    "Error checking exam slots: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        viewReportButton.addActionListener(ignored -> {
            SwingUtilities.invokeLater(() -> new ReportViewFrame());
        });
        setLocationRelativeTo(null); setVisible(true);
    }
}
