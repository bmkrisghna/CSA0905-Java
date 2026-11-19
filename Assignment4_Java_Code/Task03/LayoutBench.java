import java.awt.*;

/** Measures layout computation time and container count for 3 designs. Needs a display (it creates AWT components). */
public class LayoutBench {

    static Panel nested() {                                   // BorderLayout + FlowLayout panels (5 containers)
        Panel root = new Panel(new BorderLayout());
        root.add(new Label("Title"), BorderLayout.NORTH);
        Panel centre = new Panel(new BorderLayout());
        Panel row = new Panel(new FlowLayout());
        row.add(new Label("Search")); row.add(new TextField(15)); row.add(new Choice()); row.add(new Button("Search"));
        centre.add(row, BorderLayout.NORTH); centre.add(new TextArea(5, 20), BorderLayout.CENTER);
        Panel bottom = new Panel(new BorderLayout());
        Panel buttons = new Panel(new FlowLayout());
        buttons.add(new Button("Prev")); buttons.add(new Button("Next")); buttons.add(new Button("Reserve"));
        bottom.add(buttons, BorderLayout.NORTH); bottom.add(new Label("Status"), BorderLayout.SOUTH);
        root.add(centre, BorderLayout.CENTER); root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    static Panel gridBag() {                                  // ONE container
        Panel p = new Panel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 4; p.add(new Label("Title"), c);
        c.gridy = 1; c.gridwidth = 1; p.add(new Label("Search"), c);
        c.gridx = 1; p.add(new TextField(15), c); c.gridx = 2; p.add(new Choice(), c); c.gridx = 3; p.add(new Button("Search"), c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 4; c.weighty = 1; c.fill = GridBagConstraints.BOTH; p.add(new TextArea(5, 20), c);
        c.weighty = 0; c.fill = GridBagConstraints.NONE; c.gridwidth = 1; c.gridy = 3;
        c.gridx = 0; p.add(new Button("Prev"), c); c.gridx = 1; p.add(new Button("Next"), c); c.gridx = 2; p.add(new Button("Reserve"), c);
        c.gridx = 0; c.gridy = 4; c.gridwidth = 4; c.fill = GridBagConstraints.HORIZONTAL; p.add(new Label("Status"), c);
        return p;
    }

    static Panel absolute() {                                 // null layout: positions are fixed numbers, resize() is manual
        Panel p = new Panel(null);
        Component[] list = {new Label("Title"), new Label("Search"), new TextField(15), new Choice(), new Button("Search"),
                new TextArea(5, 20), new Button("Prev"), new Button("Next"), new Button("Reserve"), new Label("Status")};
        for (Component c : list) p.add(c);
        return p;
    }

    static void absoluteResize(Panel p, int w, int h) {       // YOU must write the resize logic yourself
        Component[] c = p.getComponents();
        c[0].setBounds(0, 0, w, 25); c[1].setBounds(5, 30, 50, 25); c[2].setBounds(60, 30, 150, 25);
        c[3].setBounds(215, 30, 80, 25); c[4].setBounds(300, 30, 70, 25); c[5].setBounds(0, 60, w, h - 130);
        c[6].setBounds(5, h - 65, 60, 25); c[7].setBounds(70, h - 65, 60, 25); c[8].setBounds(135, h - 65, 70, 25); c[9].setBounds(0, h - 30, w, 25);
    }

    static int count(Container c) {
        int n = 1;
        for (Component x : c.getComponents()) if (x instanceof Container k) n += count(k);
        return n;
    }

    public static void main(String[] args) throws Exception {
        String[] names = {"nested BorderLayout+FlowLayout", "single GridBagLayout", "absolute (null layout)"};
        for (int d = 0; d < 3; d++) {
            Frame f = new Frame();
            Panel p = d == 0 ? nested() : d == 1 ? gridBag() : absolute();
            f.setLayout(new BorderLayout()); f.add(p, BorderLayout.CENTER);
            f.pack(); f.setVisible(true);
            int[][] sizes = {{600, 400}, {900, 600}, {400, 300}, {700, 500}};
            int rounds = 1000;
            for (int w = 0; w < 2; w++) {
                long t = System.nanoTime();
                for (int i = 0; i < rounds; i++) {
                    int[] s = sizes[i % sizes.length];
                    f.setSize(s[0], s[1]);
                    if (d == 2) absoluteResize(p, p.getWidth(), p.getHeight());
                    f.validate();
                }
                if (w == 1) System.out.printf("%-32s containers (incl. root panel) = %d | %d resize+layout passes = %.1f ms (%.3f ms each)%n",
                        names[d], count(p), rounds, (System.nanoTime() - t) / 1e6, (System.nanoTime() - t) / 1e6 / rounds);
            }
            f.dispose();
        }
    }
}
