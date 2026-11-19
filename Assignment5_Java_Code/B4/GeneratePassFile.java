import java.io.*;
import java.nio.file.*;

/** Creates a big pass file for testing the migration.  java GeneratePassFile 100000 passes_big.csv  (every 50th line is damaged) */
public class GeneratePassFile {
    public static void main(String[] args) throws IOException {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 100_000;
        String out = args.length > 1 ? args[1] : "passes_big.csv";
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(out))) {
            for (int i = 0; i < n; i++) {
                String line = "P" + (100000 + i) + "," + String.format("%09d", i) + ",Student" + i + "," + (1 + i % 40) + "," + (3000 + i % 2000);
                if (i % 50 == 49) line = "P" + (100000 + i) + ",DAMAGED";
                w.write(line); w.newLine();
            }
        }
        System.out.println("Wrote " + n + " lines to " + out);
    }
}
