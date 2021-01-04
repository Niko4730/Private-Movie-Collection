package DAL.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Author: Carlo De Leon
 * Version: 1.0
 */
public class DbMysqlConnectionProvider implements DAL.DB.IINTERFACE.IDbConnectionProvider {

    protected String host;
    protected String user;
    protected String password;
    protected String database;
    protected int port = 3306;

    protected Connection connection;

    public DbMysqlConnectionProvider() {
    }

    /**
     * Tries to connect to the database
     */
    @Override
    public void connect() {
        try {
            // Connect to the database.
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s", getHost(), getPort(), getDatabase()), getUser(), getPassword());
        } catch (SQLException e) {
            System.out.println(String.format("MySQL connect exception: %s", e.getMessage()));
        }
    }


    /**
     * Gets the connection
     * @return the connection
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * Gets the databse
     * @return the database
     */
    @Override
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the host
     * @return the host
     */
    @Override
    public String getHost() {
        return host;
    }

    /**
     * Gets the user
     * @return the user
     */
    @Override
    public String getUser() {
        return user;
    }

    /**
     * Gets the password
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Gets the port
     * @return the port
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * Sets the database
     * @param database the new database
     */
    @Override
    public void setDatabase(String database) {
        if (database.isEmpty()) return;
        this.database = database;
    }

    /**
     * Sets the host
     * @param host the new host
     */
    @Override
    public void setHost(String host) {
        if (host.isEmpty()) return;
        this.host = host;
    }

    /**
     * Sets the user
     * @param user the new user
     */
    @Override
    public void setUser(String user) {
        if (user.isEmpty()) return;
        this.user = user;
    }

    /**
     * Sets the password
     * @param password the new password
     */
    @Override
    public void setPassword(String password) {
        if (password.isEmpty()) return;
        this.password = password;
    }

    /**
     * Sets the port
     * @param port the new port
     */
    @Override
    public void setPort(int port) {
        if (port <= 0) return;
        this.port = port;
    }
}
