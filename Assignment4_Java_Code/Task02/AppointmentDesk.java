import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Task 2 - Hospital outpatient appointment desk (AWT).
 * The doctor list is loaded ONCE into a Map<String, List<String>>; changing the department only refreshes the Choice.
 * Window events are printed to the console, so you can copy the order of calls for Task 2.4.
 */
public class AppointmentDesk extends Frame {
    private final Map<String, List<String>> doctorsByDept = new LinkedHashMap<>();
    private final Set<String> booked = new HashSet<>();                  // "doctor|slot" already taken

    private final TextField patientId = new TextField(12);
    private final Choice department = new Choice();
    private final Choice doctor = new Choice();
    private final Choice slot = new Choice();
    private final Button book = new Button("Book Appointment");
    private final Label status = new Label("Enter details and press Book");
    private volatile boolean bookingInProgress = false;

    public AppointmentDesk() {
        super("Outpatient Appointment Desk");
        loadDoctors();

        setLayout(new BorderLayout(5, 5));
        Panel form = new Panel(new GridLayout(4, 2, 5, 5));
        form.add(new Label("Patient ID (5 digits):")); form.add(patientId);
        form.add(new Label("Department:"));            form.add(department);
        form.add(new Label("Doctor:"));                form.add(doctor);
        form.add(new Label("Time slot:"));             form.add(slot);
        Panel buttons = new Panel(new FlowLayout());
        buttons.add(book);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        add(status, BorderLayout.NORTH);

        department.add("-- Select department --");
        for (String d : doctorsByDept.keySet()) department.add(d);
        doctor.add("-- Select doctor --");
        for (String s : new String[]{"09:00", "09:30", "10:00", "10:30", "11:00"}) slot.add(s);

        // event source -> event object -> listener
        department.addItemListener(this::onDepartmentChanged);           // Choice -> ItemEvent -> ItemListener
        book.addActionListener(this::onBook);                            // Button -> ActionEvent -> ActionListener
        addWindowListener(new WindowAdapter() {                          // Frame  -> WindowEvent -> WindowListener
            @Override public void windowOpened(WindowEvent e)      { log("windowOpened"); }
            @Override public void windowActivated(WindowEvent e)   { log("windowActivated"); }
            @Override public void windowDeactivated(WindowEvent e) { log("windowDeactivated"); }
            @Override public void windowIconified(WindowEvent e)   { log("windowIconified"); }
            @Override public void windowDeiconified(WindowEvent e) { log("windowDeiconified"); }
            @Override public void windowClosed(WindowEvent e)      { log("windowClosed"); System.exit(0); }
            @Override public void windowClosing(WindowEvent e) {
                log("windowClosing");
                if (bookingInProgress) askBeforeClosing(); else dispose();
            }
        });
        pack();
    }

    private void loadDoctors() {                                         // loaded once, kept in memory
        doctorsByDept.put("Cardiology", List.of("Dr. Rao", "Dr. Mehta"));
        doctorsByDept.put("Orthopaedics", List.of("Dr. Singh", "Dr. Nair", "Dr. Iyer"));
        doctorsByDept.put("Paediatrics", List.of("Dr. Das"));
        doctorsByDept.put("General Medicine", List.of("Dr. Khan", "Dr. Fernandes"));
    }

    private void onDepartmentChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;            // ignore DESELECTED
        doctor.removeAll();
        doctor.add("-- Select doctor --");
        List<String> list = doctorsByDept.get(department.getSelectedItem());
        if (list != null) for (String d : list) doctor.add(d);
        status.setText("Doctors for " + department.getSelectedItem() + " loaded");
    }

    private void onBook(ActionEvent e) {
        String id = patientId.getText().trim();
        if (!id.matches("\\d{5}"))                        { status.setText("ERROR: patient ID must be 5 digits"); return; }
        if (department.getSelectedIndex() == 0)           { status.setText("ERROR: select a department"); return; }
        if (doctor.getSelectedIndex() == 0)               { status.setText("ERROR: select a doctor"); return; }
        String key = doctor.getSelectedItem() + "|" + slot.getSelectedItem();
        if (booked.contains(key))                         { status.setText("ERROR: that slot is already booked"); return; }

        bookingInProgress = true;
        book.setEnabled(false);
        status.setText("Booking...");
        new Thread(() -> {                                               // simulated slow confirmation, off the event thread
            try { Thread.sleep(1200); } catch (InterruptedException ex) { return; }
            EventQueue.invokeLater(() -> {
                booked.add(key);
                bookingInProgress = false;
                book.setEnabled(true);
                status.setText("CONFIRMED: patient " + id + " with " + doctor.getSelectedItem() + " at " + slot.getSelectedItem());
            });
        }).start();
    }

    private void askBeforeClosing() {                                    // AWT Dialog (modal)
        Dialog d = new Dialog(this, "Booking in progress", true);
        d.setLayout(new FlowLayout());
        d.add(new Label("A booking is in progress. Close anyway?"));
        Button yes = new Button("Yes"), no = new Button("No");
        yes.addActionListener(ev -> { d.dispose(); dispose(); });
        no.addActionListener(ev -> d.dispose());
        d.add(yes); d.add(no);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private static void log(String s) { System.out.println("[event] " + s); }

    public static void main(String[] args) {
        System.out.println("[event] main() creates the frame");
        new AppointmentDesk().setVisible(true);
    }
}
