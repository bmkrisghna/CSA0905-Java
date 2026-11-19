import java.util.*;

/** Three designed tests. Write your PREDICTION before running each one. */
public class HashSetTests {
    public static void main(String[] args) {
        // Test A: all different students -> looks fine even without hashCode
        Set<BrokenStudent2> a = new HashSet<>();
        a.add(new BrokenStudent2("1")); a.add(new BrokenStudent2("2")); a.add(new BrokenStudent2("3"));
        System.out.println("Test A (3 different, no hashCode)      size = " + a.size());

        // Test B: same student scanned twice -> duplicate appears without hashCode
        Set<BrokenStudent2> b = new HashSet<>();
        b.add(new BrokenStudent2("1")); b.add(new BrokenStudent2("1"));
        System.out.println("Test B (same twice, no hashCode)       size = " + b.size());

        // Test C: mutate a field used in hashCode AFTER adding
        Set<GoodStudent> c = new HashSet<>();
        GoodStudent s = new GoodStudent("1");
        c.add(s);
        s.regNo = "999";                                     // changed after insertion
        System.out.println("Test C contains(s) after change    = " + c.contains(s) + "   (size still " + c.size() + ")");

        // Correct class: duplicates removed
        Set<GoodStudent> d = new HashSet<>();
        d.add(new GoodStudent("1")); d.add(new GoodStudent("1"));
        System.out.println("Correct class (same twice)             size = " + d.size());

        // contract check
        System.out.println("equal objects -> equal hash codes ? " +
                (new GoodStudent("7").hashCode() == new GoodStudent("7").hashCode()));
    }
}

class BrokenStudent2 {
    String regNo;
    BrokenStudent2(String r) { regNo = r; }
    @Override public boolean equals(Object o) { return o instanceof BrokenStudent2 x && regNo.equals(x.regNo); }
}

class GoodStudent {
    String regNo;                       // NOT final here on purpose, to show Test C
    GoodStudent(String r) { regNo = r; }
    @Override public boolean equals(Object o) { return o instanceof GoodStudent x && regNo.equals(x.regNo); }
    @Override public int hashCode() { return Objects.hash(regNo); }
}
