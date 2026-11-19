import java.awt.*;
import java.awt.event.*;

/** The ORIGINAL faulty program. It compiles, but clicking does nothing. Run it and see. */
public class DrawPadBroken extends Frame {
    int count = 0;
    Label status = new Label("Clicks: 0");

    public DrawPadBroken() {
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            public void mouseclicked(MouseEvent e) {          // BUG: the method is mouseClicked (capital C)
                count++;
                Graphics g = getGraphics(); g.fillOval(e.getX(), e.getY(), 6, 6);
            }
        });
        setSize(400, 300); setVisible(true);
        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { System.exit(0); } });  // added only so you can close it
    }

    public static void main(String[] args) { new DrawPadBroken(); }
}
