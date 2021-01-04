package DAL.DB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Author: Carlo De Leon
 * Version: 1.0
 */
public class DbConnectionHandler implements DAL.DB.IINTERFACE.IDbConnectionProvider {

    protected DbMSSQLConnectionProvider mssqlConnectionProvider;
    protected DbMysqlConnectionProvider mysqlConnectionProvider;
    protected int connectionType;

    protected Properties databaseProperties = new Properties();
    protected static String databaseSettingsFile = "data/database.settings";
    private static DbConnectionHandler instance;

    /**
     * Initializes the connection
     */
    public DbConnectionHandler() {
        loadDbSettings();
    }

    /**
     * Loads the database settings
     */
    protected void loadDbSettings() {
        try {
            databaseProperties = new Properties();
            databaseProperties.load(new FileInputStream(new File(databaseSettingsFile)));

            String server = databaseProperties.getProperty("Server");
            String database = databaseProperties.getProperty("Database");
            String user = databaseProperties.getProperty("User");
            String password = databaseProperties.getProperty("Password");
            String port = databaseProperties.getProperty("Port");
            connectionType = Integer.parseInt(databaseProperties.getProperty("ConnectionType"));

            switch (connectionType) {
                case 0 -> {
                    mssqlConnectionProvider = new DbMSSQLConnectionProvider();
                    mssqlConnectionProvider.setHost(server);
                    mssqlConnectionProvider.setDatabase(database);
                    mssqlConnectionProvider.setUser(user);
                    mssqlConnectionProvider.setPassword(password);
                    mssqlConnectionProvider.setPort(Integer.parseInt(port));
                    mssqlConnectionProvider.connect();
                }
                case 1 -> {
                    mysqlConnectionProvider = new DbMysqlConnectionProvider();
                    mysqlConnectionProvider.setHost(server);
                    mysqlConnectionProvider.setDatabase(database);
                    mysqlConnectionProvider.setUser(user);
                    mysqlConnectionProvider.setPassword(password);
                    mysqlConnectionProvider.setPort(Integer.parseInt(port));
                    mysqlConnectionProvider.connect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to reconnect
     */
    protected void reconnect() {
        switch (connectionType) {
            case 0:
                try {
                    if (mssqlConnectionProvider != null && mssqlConnectionProvider.getConnection().isClosed())
                        mssqlConnectionProvider.connect();
                    break;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            case 1:
                try {
                    if (mysqlConnectionProvider != null && mysqlConnectionProvider.getConnection().isClosed())
                        mysqlConnectionProvider.connect();
                    break;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
        }
    }

    /**
     * Gets the database settings filepath
     * @return The path of the database settings file
     */
    public String getDatabaseSettingsFile() {
        return databaseSettingsFile;
    }

    /**
     * sets the database settings filepath
     * @param file the new filepath
     */
    public void setDatabaseSettingsFile(String file) {
        databaseSettingsFile = file;
    }

    /**
     * Tries to get a connection to the database
     * @return the connection
     */
    @Override
    public Connection getConnection() {
        reconnect();
        return switch (connectionType) {
            case 0 -> mssqlConnectionProvider.getConnection();
            case 1 -> mysqlConnectionProvider.getConnection();
            default -> null;
        };
    }

    /**
     * Tries to get the database filepath
     * @return the filepath
     */
    @Override
    public String getDatabase() {
        return switch (connectionType) {
            case 0 -> mssqlConnectionProvider.getDatabase();
            case 1 -> mysqlConnectionProvider.getDatabase();
            default -> null;
        };
    }

    /**
     * Gets the host
     * @return the host
     */
    @Override
    public String getHost() {
        return switch (connectionType) {
            case 0 -> mssqlConnectionProvider.getHost();
            case 1 -> mysqlConnectionProvider.getHost();
            default -> null;
        };
    }

    /**
     * Gets the user
     * @return the user
     */
    @Override
    public String getUser() {
        return switch (connectionType) {
            case 0 -> mssqlConnectionProvider.getUser();
            case 1 -> mysqlConnectionProvider.getUser();
            default -> null;
        };
    }

    /**
     * Gets the password
     * @return the password
     */
    @Override
    public String getPassword() {
        return switch (connectionType) {
            case 0 -> mssqlConnectionProvider.getPassword();
            case 1 -> mysqlConnectionProvider.getPassword();
            default -> null;
        };
    }

    /**
     * Gets the port
     * @return the port
     */
    @Override
    public int getPort() {
        return switch (connectionType) {
            case 0 -> mssqlConnectionProvider.getPort();
            case 1 -> mysqlConnectionProvider.getPort();
            default -> 0;
        };
    }

    /**
     * Tries to connect to the database
     */
    @Override
    public void connect() {
        switch (connectionType) {
            case 0 -> mssqlConnectionProvider.connect();
            case 1 -> mysqlConnectionProvider.connect();
        }
    }

    /**
     * Sets the database
     * @param database the new database
     */
    @Override
    public void setDatabase(String database) {
        switch (connectionType) {
            case 0 -> mssqlConnectionProvider.setDatabase(database);
            case 1 -> mysqlConnectionProvider.setDatabase(database);
        }
    }

    /**
     * Sets the host
     * @param host the new host
     */
    @Override
    public void setHost(String host) {
        switch (connectionType) {
            case 0 -> mssqlConnectionProvider.setHost(host);
            case 1 -> mysqlConnectionProvider.setHost(host);
        }
    }

    /**
     * Sets the user
     * @param user the new user
     */
    @Override
    public void setUser(String user) {
        switch (connectionType) {
            case 0 -> mssqlConnectionProvider.setUser(user);
            case 1 -> mysqlConnectionProvider.setUser(user);
        }
    }

    /**
     * Set the password
     * @param password the new password
     */
    @Override
    public void setPassword(String password) {
        switch (connectionType) {
            case 0 -> mssqlConnectionProvider.setPassword(password);
            case 1 -> mysqlConnectionProvider.setPassword(password);
        }
    }

    /**
     * sets the port
     * @param port the new port
     */
    @Override
    public void setPort(int port) {
        switch (connectionType) {
            case 0 -> mssqlConnectionProvider.setPort(port);
            case 1 -> mysqlConnectionProvider.setPort(port);
        }
    }

    /**
     * Gets the instance
     * @return the instance
     */
    public static DbConnectionHandler getInstance() {
        if (instance == null)
            instance = new DbConnectionHandler();
        return instance;
    }

    /**
     * Closes the connection if there is one
     */
    public void close() {
        try {
            switch (connectionType) {
                case 0 -> {
                    // Close open connection.
                    if (mssqlConnectionProvider.getConnection() != null)
                        mssqlConnectionProvider.getConnection().close();
                }
                case 1 -> {
                    // Close open connection.
                    if (mysqlConnectionProvider.getConnection() != null)
                        mysqlConnectionProvider.getConnection().close();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     * Gets the connection type
     * @return 0 if microsoft sql, 1 if my sql
     */
    public int getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the connection type
     * @param type 0 if microsoft sql, 1 if my sql
     */
    public void setConnectionType(int type) {
        // Close any open connection.
        close();
        connectionType = type;
        connect();
    }
}
