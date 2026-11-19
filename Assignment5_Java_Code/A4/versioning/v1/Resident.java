import java.io.Serializable;

public class Resident implements Serializable {
    // EXPERIMENT 2: remove the two slashes at the start of the next line (in BOTH v1 and v2)
    // private static final long serialVersionUID = 1L;

    String name;
    String regNo;

    Resident(String name, String regNo) { this.name = name; this.regNo = regNo; }
    @Override public String toString() { return name + " (" + regNo + ")"; }
}
