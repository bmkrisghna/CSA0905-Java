import java.io.*;
import java.sql.*;
import java.util.Properties;

/** Small helper used by the JDBC programs: reads db.properties (this folder or the parent folder) and connects. */
public class Db {
    public static Connection connect() throws Exception {
        String custom = System.getProperty("db.config");
        File[] places = custom != null ? new File[]{new File(custom)} : new File[]{new File("db.properties"), new File("../db.properties")};
        for (File f : places) {
            if (f.isFile()) {
                Properties p = new Properties();
                try (InputStream in = new FileInputStream(f)) { p.load(in); }
                return DriverManager.getConnection(p.getProperty("url"), p.getProperty("user"), p.getProperty("password", ""));
            }
        }
        throw new FileNotFoundException("db.properties not found (looked in this folder and in the parent folder)");
    }

    /** Creates the bus_pass table if it does not exist (same definition as schema.sql). */
    public static void createBusPassTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS bus_pass (pass_id VARCHAR(10) PRIMARY KEY, reg_no VARCHAR(12) NOT NULL, "
                     + "name VARCHAR(60) NOT NULL, route_no INT NOT NULL, amount DECIMAL(10,2) NOT NULL)");
        }
    }
}
