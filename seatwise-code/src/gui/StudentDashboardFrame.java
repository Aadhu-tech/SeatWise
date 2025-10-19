package gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.AllocationRecord;
import model.Student;
import dao.AllocationDAO;
import dao.StudentDAO;
import dao.LoginDAO.LoginCredentials;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private LoginCredentials credentials;
    private Student student;
    private List<AllocationRecord> allocations;
    
    public StudentDashboardFrame(LoginCredentials credentials) {
        this.credentials = credentials;
        setTitle("Student Dashboard - " + credentials.username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new java.awt.BorderLayout());
        
        // Load student details
        loadStudentDetails();
        
        // Load allocations
        loadAllocations();
        
        // Create UI
        createUI();
        
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
    
    private void loadStudentDetails() {
        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students = studentDAO.getAllStudents();
            for (Student s : students) {
                if (s.getStudentId().equals(credentials.studentId)) {
                    this.student = s;
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading student details: " + ex.getMessage());
        }
    }
    
    private void loadAllocations() {
        this.allocations = AllocationDAO.fetchAllAllocations();
        // Filter allocations for this student
        allocations.removeIf(alloc -> !alloc.getStudentId().equals(credentials.studentId));
    }
    
    private void createUI() {
        // Student info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new java.awt.GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        
        infoPanel.add(new JLabel("Student ID:"));
        infoPanel.add(new JLabel(credentials.studentId));
        
        infoPanel.add(new JLabel("Username:"));
        infoPanel.add(new JLabel(credentials.username));
        
        if (student != null) {
            infoPanel.add(new JLabel("Name:"));
            infoPanel.add(new JLabel(student.getName()));
            
            infoPanel.add(new JLabel("Branch:"));
            infoPanel.add(new JLabel(student.getBranch()));
        } else {
            infoPanel.add(new JLabel("Name:"));
            infoPanel.add(new JLabel("Not found"));
            
            infoPanel.add(new JLabel("Branch:"));
            infoPanel.add(new JLabel("Not found"));
        }
        
        add(infoPanel, java.awt.BorderLayout.NORTH);
        
        // Allocations table
        String[] columns = {"Exam Slot ID", "Subject", "Date", "Room ID", "Seat Number"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        for (AllocationRecord alloc : allocations) {
            // Get exam details
            String examDetails = getExamDetails(alloc.getExamSlotId());
            String[] examParts = examDetails.split(" - ");
            String subject = examParts.length > 1 ? examParts[1] : "Unknown";
            String date = examParts.length > 2 ? examParts[2].replace("(", "").replace(")", "") : "Unknown";
            
            Object[] row = {
                alloc.getExamSlotId(),
                subject,
                date,
                alloc.getRoomId(),
                alloc.getSeatNo()
            };
            model.addRow(row);
        }
        
        JTable allocationsTable = new JTable(model);
        allocationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        allocationsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(allocationsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Seat Allocations"));
        add(scrollPane, java.awt.BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadAllocations();
            model.setRowCount(0);
            for (AllocationRecord alloc : allocations) {
                String examDetails = getExamDetails(alloc.getExamSlotId());
                String[] examParts = examDetails.split(" - ");
                String subject = examParts.length > 1 ? examParts[1] : "Unknown";
                String date = examParts.length > 2 ? examParts[2].replace("(", "").replace(")", "") : "Unknown";
                
                Object[] row = {
                    alloc.getExamSlotId(),
                    subject,
                    date,
                    alloc.getRoomId(),
                    alloc.getSeatNo()
                };
                model.addRow(row);
            }
        });
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new UserLoginFrame().setVisible(true);
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
        // Show message if no allocations
        if (allocations.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No seat allocations found for your student ID: " + credentials.studentId + 
                "\nPlease contact the administrator.", 
                "No Allocations", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private String getExamDetails(String examSlotId) {
        try {
            dao.ExamSlotDAO examDAO = new dao.ExamSlotDAO();
            List<model.ExamSlot> exams = examDAO.getAllExamSlots();
            for (model.ExamSlot exam : exams) {
                if (exam.getExamSlotId().equals(examSlotId)) {
                    return exam.getExamSlotId() + " - " + exam.getSubject() + " (" + exam.getDate() + ")";
                }
            }
        } catch (Exception ex) {
            System.err.println("Error getting exam details: " + ex.getMessage());
        }
        return examSlotId + " - Unknown - Unknown";
    }
}
