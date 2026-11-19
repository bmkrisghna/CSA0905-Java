import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

/** Style (a): the FRAME implements ActionListener. One actionPerformed for every source -> must check e.getSource(). */
public class ConverterStyleA extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JButton convertButton = new JButton("Convert");
    private final JLabel result = new JLabel(" ");

    private static double rate(String c) { return c.equals("USD") ? 0.012 : 0.011; }

    public ConverterStyleA() {
        super("Converter - Style A (frame implements ActionListener)");
        setLayout(new FlowLayout());
        add(new JLabel("INR")); add(amount); add(target); add(convertButton); add(result);

        convertButton.addActionListener(this);
        amount.addActionListener(this);                          // [5.2] Enter key in the amount field

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {                  // window closing with a WindowAdapter
            @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
        });
        pack();
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (e.getSource() == convertButton || e.getSource() == amount) {     // [5.2] changed condition
            try {
                double v = Double.parseDouble(amount.getText().trim()) * rate((String) target.getSelectedItem());
                String text = String.format("%.2f %s", v, target.getSelectedItem());
                result.setText(text);
                JOptionPane.showMessageDialog(this, text, "Result", JOptionPane.INFORMATION_MESSAGE);   // [5.2] dialog
            } catch (NumberFormatException ex) {
                result.setText("Invalid amount");
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);  // [5.2] dialog
            }
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ConverterStyleA().setVisible(true)); }
}
