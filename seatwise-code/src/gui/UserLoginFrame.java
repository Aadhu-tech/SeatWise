package gui;
import javax.swing.*;
import logic.ReportGenerator;
import model.AllocationRecord;
public class UserLoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    public UserLoginFrame() {
        setTitle("User Login"); setSize(400,250); setLayout(null); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel lblUsername = new JLabel("Username (StudentID or admin):"); lblUsername.setBounds(30,40,300,25); add(lblUsername);
        usernameField = new JTextField(); usernameField.setBounds(30,70,300,30); add(usernameField);
        JLabel lblPassword = new JLabel("Password:"); lblPassword.setBounds(30,110,100,25); add(lblPassword);
        passwordField = new JPasswordField(); passwordField.setBounds(130,110,200,25); add(passwordField);
        loginButton = new JButton("Login"); loginButton.setBounds(140,150,100,30); add(loginButton);
        loginButton.addActionListener(ignored -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            // Validate login through database
            dao.LoginDAO.LoginCredentials credentials = dao.LoginDAO.validateLogin(username, password);
            if (credentials != null) {
                if (credentials.studentId.equals("admin")) {
                    // Admin login
                    JOptionPane.showMessageDialog(this, "Logged in as Admin");
                    SwingUtilities.invokeLater(() -> new AdminDashboardFrame());
                } else {
                    // Student login
                    JOptionPane.showMessageDialog(this, "Welcome, " + username + "!");
                    SwingUtilities.invokeLater(() -> new StudentDashboardFrame(credentials));
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
            }
        });
        setLocationRelativeTo(null); setVisible(true);
    }
}
