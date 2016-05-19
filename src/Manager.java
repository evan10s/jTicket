import javax.swing.*;

/**
 * Created by Evan on 5/18/2016.
 */
public class Manager {
    private JTextField ticketType;
    private JButton createTicketsButton;
    private JTextField textField2;
    private JCheckBox yesCheckBox;
    private JPanel formContainer;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Manager");
        frame.setContentPane(new Manager().formContainer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        //frame.setVisible(true);
    }
}
