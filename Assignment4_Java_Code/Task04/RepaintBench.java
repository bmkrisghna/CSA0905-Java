import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Time of one repaint for n clicks: redraw n stored points (O(n)) vs copy one BufferedImage (O(width x height)). */
public class RepaintBench {
    public static void main(String[] args) {
        int w = 1000, h = 800;
        BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);   // stands for the screen
        BufferedImage canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int n : new int[]{100, 1_000, 10_000, 100_000}) {
            List<Point> points = new ArrayList<>();
            Graphics2D cg = canvas.createGraphics();
            for (int i = 0; i < n; i++) { Point p = new Point((i * 37) % w, (i * 91) % h); points.add(p); cg.fillOval(p.x - 3, p.y - 3, 6, 6); }
            cg.dispose();

            int reps = Math.max(5, 20_000 / Math.max(1, n / 100));
            for (int warm = 0; warm < 2; warm++) {
                Graphics2D g = target.createGraphics();
                long t = System.nanoTime();
                for (int r = 0; r < reps; r++) for (Point p : points) g.fillOval(p.x - 3, p.y - 3, 6, 6);
                double pointsMs = (System.nanoTime() - t) / 1e6 / reps;
                t = System.nanoTime();
                for (int r = 0; r < reps; r++) g.drawImage(canvas, 0, 0, null);
                double imageMs = (System.nanoTime() - t) / 1e6 / reps;
                g.dispose();
                if (warm == 1) System.out.printf("n=%-7d redraw all points = %.4f ms | copy image = %.4f ms | memory: points ~ %d KB, image = %d KB%n",
                        n, pointsMs, imageMs, n * 24 / 1024, w * h * 4 / 1024);
            }
        }
    }
}
