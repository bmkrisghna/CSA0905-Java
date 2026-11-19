import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Alternative concept: Java serialization vs CSV text for n residents - file size, save time, load time. */
public class SerializationVsCsv {
    static class Resident implements Serializable {
        private static final long serialVersionUID = 1L;
        String name, regNo;
        Resident(String n, String r) { name = n; regNo = r; }
    }

    public static void main(String[] args) throws Exception {
        for (int n : new int[]{1_000, 10_000, 100_000}) {
            List<Resident> list = new ArrayList<>();
            for (int i = 0; i < n; i++) list.add(new Resident("Student " + i, String.format("%09d", i)));
            Path ser = Files.createTempFile("res", ".ser"), csv = Files.createTempFile("res", ".csv");
            double sSave = 0, sLoad = 0, cSave = 0, cLoad = 0;
            for (int w = 0; w < 3; w++) {                                    // 1 warm-up + 2 measured
                long t = System.nanoTime();
                try (ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(ser)))) { o.writeObject(list); }
                double a = (System.nanoTime() - t) / 1e6;
                t = System.nanoTime();
                try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(ser)))) { in.readObject(); }
                double b = (System.nanoTime() - t) / 1e6;
                t = System.nanoTime();
                try (BufferedWriter wr = Files.newBufferedWriter(csv)) { for (Resident r : list) { wr.write(r.regNo + "," + r.name); wr.newLine(); } }
                double c = (System.nanoTime() - t) / 1e6;
                t = System.nanoTime();
                List<Resident> back = new ArrayList<>();
                try (BufferedReader rd = Files.newBufferedReader(csv)) { String l; while ((l = rd.readLine()) != null) { String[] p = l.split(","); back.add(new Resident(p[1], p[0])); } }
                double d = (System.nanoTime() - t) / 1e6;
                if (w > 0) { sSave += a / 2; sLoad += b / 2; cSave += c / 2; cLoad += d / 2; }
            }
            System.out.printf("n=%-7d serialization: %6d KB save %.1f ms load %.1f ms | CSV: %6d KB save %.1f ms load %.1f ms%n",
                    n, Files.size(ser) / 1024, sSave, sLoad, Files.size(csv) / 1024, cSave, cLoad);
            Files.delete(ser); Files.delete(csv);
        }
        System.out.println("New field: CSV - old lines simply have fewer columns (you decide a default). Serialization - depends on serialVersionUID.");
    }
}
