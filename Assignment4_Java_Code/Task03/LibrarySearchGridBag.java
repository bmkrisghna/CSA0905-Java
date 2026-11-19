import java.awt.*;
import java.awt.event.*;

/** Alternative concept: the same screen using ONE container with GridBagLayout (weights decide who grows). Layout only. */
public class LibrarySearchGridBag extends Frame {
    public LibrarySearchGridBag() {
        super("Library Search (GridBagLayout)");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; c.weighty = 0; c.gridx = 0; c.gridwidth = 4;

        c.gridy = 0; Label title = new Label("CAMPUS LIBRARY", Label.CENTER); add(title, c);

        c.gridy = 1; c.gridwidth = 1; c.weightx = 0; add(new Label("Search:"), c);
        c.gridx = 1; c.weightx = 1; add(new TextField(18), c);
        c.gridx = 2; c.weightx = 0; Choice ch = new Choice(); ch.add("All"); add(ch, c);
        c.gridx = 3; add(new Button("Search"), c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 4; c.weightx = 1; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        add(new TextArea("results...", 10, 40), c);                          // only this row has weighty = 1

        c.weighty = 0; c.fill = GridBagConstraints.NONE; c.gridwidth = 1; c.gridy = 3;
        c.gridx = 0; add(new Button("Prev"), c);
        c.gridx = 1; add(new Button("Next"), c);
        c.gridx = 2; add(new Button("Reserve"), c);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 4; c.fill = GridBagConstraints.HORIZONTAL; add(new Label("Status"), c);

        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { System.exit(0); } });
        setSize(600, 400);
    }

    public static void main(String[] args) { new LibrarySearchGridBag().setVisible(true); }
}
