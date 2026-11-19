import java.util.HashSet;
import java.util.Set;

/** Same program but WITHOUT hashCode() - run this first and record the output (expected: 3). */
public class AttendanceNoHash {
    public static void main(String[] args) {
        Set<BrokenStudent> present = new HashSet<>();
        present.add(new BrokenStudent("192311001", "Arun"));
        present.add(new BrokenStudent("192311002", "Divya"));
        present.add(new BrokenStudent("192311001", "Arun"));
        System.out.println("Unique students (no hashCode): " + present.size());
    }
}

class BrokenStudent {
    private final String regNo;
    private final String name;
    BrokenStudent(String regNo, String name) { this.regNo = regNo; this.name = name; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrokenStudent)) return false;
        return regNo.equals(((BrokenStudent) o).regNo);
    }
    // hashCode() NOT overridden
}
