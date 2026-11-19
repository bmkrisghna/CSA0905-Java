import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Task 3 - Campus library search screen (AWT, nested layouts).
 *
 *  Frame (BorderLayout)
 *   NORTH  : title Label
 *   CENTER : Panel (BorderLayout)
 *              NORTH  : search row  Panel (FlowLayout)  = Label + TextField + Choice + Button   <- keeps its height
 *              CENTER : results TextArea                                                       <- grows with the window
 *   SOUTH  : Panel (BorderLayout)
 *              NORTH  : button row Panel (FlowLayout)   = Prev, Next, Reserve                  <- keeps its height
 *              SOUTH  : status Label                                                           <- keeps its height
 *
 * Run:  java LibrarySearch            normal
 *       java LibrarySearch --shots    saves resize_600x400.png, resize_900x600.png, resize_400x300.png
 */
public class LibrarySearch extends Frame {
    record Book(String title, String category) { }

    private static final int PAGE_SIZE = 5;
    private final List<Book> catalog = List.of(
            new Book("Data Structures in Java", "Computer"), new Book("Operating System Concepts", "Computer"),
            new Book("Database System Concepts", "Computer"), new Book("Computer Networks", "Computer"),
            new Book("Java: The Complete Reference", "Computer"), new Book("Engineering Mathematics", "Maths"),
            new Book("Linear Algebra Done Right", "Maths"), new Book("Discrete Mathematics", "Maths"),
            new Book("Fundamentals of Physics", "Science"), new Book("Organic Chemistry", "Science"),
            new Book("Wings of Fire", "Biography"), new Book("The Story of My Experiments with Truth", "Biography"),
            new Book("Java Concurrency in Practice", "Computer"), new Book("Probability and Statistics", "Maths"));

    private final TextField query = new TextField(14);
    private final Choice category = new Choice();
    private final Button search = new Button("Search");
    private final Button prev = new Button("Prev");
    private final Button next = new Button("Next");
    private final Button reserve = new Button("Reserve");
    private final TextArea results = new TextArea("", 10, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
    private final Label status = new Label("Ready");
    private List<Book> found = new ArrayList<>();
    private int page = 0;

    public LibrarySearch() {
        super("Campus Library Search");
        setLayout(new BorderLayout());

        Label title = new Label("CAMPUS LIBRARY", Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        for (String c : new String[]{"All", "Computer", "Maths", "Science", "Biography"}) category.add(c);
        Panel searchRow = new Panel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new Label("Search:")); searchRow.add(query); searchRow.add(category); searchRow.add(search);
        results.setEditable(false);
        Panel centre = new Panel(new BorderLayout());
        centre.add(searchRow, BorderLayout.NORTH);
        centre.add(results, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);

        Panel buttonRow = new Panel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.add(prev); buttonRow.add(next); buttonRow.add(reserve);
        Panel bottom = new Panel(new BorderLayout());
        bottom.add(buttonRow, BorderLayout.NORTH);
        bottom.add(status, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        search.addActionListener(e -> doSearch());
        query.addActionListener(e -> doSearch());
        prev.addActionListener(e -> { if (page > 0) { page--; showPage(); } });
        next.addActionListener(e -> { if ((page + 1) * PAGE_SIZE < found.size()) { page++; showPage(); } });
        reserve.addActionListener(e -> doReserve());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
        });
        doSearch();
        setSize(600, 400);
    }

    private void doSearch() {
        String q = query.getText().trim().toLowerCase();
        String cat = category.getSelectedItem();
        found = new ArrayList<>();
        for (Book b : catalog)
            if (b.title().toLowerCase().contains(q) && (cat.equals("All") || b.category().equals(cat))) found.add(b);
        page = 0;
        showPage();
        status.setText(found.size() + " book(s) found");
    }

    private void showPage() {
        StringBuilder sb = new StringBuilder();
        int from = page * PAGE_SIZE, to = Math.min(from + PAGE_SIZE, found.size());
        for (int i = from; i < to; i++) sb.append(i + 1).append(". ").append(found.get(i).title()).append("  [").append(found.get(i).category()).append("]\n");
        if (found.isEmpty()) sb.append("No results.");
        results.setText(sb.toString());
        int pages = Math.max(1, (found.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        prev.setEnabled(page > 0);
        next.setEnabled(page + 1 < pages);
        status.setText("Page " + (page + 1) + " of " + pages + " - " + found.size() + " book(s)");
    }

    private void doReserve() {
        String sel = results.getSelectedText();
        if (sel == null || sel.isBlank()) { status.setText("Select a line in the results first"); return; }
        status.setText("Reserved: " + sel.trim());
    }

    /** Resizes the window to three sizes and saves a screenshot of each. */
    private static void takeShots(LibrarySearch f) throws Exception {
        Robot robot = new Robot();
        int[][] sizes = {{600, 400}, {900, 600}, {400, 300}};
        for (int[] s : sizes) {
            f.setSize(s[0], s[1]);
            f.validate();
            Thread.sleep(700);
            BufferedImage img = robot.createScreenCapture(f.getBounds());
            File out = new File("resize_" + s[0] + "x" + s[1] + ".png");
            ImageIO.write(img, "png", out);
            System.out.println("saved " + out.getName() + "  (results area: " + f.results.getWidth() + " x " + f.results.getHeight()
                    + ", search row height stays " + f.search.getHeight() + ")");
        }
        f.dispose();
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        LibrarySearch f = new LibrarySearch();
        f.setLocation(50, 50);
        f.setVisible(true);
        if (args.length > 0 && args[0].equals("--shots")) takeShots(f);
    }
}
