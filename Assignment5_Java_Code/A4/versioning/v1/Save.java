import java.io.*;
import java.util.*;

/** Version 1: saves residents WITHOUT a phone number into hostel.ser */
public class Save {
    public static void main(String[] args) throws Exception {
        List<Resident> list = new ArrayList<>(List.of(new Resident("Arun", "192311001"), new Resident("Divya", "192311002")));
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("../hostel.ser"))) { out.writeObject(list); }
        System.out.println("Saved " + list.size() + " residents (version 1) to ../hostel.ser");
    }
}
