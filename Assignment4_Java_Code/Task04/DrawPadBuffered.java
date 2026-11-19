import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/** Alternative concept: dots are drawn ONCE onto an off-screen BufferedImage; paint() only copies that image. */
public class DrawPadBuffered extends Frame {
    private BufferedImage canvas = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_ARGB);   // width x height pixels
    private final Label status = new Label("Clicks: 0");
    private int count = 0;

    public DrawPadBuffered() {
        super("Draw Pad (BufferedImage)");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Graphics2D g = canvas.createGraphics();
                g.setColor(Color.BLACK);
                g.fillOval(e.getX() - 3, e.getY() - 3, 6, 6);        // drawn once, O(1) per click
                g.dispose();
                status.setText("Clicks: " + (++count));
                repaint();
            }
        });
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); } });
        setSize(400, 300);
    }

    @Override public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(canvas, 0, 0, null);                              // one image copy, independent of n
    }

    public static void main(String[] args) { new DrawPadBuffered().setVisible(true); }
}
