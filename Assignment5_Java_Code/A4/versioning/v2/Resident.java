import java.io.Serializable;

public class Resident implements Serializable {
    // EXPERIMENT 2: remove the two slashes at the start of the next line (in BOTH v1 and v2)
    // private static final long serialVersionUID = 1L;

    String name;
    String regNo;
    String phone;                      // NEW FIELD added in version 2

    Resident(String name, String regNo, String phone) { this.name = name; this.regNo = regNo; this.phone = phone; }
    @Override public String toString() { return name + " (" + regNo + ", phone=" + phone + ")"; }
}
