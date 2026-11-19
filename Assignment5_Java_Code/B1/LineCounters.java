import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * A2 / B1.3 - counting the lines of a big attendance log in different ways.
 *   java LineCounters              sizes 1, 10 and 50 MB
 *   java LineCounters 1 10         your own sizes in MB
 *   java LineCounters 1 10 50 skip-slow    skips the one-byte-at-a-time version for sizes above 10 MB
 */
public class LineCounters {
    /** one read() call per byte -> one system call per byte */
    static long unbuffered(Path f) throws IOException {
        long lines = 0;
        try (FileInputStream in = new FileInputStream(f.toFile())) { int b; while ((b = in.read()) != -1) if (b == '\n') lines++; }
        return lines;
    }
    /** BufferedInputStream: read() is still called per byte, but the OS is asked only once per 64 KB */
    static long bufferedStream(Path f) throws IOException {
        long lines = 0;
        try (InputStream in = new BufferedInputStream(new FileInputStream(f.toFile()), 65536)) { int b; while ((b = in.read()) != -1) if (b == '\n') lines++; }
        return lines;
    }
    /** own byte[] buffer: one read call per 64 KB */
    static long byteArray(Path f) throws IOException {
        long lines = 0; byte[] buf = new byte[65536];
        try (FileInputStream in = new FileInputStream(f.toFile())) { int n; while ((n = in.read(buf)) != -1) for (int i = 0; i < n; i++) if (buf[i] == '\n') lines++; }
        return lines;
    }
    static long bufferedReader(Path f) throws IOException {
        long lines = 0;
        try (BufferedReader r = Files.newBufferedReader(f)) { while (r.readLine() != null) lines++; }
        return lines;
    }
    /** whole file in memory */
    static long readAllLines(Path f) throws IOException {
        List<String> all = Files.readAllLines(f);
        return all.size();
    }

    static Path makeFile(int mb) throws IOException {
        Path p = Files.createTempFile("attendance" + mb + "mb", ".log");
        String line = "2026-01-05,192311001,PRESENT,Gate-3,Block-A,0123456789\n";       // 53 bytes
        long target = mb * 1024L * 1024L;
        try (BufferedWriter w = Files.newBufferedWriter(p)) { for (long written = 0; written < target; written += line.length()) w.write(line); }
        return p;
    }

    public static void main(String[] args) throws Exception {
        boolean skipSlow = false; java.util.List<Integer> sizes = new java.util.ArrayList<>();
        for (String a : args) { if (a.equals("skip-slow")) skipSlow = true; else sizes.add(Integer.parseInt(a)); }
        if (sizes.isEmpty()) { sizes.add(1); sizes.add(10); sizes.add(50); }

        for (int mb : sizes) {
            Path f = makeFile(mb);
            long bytes = Files.size(f);
            System.out.printf("%n=== %d MB file (%d bytes) -> unbuffered version makes about %d read() calls, a 64 KB buffer about %d%n", mb, bytes, bytes + 1, bytes / 65536 + 1);
            bufferedStream(f); byteArray(f);                                                // warm-up
            if (!(skipSlow && mb > 10)) { long t = System.nanoTime(); long n = unbuffered(f); System.out.printf("  FileInputStream.read() per byte : %9.1f ms  (%d lines)%n", (System.nanoTime() - t) / 1e6, n); }
            else System.out.println("  FileInputStream.read() per byte : skipped");
            long t = System.nanoTime(); long n = bufferedStream(f);  System.out.printf("  BufferedInputStream             : %9.1f ms  (%d lines)%n", (System.nanoTime() - t) / 1e6, n);
            t = System.nanoTime(); n = byteArray(f);                 System.out.printf("  own byte[65536] buffer          : %9.1f ms  (%d lines)%n", (System.nanoTime() - t) / 1e6, n);
            t = System.nanoTime(); n = bufferedReader(f);            System.out.printf("  BufferedReader.readLine()       : %9.1f ms  (%d lines)%n", (System.nanoTime() - t) / 1e6, n);

            Runtime rt = Runtime.getRuntime(); System.gc();
            long before = rt.totalMemory() - rt.freeMemory();
            t = System.nanoTime();
            List<String> all = Files.readAllLines(f);
            double ms = (System.nanoTime() - t) / 1e6;
            long used = rt.totalMemory() - rt.freeMemory() - before;
            System.out.printf("  Files.readAllLines()            : %9.1f ms  (%d lines, about %d MB of heap held; the buffered versions hold about 64 KB)%n", ms, all.size(), Math.max(0, used) / 1024 / 1024);
            all = null;
            Files.delete(f);
        }
    }
}
