import java.sql.*;
import java.util.Properties;
import java.io.*;

/**
 * MySQL store (optional). Table:  fees(id INT AUTO_INCREMENT PRIMARY KEY, reg_no VARCHAR(12), amount DECIMAL(10,2))
 * Needs db.properties (url, user, password) and the MySQL driver jar on the classpath.
 */
public class JdbcPaymentStore implements PaymentStore {
    private final String url, user, password;

    public JdbcPaymentStore(File propertiesFile) throws IOException {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(propertiesFile)) { p.load(in); }
        url = p.getProperty("url"); user = p.getProperty("user"); password = p.getProperty("password", "");
    }

    @Override public int save(String regNo, double amount) throws FeeException {
        String sql = "INSERT INTO fees(reg_no, amount) VALUES (?, ?)";          // PreparedStatement: no injection
        try (Connection con = DriverManager.getConnection(url, user, password);  // always closed
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, regNo);
            ps.setDouble(2, amount);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { return keys.next() ? keys.getInt(1) : 0; }
        } catch (SQLException e) {
            throw new FeeException("Could not record the payment. Please try again.", e);   // checked exception handled and translated
        }
    }
}
