import java.io.*;
import java.util.*;

/** Version 2: reads the OLD file hostel.ser that was written by version 1 */
public class Load {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("../hostel.ser"))) {
            List<Resident> list = (List<Resident>) in.readObject();
            for (Resident r : list) System.out.println("Loaded: " + r);
        } catch (InvalidClassException e) {
            System.out.println("InvalidClassException: " + e.getMessage());
        }
    }
}
