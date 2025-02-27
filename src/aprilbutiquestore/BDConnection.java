package aprilbutiquestore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class BDConnection {
    private static final String URL = "jdbc:oracle:thin:@a2d0jl1c68yo14eb_high?TNS_ADMIN=" + 
                                     System.getProperty("user.dir").replace("\\", "/") + "/wallet"; 
    private static String currentUser;
    private static String currentRole;
    private static String dbUser;
    private static char[] dbPassword;
    private static final String TEMP_USER = "USER"; // Usuario genérico
    private static final String TEMP_PASSWORD = "PASSWORD"; // Contraseña genérica
    
    public static Connection getTempConnection() throws SQLException {
        return DriverManager.getConnection(URL, TEMP_USER, TEMP_PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        if (dbUser == null || dbPassword == null) {
            throw new SQLException("Credenciales de DB no configuradas");
        }
        return DriverManager.getConnection(URL, dbUser, new String(dbPassword));
    }

    public static void setCredentials(String user, String role, String dbUser, char[] dbPassword) {
        currentUser = user;
        currentRole = role;
        BDConnection.dbUser = dbUser;
        BDConnection.dbPassword = Arrays.copyOf(dbPassword, dbPassword.length);
    }

    public static void clearCredentials() {
        currentUser = null;
        currentRole = null;
        dbUser = null;
        if (dbPassword != null) {
            Arrays.fill(dbPassword, '\0');
        }
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentRole() {
        return currentRole;
    }
}