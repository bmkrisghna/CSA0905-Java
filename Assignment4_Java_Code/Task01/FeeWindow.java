import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;

/**
 * Task 1.2 - the window ONLY collects input and shows results. Each button has its own listener.
 * Run:  java FeeWindow          (demo store, 1.5 s simulated bank delay)
 *       java FeeWindow jdbc     (MySQL via db.properties)
 */
public class FeeWindow extends JFrame {
    private final JTextField regNo = new JTextField(12);
    private final JTextField amount = new JTextField(12);
    private final JButton pay = new JButton("Pay");
    private final JButton clear = new JButton("Clear");
    private final JButton receipt = new JButton("Receipt");
    private final JLabel status = new JLabel("Enter register number and amount");
    private final FeeService service;

    public FeeWindow(FeeService service) {
        super("Fee Payment");
        this.service = service;

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Register No:")); form.add(regNo);
        form.add(new JLabel("Amount (Rs):")); form.add(amount);
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(pay); buttons.add(clear); buttons.add(receipt);
        setLayout(new BorderLayout(5, 5));
        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        pay.addActionListener(e -> onPay());                    // one listener per button
        clear.addActionListener(e -> onClear());
        receipt.addActionListener(e -> onReceipt());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);          // window closing handled explicitly
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        pack();
        setLocationRelativeTo(null);
    }

    private void onPay() {
        pay.setEnabled(false);
        status.setText("Contacting bank...");
        String reg = regNo.getText(), amt = amount.getText();
        new SwingWorker<Receipt, Void>() {                      // slow work runs OFF the event-dispatch thread
            @Override protected Receipt doInBackground() throws Exception { return service.pay(reg, amt); }
            @Override protected void done() {                   // back on the event-dispatch thread
                pay.setEnabled(true);
                try { status.setText("Paid. " + get()); }
                catch (java.util.concurrent.ExecutionException e) { status.setText("Error: " + e.getCause().getMessage()); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }.execute();
    }

    private void onClear() { regNo.setText(""); amount.setText(""); status.setText("Cleared"); }

    private void onReceipt() {
        try { JOptionPane.showMessageDialog(this, service.lastReceipt().toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE); }
        catch (FeeException e) { status.setText(e.getMessage()); }
    }

    public static void main(String[] args) throws Exception {
        PaymentStore store = (args.length > 0 && args[0].equals("jdbc"))
                ? new JdbcPaymentStore(new File("db.properties"))
                : new InMemoryPaymentStore(1500);
        SwingUtilities.invokeLater(() -> new FeeWindow(new FeeService(store)).setVisible(true));
    }
}
