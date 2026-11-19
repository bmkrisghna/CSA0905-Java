import java.io.*;
import java.time.LocalDate;

/**
 * A1.1(b) Binary record written with DataOutputStream. Field order and types:
 *   writeUTF(passId)  writeUTF(regNo)  writeUTF(name)  writeInt(route)  writeUTF(stop)  writeLong(validUntil epoch-day)  writeDouble(amount)
 * writeUTF stores a 2-byte length first, so a name containing a comma (or any character) needs no escaping.
 */
public class BinaryPassFile {
    public static void main(String[] args) throws IOException {
        File f = File.createTempFile("passes", ".bin");
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)))) {
            write(out, "P1001", "192311001", "Arun, K.", 12, "Main Gate", LocalDate.of(2027, 3, 31), 4500);
            write(out, "P1002", "192311002", "Divya", 7, "Temple Road", LocalDate.of(2027, 3, 31), 3800);
        }
        System.out.println("File size = " + f.length() + " bytes for 2 records");
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            while (true) {
                try {
                    String id = in.readUTF(), reg = in.readUTF(), name = in.readUTF();
                    int route = in.readInt();
                    String stop = in.readUTF();
                    LocalDate valid = LocalDate.ofEpochDay(in.readLong());
                    double amount = in.readDouble();
                    System.out.println(id + " | " + reg + " | " + name + " | route " + route + " | " + stop + " | " + valid + " | " + amount);
                } catch (EOFException e) { break; }          // end of file reached normally
            }
        }
        f.delete();
    }

    static void write(DataOutputStream out, String id, String reg, String name, int route, String stop, LocalDate valid, double amount) throws IOException {
        out.writeUTF(id); out.writeUTF(reg); out.writeUTF(name); out.writeInt(route);
        out.writeUTF(stop); out.writeLong(valid.toEpochDay()); out.writeDouble(amount);
    }
}
