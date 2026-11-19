import java.sql.*;

/**
 * B2 - SQL injection. Use a LOCAL TEST database created only for this assignment (see schema.sql).
 * The program creates the users table and one user:  id = admin , password = S3cret!
 */
public class LoginDemo {

    /** UNSAFE: user input is pasted into the SQL text (Task A5.2). */
    static boolean unsafeLogin(Connection con, String id, String pwd) throws SQLException {
        String sql = "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + pwd + "'";
        System.out.println("      SQL built : " + sql);
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) { return rs.next(); }
    }

    /** SAFE: PreparedStatement - the input is only ever DATA, never SQL. */
    static boolean safeLogin(Connection con, String id, String pwd) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND pwd = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id); ps.setString(2, pwd);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public static void main(String[] args) throws Exception {
        try (Connection con = Db.connect()) {
            try (Statement st = con.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR(30) PRIMARY KEY, pwd VARCHAR(30) NOT NULL)");
                st.executeUpdate("DELETE FROM users");
                st.executeUpdate("INSERT INTO users(id, pwd) VALUES ('admin', 'S3cret!')");
            }
            String[][] tests = {
                // label, id, pwd, correct answer
                {"valid login",                 "admin",              "S3cret!",        "ACCEPT"},
                {"wrong password",              "admin",              "wrong",          "REJECT"},
                {"comment attack (id)",         "admin' -- ",         "anything",       "REJECT"},
                {"OR 1=1 attack (password)",    "admin",              "x' OR '1'='1",   "REJECT"},
                {"OR 1=1 attack (both fields)", "nobody' OR '1'='1",  "x' OR '1'='1",   "REJECT"},
            };
            System.out.printf("%-30s %-12s %-12s %s%n", "Test", "UNSAFE", "PREPARED", "Correct answer");
            for (String[] t : tests) {
                System.out.println(t[0]);
                boolean u = unsafeLogin(con, t[1], t[2]);
                boolean s = safeLogin(con, t[1], t[2]);
                System.out.printf("   result: %-12s %-12s %-8s %s%n", u ? "ACCEPTED" : "rejected", s ? "ACCEPTED" : "rejected", t[3],
                        (u ? "ACCEPT" : "REJECT").equals(t[3]) ? "" : "  <-- UNSAFE VERSION WRONG");
            }
        }
    }
}
