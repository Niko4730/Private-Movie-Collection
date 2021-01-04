package DAL.DB.IINTERFACE;

import java.sql.Connection;

public interface IDbConnectionProvider {

    /**
     * Gets the connection
     * @return the connection
     */
    Connection getConnection();

    /**
     * Gets the database
     * @return the database
     */
    String getDatabase();

    /**
     * Gets the host
     * @return the host
     */
    String getHost();

    /**
     * Gets the user
     * @return the user
     */
    String getUser();

    /**
     * Gets the password
     * @return the password
     */
    String getPassword();

    /**
     * Gets the port
     * @return the port
     */
    int getPort();

    /**
     * Connects to the database
     */
    void connect();

    /**
     * Changes the database
     * @param database the new database
     */
    void setDatabase(String database);

    /**
     * Sets the host
     * @param host the new host
     */
    void setHost(String host);

    /**
     * Sets the user
     * @param user the new user
     */
    void setUser(String user);

    /**
     * Sets the password
     * @param password the new password
     */
    void setPassword(String password);

    /**
     * Sets the port
     * @param port the new port
     */
    void setPort(int port);
}
