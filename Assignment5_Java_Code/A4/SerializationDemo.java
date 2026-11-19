import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/** A4.1 - object-graph serialization: Hostel -> Rooms -> Residents. Password and Connection are transient. */
public class SerializationDemo {

    static class Resident implements Serializable {
        private static final long serialVersionUID = 1L;
        String name; String regNo;
        transient String password;                 // never written to the file
        transient Connection connection;           // a live connection cannot (and must not) be serialized
        Resident(String n, String r, String p) { name = n; regNo = r; password = p; }
        @Override public String toString() { return name + "(" + regNo + ", password=" + password + ", connection=" + connection + ")"; }
    }
    static class Room implements Serializable {
        private static final long serialVersionUID = 1L;
        int number; List<Resident> residents = new ArrayList<>();
        Room(int n) { number = n; }
    }
    static class Hostel implements Serializable {
        private static final long serialVersionUID = 1L;
        String name; List<Room> rooms = new ArrayList<>();
        Hostel(String n) { name = n; }
    }

    public static void main(String[] args) throws Exception {
        Hostel h = new Hostel("Block A");
        Room r101 = new Room(101), r102 = new Room(102);
        Resident arun = new Resident("Arun", "192311001", "secret1");
        r101.residents.add(arun); r101.residents.add(new Resident("Divya", "192311002", "secret2"));
        r102.residents.add(arun);                                 // the SAME object in two rooms (shared reference)
        h.rooms.add(r101); h.rooms.add(r102);

        File f = File.createTempFile("hostel", ".ser");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) { out.writeObject(h); }
        System.out.println("Saved the whole graph with ONE writeObject call. File size = " + f.length() + " bytes");

        Hostel back;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) { back = (Hostel) in.readObject(); }
        System.out.println("Hostel " + back.name + ", rooms = " + back.rooms.size());
        for (Room r : back.rooms) for (Resident x : r.residents) System.out.println("  room " + r.number + ": " + x);
        System.out.println("Shared reference preserved (same object in both rooms)? "
                + (back.rooms.get(0).residents.get(0) == back.rooms.get(1).residents.get(0)));
        System.out.println("Original resident still has its password in memory: " + arun.password);

        // what happens if a field is NOT serializable and NOT transient
        class Bad implements Serializable { Object lock = new Object(); }
        try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) { out.writeObject(new Bad()); }
        catch (NotSerializableException e) { System.out.println("Without transient: NotSerializableException: " + e.getMessage()); }
        f.delete();
    }
}
