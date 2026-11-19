import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Alternative concept: RandomAccessFile with FIXED-LENGTH records.
 * Record = passId 10 bytes + regNo 12 + name 30 + route 4 + amount 8 = 64 bytes.
 * Pass "P<number>" is stored at record index (number - 1001), so find = seek(index * 64) = O(1).
 */
public class RandomAccessPassFile {
    static final int ID = 10, REG = 12, NAME = 30, SIZE = ID + REG + NAME + 4 + 8;

    static byte[] pad(String s, int len) {
        byte[] b = new byte[len];
        byte[] src = s.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(src, 0, b, 0, Math.min(src.length, len));       // longer text is cut, shorter text is padded with 0
        return b;
    }
    static String text(byte[] b, int from, int len) {
        int end = from; while (end < from + len && b[end] != 0) end++;
        return new String(b, from, end - from, StandardCharsets.UTF_8);
    }

    static void write(RandomAccessFile raf, long index, String id, String reg, String name, int route, double amount) throws IOException {
        raf.seek(index * SIZE);
        raf.write(pad(id, ID)); raf.write(pad(reg, REG)); raf.write(pad(name, NAME)); raf.writeInt(route); raf.writeDouble(amount);
    }

    static String[] read(RandomAccessFile raf, long index) throws IOException {
        if (index < 0 || (index + 1) * SIZE > raf.length()) return null;
        raf.seek(index * SIZE);
        byte[] b = new byte[ID + REG + NAME];
        raf.readFully(b);
        return new String[]{text(b, 0, ID), text(b, ID, REG), text(b, ID + REG, NAME), String.valueOf(raf.readInt()), String.valueOf(raf.readDouble())};
    }

    public static void main(String[] args) throws IOException {
        int n = 100_000;
        Path p = Files.createTempFile("passes", ".dat");
        long usedBytes = 0;
        try (RandomAccessFile raf = new RandomAccessFile(p.toFile(), "rw")) {
            for (int i = 0; i < n; i++) {
                String name = "Student" + i;
                usedBytes += ("P" + (1001 + i)).length() + 9 + name.length() + 12;
                write(raf, i, "P" + (1001 + i), String.format("%09d", i), name, 1 + i % 40, 3000 + i % 2000);
            }
        }
        System.out.println("File size: " + Files.size(p) + " bytes (" + n + " records x " + SIZE + " bytes)");
        System.out.printf("Padding wasted: %d bytes = %.0f%% of the file%n", Files.size(p) - usedBytes, 100.0 * (Files.size(p) - usedBytes) / Files.size(p));

        try (RandomAccessFile raf = new RandomAccessFile(p.toFile(), "r")) {
            String target = "P" + (1001 + n - 1);
            int q = 200;
            for (int w = 0; w < 2; w++) {
                long t = System.nanoTime();
                String[] found = null;
                for (int i = 0; i < q; i++) found = read(raf, (Long.parseLong(target.substring(1)) - 1001));      // O(1) seek
                double seekMs = (System.nanoTime() - t) / 1e6 / q;

                t = System.nanoTime();
                for (int i = 0; i < 5; i++) {                                                                      // O(n) scan of the same file
                    for (long idx = 0; ; idx++) { String[] r = read(raf, idx); if (r == null || r[0].equals(target)) break; }
                }
                double scanMs = (System.nanoTime() - t) / 1e6 / 5;
                if (w == 1) System.out.printf("find %s: seek (O(1)) = %.4f ms | scan all records (O(n)) = %.2f ms | found = %s%n",
                        target, seekMs, scanMs, java.util.Arrays.toString(found));
            }
        }
        Files.delete(p);
    }
}
