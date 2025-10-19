package gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.AllocationRecord;
import logic.ReportGenerator;
import java.io.*;
import java.util.List;

public class ReportViewFrame extends JFrame {
    private JTable reportTable;
    private JButton exportButton;
    private List<AllocationRecord> allocations;
    
    public ReportViewFrame() {
        setTitle("Allocation Report");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new java.awt.BorderLayout());
        
        // Get data
        ReportGenerator rg = new ReportGenerator();
        allocations = rg.getAllAllocations();
        
        if (allocations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No allocations found.");
            dispose();
            return;
        }
        
        // Create table
        String[] columns = {"Student ID", "Name", "Branch", "Room ID", "Seat No", "Exam Slot"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        for (AllocationRecord record : allocations) {
            Object[] row = {
                record.getStudentId(),
                record.getStudentName(),
                record.getBranch(),
                record.getRoomId(),
                record.getSeatNo(),
                record.getExamSlotId()
            };
            model.addRow(row);
        }
        
        reportTable = new JTable(model);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        reportTable.getTableHeader().setReorderingAllowed(false);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, java.awt.BorderLayout.CENTER);
        
        // Add export button
        JPanel buttonPanel = new JPanel();
        exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportToCSV());
        buttonPanel.add(exportButton);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        fileChooser.setSelectedFile(new File("allocation_report.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                writer.println("Student ID,Name,Branch,Room ID,Seat No,Exam Slot");
                
                // Write data
                for (AllocationRecord record : allocations) {
                    writer.printf("%s,%s,%s,%s,%d,%s%n",
                        record.getStudentId(),
                        record.getStudentName(),
                        record.getBranch(),
                        record.getRoomId(),
                        record.getSeatNo(),
                        record.getExamSlotId()
                    );
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Report exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting file: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
