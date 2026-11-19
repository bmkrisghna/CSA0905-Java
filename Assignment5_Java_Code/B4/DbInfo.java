import java.sql.*;

/** B4.2 - DatabaseMetaData: product, driver and the tables present. */
public class DbInfo {
    public static void main(String[] args) throws Exception {
        try (Connection con = Db.connect()) {
            DatabaseMetaData md = con.getMetaData();
            System.out.println("Database product : " + md.getDatabaseProductName() + " " + md.getDatabaseProductVersion());
            System.out.println("Driver           : " + md.getDriverName() + " " + md.getDriverVersion());
            System.out.println("JDBC version     : " + md.getJDBCMajorVersion() + "." + md.getJDBCMinorVersion());
            System.out.println("URL              : " + md.getURL());
            System.out.println("User             : " + md.getUserName());
            System.out.println("Supports transactions: " + md.supportsTransactions() + " | batch updates: " + md.supportsBatchUpdates());
            System.out.println("Tables in catalog '" + con.getCatalog() + "':");
            try (ResultSet rs = md.getTables(con.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String table = rs.getString("TABLE_NAME");
                    System.out.println("   " + table);
                    try (ResultSet cols = md.getColumns(con.getCatalog(), null, table, "%")) {
                        while (cols.next()) System.out.println("        " + cols.getString("COLUMN_NAME") + "  " + cols.getString("TYPE_NAME") + "(" + cols.getInt("COLUMN_SIZE") + ")");
                    }
                }
            }
        }
    }
}
