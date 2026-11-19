import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * Alternative concept: ONE shared Action object used by the button, a menu item, the Enter key of the text field
 * and a keyboard shortcut (Ctrl+K). Adding a new trigger = one line, and ONE listener object in total.
 */
public class ConverterSharedAction extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private static double rate(String c) { return c.equals("USD") ? 0.012 : 0.011; }

    private final Action convertAction = new AbstractAction("Convert") {
        @Override public void actionPerformed(ActionEvent e) { convert(); }
    };

    public ConverterSharedAction() {
        super("Converter - shared Action");
        setLayout(new FlowLayout());
        add(new JLabel("INR")); add(amount); add(target);
        add(new JButton(convertAction));                                   // trigger 1: button
        add(result);

        amount.addActionListener(convertAction);                           // trigger 2: Enter key
        JMenuBar bar = new JMenuBar(); JMenu m = new JMenu("Tools");
        m.add(new JMenuItem(convertAction)); bar.add(m); setJMenuBar(bar); // trigger 3: menu item
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ctrl K"), "convert");         // trigger 4: shortcut
        getRootPane().getActionMap().put("convert", convertAction);

        // method-reference version of the same idea:  button.addActionListener(e -> this.convert())  ==  this::convert needs an ActionEvent parameter
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    private void convert() {
        try {
            double v = Double.parseDouble(amount.getText().trim()) * rate((String) target.getSelectedItem());
            result.setText(String.format("%.2f %s", v, target.getSelectedItem()));
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ConverterSharedAction().setVisible(true)); }
}
