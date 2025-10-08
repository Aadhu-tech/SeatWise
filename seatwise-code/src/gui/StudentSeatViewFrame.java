package gui;
import javax.swing.*;
public class StudentSeatViewFrame extends JFrame {
    public StudentSeatViewFrame() {
        setTitle("My Seat Information"); setSize(400,200); setLayout(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel info = new JLabel("This window will display the student's seat info after login."); info.setBounds(20,60,360,30); add(info);
        setLocationRelativeTo(null); setVisible(true);
    }
}
